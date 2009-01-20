/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.text.Element;
import javax.swing.text.View;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobocodeViewFactory implements javax.swing.text.ViewFactory {

	/**
	 * Creates a view from the given structural element of a
	 * document.
	 *
	 * @param elem the piece of the document to build a view of
	 * @return the view
	 * @see View
	 */
	public View create(Element elem) {
		return new RobocodeView(elem);
	}
}
