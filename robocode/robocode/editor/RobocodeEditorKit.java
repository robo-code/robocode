/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.editor;


import javax.swing.*;
import javax.swing.text.*;
import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobocodeEditorKit extends DefaultEditorKit {
	public EditWindow editWindow;

	/**
	 * RobocodeEditorKit constructor
	 */
	public RobocodeEditorKit() {
		super();
	}

	/**
	 * Creates a copy of the editor kit.  This
	 * allows an implementation to serve as a prototype
	 * for others, so that they can be quickly created.
	 * <em>In a future release this method will be implemented
	 * to use Object.clone</em>
	 *
	 * @return the copy
	 */
	public Object clone() {
		return super.clone();
	}

	/**
	 * Fetches a caret that can navigate through views
	 * produced by the associated ViewFactory.
	 *
	 * @return the caret
	 */
	public javax.swing.text.Caret createCaret() {
		return super.createCaret();
	}

	/**
	 * Creates an uninitialized text storage model
	 * that is appropriate for this type of editor.
	 *
	 * @return the model
	 */
	public javax.swing.text.Document createDefaultDocument() {

		JavaDocument doc = new JavaDocument();

		doc.setEditWindow(editWindow);
		return doc;
	}

	/**
	 * Fetches the set of commands that can be used
	 * on a text component that is using a model and
	 * view produced by this kit.
	 *
	 * @return the set of actions
	 */
	public Action[] getActions() {
		return super.getActions();
	}

	/**
	 * Gets the MIME type of the data that this
	 * kit represents support for.
	 *
	 * @return the type
	 */
	public String getContentType() {
		return "text/java";
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 5:05:58 PM)
	 * @return robocode.editor.EditWindow
	 */
	public EditWindow getEditWindow() {
		return editWindow;
	}

	/**
	 * Fetches a factory that is suitable for producing 
	 * views of any models that are produced by this
	 * kit.  
	 *
	 * @return the factory
	 */
	public ViewFactory getViewFactory() {
		return new RobocodeViewFactory();
	}

	/**
	 * Inserts content from the given stream which is expected 
	 * to be in a format appropriate for this kind of content
	 * handler.
	 * 
	 * @param in  The stream to read from
	 * @param doc The destination for the insertion.
	 * @param pos The location in the document to place the
	 *   content >= 0.
	 * @exception IOException on any I/O error
	 * @exception BadLocationException if pos represents an invalid
	 *   location within the document.
	 */
	public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
		super.read(in, doc, pos);
	}

	/**
	 * Inserts content from the given stream which is expected 
	 * to be in a format appropriate for this kind of content
	 * handler.
	 * <p>
	 * Since actual text editing is unicode based, this would 
	 * generally be the preferred way to read in the data.  
	 * Some types of content are stored in an 8-bit form however,
	 * and will favor the InputStream.
	 * 
	 * @param in  The stream to read from
	 * @param doc The destination for the insertion.
	 * @param pos The location in the document to place the
	 *   content >= 0.
	 * @exception IOException on any I/O error
	 * @exception BadLocationException if pos represents an invalid
	 *   location within the document.
	 */
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
		super.read(in, doc, pos);
	}

	public void setEditWindow(EditWindow newEditWindow) {
		editWindow = newEditWindow;
	}

	/**
	 * Writes content from a document to the given stream
	 * in a format appropriate for this kind of content handler.
	 * 
	 * @param out  The stream to write to
	 * @param doc The source for the write.
	 * @param pos The location in the document to fetch the
	 *   content from >= 0.
	 * @param len The amount to write out >= 0.
	 * @exception IOException on any I/O error
	 * @exception BadLocationException if pos represents an invalid
	 *   location within the document.
	 */
	public void write(java.io.OutputStream out, javax.swing.text.Document doc, int pos, int len) throws java.io.IOException, javax.swing.text.BadLocationException {
		super.write(out, doc, pos, len);
	}

	/**
	 * Writes content from a document to the given stream
	 * in a format appropriate for this kind of content handler.
	 * <p>
	 * Since actual text editing is unicode based, this would 
	 * generally be the preferred way to write the data.  
	 * Some types of content are stored in an 8-bit form however,
	 * and will favor the OutputStream.
	 * 
	 * @param out  The stream to write to
	 * @param doc The source for the write.
	 * @param pos The location in the document to fetch the
	 *   content >= 0.
	 * @param len The amount to write out >= 0.
	 * @exception IOException on any I/O error
	 * @exception BadLocationException if pos represents an invalid
	 *   location within the document.
	 */
	public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
		super.write(out, doc, pos, len);
	}
}
