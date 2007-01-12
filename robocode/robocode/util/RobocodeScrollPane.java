/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.util;


import java.awt.*;
import javax.swing.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeScrollPane extends JScrollPane implements java.beans.PropertyChangeListener {

	public RobocodeScrollPane(Component view) {
		super(view);
		addPropertyChangeListener(this);
	}

	public JScrollBar createHorizontalScrollBar() {
		return super.createHorizontalScrollBar();
	}

	public Dimension getMaximumSize() {
		return super.getMaximumSize();
	}

	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	public void propertyChange(java.beans.PropertyChangeEvent e) {}
}
