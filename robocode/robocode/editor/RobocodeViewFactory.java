/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


/**
 * Insert the type's description here.
 * Creation date: (4/4/2001 3:17:02 PM)
 * @author: Mathew A. Nelson
 */
public class RobocodeViewFactory implements javax.swing.text.ViewFactory {

	/**
	 * Creates a view from the given structural element of a
	 * document.
	 *
	 * @param elem  the piece of the document to build a view of
	 * @return the view
	 * @see View
	 */
	public javax.swing.text.View create(javax.swing.text.Element elem) {
		return new RobocodeView(elem);
	}
}
