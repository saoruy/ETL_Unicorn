/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.LYG.GUI.platForm;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.tree.TreePath;
/**
 * The metal look and feel implementation of <code>TreeUI</code>.
 * <p>
 * <code>MetalTreeUI</code> allows for configuring how to
 * visually render the spacing and delineation between nodes. The following
 * hints are supported:
 *
 * <table summary="Descriptions of supported hints: Angled, Horizontal, and None">
 *  <tr>
 *    <th><p align="left">Angled</p></th>
 *    <td>A line is drawn connecting the child to the parent. For handling
 *          of the root node refer to
 *          {@link javax.swing.JTree#setRootVisible} and
 *          {@link javax.swing.JTree#setShowsRootHandles}.
 *    </td>
 *  </tr>
 *  <tr>
 *     <th><p align="left">Horizontal</p></th>
 *     <td>A horizontal line is drawn dividing the children of the root node.</td>
 *  </tr>
 *  <tr>
 *      <th><p align="left">None</p></th>
 *      <td>Do not draw any visual indication between nodes.</td>
 *  </tr>
 * </table>
 *
 * <p>
 * As it is typically impratical to obtain the <code>TreeUI</code> from
 * the <code>JTree</code> and cast to an instance of <code>MetalTreeUI</code>
 * you enable this property via the client property
 * <code>JTree.lineStyle</code>. For example, to switch to
 * <code>Horizontal</code> style you would do:
 * <code>tree.putClientProperty("JTree.lineStyle", "Horizontal");</code>
 * <p>
 * The default is <code>Angled</code>.
 */
public class UnicornTreeUI extends BasicTreeUI {
	private static Color lineColor;
	private static final String LINE_STYLE = "JTree.lineStyle";
	private static final String LEG_LINE_STYLE_STRING = "Angled";
	private static final String HORIZ_STYLE_STRING = "Horizontal";
	private static final String NO_STYLE_STRING = "None";
	private static final int LEG_LINE_STYLE = 2; 
	private static final int HORIZ_LINE_STYLE = 1;
	private static final int NO_LINE_STYLE = 0;
	private int lineStyle = LEG_LINE_STYLE;
	private PropertyChangeListener lineStyleListener = new LineListener();
	// Boilerplate
	public static ComponentUI createUI(JComponent x) {
		return new UnicornTreeUI();
	}
	public UnicornTreeUI(){
		super();
	}

	protected int getHorizontalLegBuffer(){
		return 3;
	} 

	public void installUI( JComponent c ) {
		super.installUI( c );
		lineColor = UIManager.getColor( "Tree.line" );

		Object lineStyleFlag = c.getClientProperty( LINE_STYLE );
		decodeLineStyle(lineStyleFlag);
		c.addPropertyChangeListener(lineStyleListener);

	}

	public void uninstallUI( JComponent c)  {
		c.removePropertyChangeListener(lineStyleListener);
		super.uninstallUI(c);
	}

	/** this function converts between the string passed into the client property
	 * and the internal representation (currently and int)
	 *
	 */
	protected void decodeLineStyle(Object lineStyleFlag) {
		if ( lineStyleFlag == null ||lineStyleFlag.equals(LEG_LINE_STYLE_STRING)){
			lineStyle = LEG_LINE_STYLE; // default case
		} else {
			if ( lineStyleFlag.equals(NO_STYLE_STRING) ) {
				lineStyle = NO_LINE_STYLE;
			} else if ( lineStyleFlag.equals(HORIZ_STYLE_STRING) ) {
				lineStyle = HORIZ_LINE_STYLE;
			}
		}
	}

	protected boolean isLocationInExpandControl(int row, int rowLevel,int mouseX, int mouseY) {
		if(tree != null && !isLeaf(row)) {
			int boxWidth;

			if(getExpandedIcon() != null)
				boxWidth = getExpandedIcon().getIconWidth() + 6;
			else
				boxWidth = 8;

			Insets i = tree.getInsets();
			int boxLeftX = (i != null) ? i.left : 0;
			boxLeftX += (((rowLevel + depthOffset - 1) * totalChildIndent) + getLeftChildIndent()) - boxWidth/2;
			int boxRightX = boxLeftX + boxWidth;
			return mouseX >= boxLeftX && mouseX <= boxRightX;
		}
		return false;
	}
	public void paint(Graphics g, JComponent c)  {
		super.paint( g, c );
		// Paint the lines
		if (lineStyle == HORIZ_LINE_STYLE && !largeModel)  {
			paintHorizontalSeparators(g,c);
		}
	}

	protected void paintHorizontalSeparators(Graphics g, JComponent c)  {
		g.setColor( lineColor );
		Rectangle clipBounds = g.getClipBounds();
		int beginRow = getRowForPath(tree, getClosestPathForLocation(tree, 0, clipBounds.y));
		int endRow = getRowForPath(tree, getClosestPathForLocation(tree, 0, clipBounds.y + clipBounds.height - 1));
		if ( beginRow <= -1 || endRow <= -1 )  {
			return;
		}
		for ( int i = beginRow; i <= endRow; ++i )  {
			TreePath path = getPathForRow(tree, i);	
			if(path != null && path.getPathCount() == 2) {
				Rectangle rowBounds = getPathBounds(tree,getPathForRow(tree, i));
				// Draw a line at the top
				if(rowBounds != null){
					g.setColor(Color.black);
					g.drawLine(clipBounds.x, rowBounds.y,clipBounds.x + clipBounds.width, rowBounds.y);

					g.setColor(Color.white);
					g.drawLine(clipBounds.x, rowBounds.y+1,clipBounds.x + clipBounds.width, rowBounds.y+1);

					g.setColor(Color.orange);
					g.drawLine(clipBounds.x, rowBounds.y+2,clipBounds.x + clipBounds.width, rowBounds.y+2);
				}
			}
		}
	}
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,Insets insets, TreePath path)  {
		if (lineStyle == LEG_LINE_STYLE) {
			super.paintVerticalPartOfLeg(g, clipBounds, insets, path);
		}
	}
	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds,
			TreePath path, int row,
			boolean isExpanded,
			boolean hasBeenExpanded, boolean
			isLeaf) {
		if (lineStyle == LEG_LINE_STYLE) {
			super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds,
					path, row, isExpanded,
					hasBeenExpanded, isLeaf);
		}
	}
	/** This class listens for changes in line style */
	class LineListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			String name = e.getPropertyName();
			if ( name.equals( LINE_STYLE ) ) {
				decodeLineStyle(e.getNewValue());
			}
		}
	} // end class PaletteListener
}
