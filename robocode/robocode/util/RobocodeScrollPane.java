/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.util;

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (10/26/2001 2:02:59 PM)
 * @author: Administrator
 */
public class RobocodeScrollPane extends javax.swing.JScrollPane implements java.beans.PropertyChangeListener {
/**
 * RobocodeScrollPane constructor comment.
 * @param view java.awt.Component
 */
public RobocodeScrollPane(java.awt.Component view) {
	super(view);
	addPropertyChangeListener(this);
}
	public JScrollBar createHorizontalScrollBar() {
//		System.out.println("Creating hscrollbar.");
		return super.createHorizontalScrollBar();
	}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 2:04:33 PM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMaximumSize() {
//	System.out.println("getMaxSize... pref is: " + super.getPreferredSize() + " hs: " + getHorizontalScrollBar());
	return super.getMaximumSize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 2:04:33 PM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
//	System.out.println("getPrefSize... pref is: " + super.getPreferredSize() + " hs: " + getHorizontalScrollBar());
	return super.getPreferredSize();
}
	public void propertyChange(java.beans.PropertyChangeEvent e)
	{
//		System.out.println("property changed: " + e.getPropertyName());
	}
}
