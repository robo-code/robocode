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


import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.ViewFactory;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial")
public class RobocodeEditorKit extends DefaultEditorKit {

	private EditWindow editWindow;

	@Override
	public Document createDefaultDocument() {
		JavaDocument doc = new JavaDocument();

		doc.setEditWindow(editWindow);
		return doc;
	}

	@Override
	public String getContentType() {
		return "text/java";
	}

	@Override
	public ViewFactory getViewFactory() {
		return new RobocodeViewFactory();
	}

	public void setEditWindow(EditWindow newEditWindow) {
		editWindow = newEditWindow;
	}

	public EditWindow getEditWindow() {
		return editWindow;
	}
}
