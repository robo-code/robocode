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


import javax.swing.text.*;
import javax.swing.event.*;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (4/5/2001 2:51:34 PM)
 * @author: Mathew A. Nelson
 */
public class JavaDocument extends PlainDocument {
	public boolean needsRedraw = false;
	public EditWindow editWindow = null;
	private boolean editing = false;

	/**
	 * JavaDocument constructor comment.
	 */
	public JavaDocument() {
		super();
	}

	/**
	 * JavaDocument constructor comment.
	 * @param c javax.swing.text.AbstractDocument.Content
	 */
	protected JavaDocument(javax.swing.text.AbstractDocument.Content c) {
		super(c);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:51:26 PM)
	 * @return robocode.editor.EditWindow
	 */
	public EditWindow getEditWindow() {
		return editWindow;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 6:45:39 PM)
	 * @param offs int
	 * @param str java.lang.String
	 * @param a javax.swing.text.AttributeSet
	 */
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (!editing) {
			super.insertString(offs, str, a);
			return;
		}
	
		// log("***********************");
		if (str.equals("}")) {
			if (getText(offs - 1, 1).equals("\t")) {
				super.remove(offs - 1, 1);
				super.insertString(offs - 1, str, a);
			} else {
				super.insertString(offs, str, a);
			}
		} else if (str.equals("\n")) {
			// log("newline");
			int elementIndex = getDefaultRootElement().getElementIndex(offs);
			Element element = getDefaultRootElement().getElement(elementIndex);
			int startOffset = element.getStartOffset();
			int endOffset = element.getEndOffset();
			String elementText = null;

			elementText = getText(startOffset, endOffset - startOffset);
			int tabCount = 0;

			// log("enter tabcount for string |" + elementText + "|");
			while (elementText.charAt(tabCount) == '\t') {
				tabCount++;
				// log("tabCount is: " + tabCount);
			}
			if (elementText.indexOf("{") >= 0) {
				tabCount++;
			}
			String tabs = "";

			for (int i = 0; i < tabCount; i++) {
				tabs += "\t";
			}
			// log("calling super with " + tabCount + " tabs");
			super.insertString(offs, str + tabs, a);
		} else {
			// log("inserting string: |" + str + "|");
			super.insertString(offs, str, a);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/5/2001 2:58:51 PM)
	 * @param chng javax.swing.event.DocumentEvent
	 * @param attr javax.swing.text.AttributeSet
	 */
	protected void insertUpdate(DefaultDocumentEvent event, AttributeSet attributeSet) {
		if (editWindow != null) {
			editWindow.setModified(true);
		}
		
		// if (editorKit != null)
		// editorKit.setModified(true);
		int orgChangedIndex = getDefaultRootElement().getElementIndex(event.getOffset());
	
		super.insertUpdate(event, attributeSet);

		// Get the root element
		Element rootElement = getDefaultRootElement();

		// Determine what changes were made to the document
		DocumentEvent.ElementChange deltas = event.getChange(rootElement);

		// if (deltas != null && deltas.getChildrenAdded() != null)
		// {
		// log("have " + deltas.getChildrenAdded().length+ " deltas");
		// }
		/*
		 int offset = deltas.getChildrenAdded()[0].getEndOffset();
		 try {
		 insertString(offset,"\t",null);
		 } catch (BadLocationException e) {
		 log("Cannot insert tab.");
		 }
		 log("You hit enter for element" + deltas.getChildrenAdded()[0].getEndOffset());
		 /*offs - the starting offset >= 0
		 str - the string to insert; does nothing with null/empty strings
		 a - the attributes for the inserted content	}
		 */
		// }
		if (deltas == null) {
			Element changedElement = getDefaultRootElement().getElement(orgChangedIndex);

			processMultilineComments(changedElement, false);
		} else {
			Element changedElements[] = deltas.getChildrenAdded();

			if (changedElements == null || changedElements.length == 0) {
				log("Unknown insert even, 0 children added.");
			}
			for (int i = 0; i < changedElements.length; i++) {
				processMultilineComments(changedElements[i], true);
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 2:27:00 PM)
	 * @return boolean
	 */
	public boolean isNeedsRedraw() {
		return needsRedraw;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 5:28:09 PM)
	 * @param s java.lang.String
	 */
	private void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/5/2001 2:58:51 PM)
	 * @param chng javax.swing.event.DocumentEvent
	 */
	protected void postRemoveUpdate(DefaultDocumentEvent event) {
		if (editWindow != null) {
			editWindow.setModified(true);
		}
		
		int orgChangedIndex = getDefaultRootElement().getElementIndex(event.getOffset());

		super.postRemoveUpdate(event);
		// Get the root element
		Element rootElement = getDefaultRootElement();
		// Determine what changes were made to the document
		int changedIndex = getDefaultRootElement().getElementIndex(event.getOffset());
		Element changedElement = getDefaultRootElement().getElement(changedIndex);

		DocumentEvent.ElementChange deltas = event.getChange(rootElement);

		if (deltas != null) {
			processMultilineComments(changedElement, true);
		} else {
			processMultilineComments(changedElement, false);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 2:34:08 PM)
	 * @param e javax.swing.text.Element
	 */
	public void processMultilineComments(Element element, boolean isDeltas) {

		int elementIndex = getDefaultRootElement().getElementIndex(element.getStartOffset());
		// log("Processing multiline comments for line " + elementIndex + " at offset " + element.getStartOffset());

	
		int startOffset = element.getStartOffset();
		int endOffset = element.getEndOffset();
		String elementText = null;

		try {
			elementText = getText(startOffset, endOffset - startOffset);
			// log("Checking text: " + elementText);
		} catch (BadLocationException e) {
			log("Error processing updates: " + e);
		}
		boolean followingLineComment = false,
				previousLineComment = false, 
				startsComment = false,
				endsComment = false;

		// If we already had a comment flag, then the last line must
		// have ended "still in a comment"
		MutableAttributeSet a = (MutableAttributeSet) element.getAttributes();

		if (a.isDefined("inComment")) {
			previousLineComment = true;
		} // we don't have a comment flag, so check the last line.
		// note:  This should only happen on new lines!
		else if (isDeltas) {
			int lastElementIndex = elementIndex - 1;

			if (lastElementIndex >= 0) {
				AbstractDocument.AbstractElement lastElement = (AbstractDocument.AbstractElement) getDefaultRootElement().getElement(
						lastElementIndex);

				if (!lastElement.isDefined("endsComment") && lastElement.isDefined("inComment")
						|| lastElement.isDefined("startsComment")) {
					a.addAttribute("inComment", "inComment");
					previousLineComment = true;
				}
			}

			/*
			 // For deletes
			 int nextElementIndex = elementIndex + 1;
			 if (nextElementIndex < getDefaultRootElement().getElementCount())
			 {
			 AbstractDocument.AbstractElement lastElement= (AbstractDocument.AbstractElement)getDefaultRootElement().getElement(lastElementIndex);
			 if (!lastElement.isDefined("endsComment") && lastElement.isDefined("inComment") || lastElement.isDefined("startsComment"))
			 {
			 a.addAttribute("inComment","inComment");
			 previousLineComment = true;
			 }
			 }
			 */
		}
		followingLineComment = previousLineComment;

		int cIndex = elementText.indexOf("//");
		int eIndex, sIndex;

		if (cIndex >= 0) {
			eIndex = elementText.lastIndexOf("*/", cIndex);
			sIndex = elementText.lastIndexOf("/*", cIndex);
		} else {
			eIndex = elementText.lastIndexOf("*/");
			sIndex = elementText.lastIndexOf("/*");
		}
		if (eIndex > sIndex) {
			followingLineComment = false;
			endsComment = true;
			startsComment = false;
		} else if (sIndex > eIndex) {
			followingLineComment = true;
			startsComment = true;
			endsComment = false;
		}
		// Following lines should be comments.	
		if (followingLineComment == true) {
			// We started the comment
			if (startsComment) {
				if (!a.isDefined("startsComment")) {
					// mark line startsComment
					a.addAttribute("startsComment", "startsComment");
					// make sure next line(s) are marked inComment, until an endComment line
					setFollowingLinesCommentFlag(startOffset, true);
				}
			} else if (a.isDefined("startsComment")) {
				// log("Removing attribute start from line " + elementIndex);
				a.removeAttribute("startsComment");
			}
			// If we used to end the comment but no longer do, fix that...
			if (a.isDefined("endsComment")) {
				// unmark line as endsComment
				a.removeAttribute("endsComment");
				// make sure next line(s) are marked inComment, until a endsComment line
			
				setFollowingLinesCommentFlag(startOffset, true);
			} // For cut & paste we need to check anyway.
			else if (isDeltas) {
				setFollowingLinesCommentFlag(startOffset, true);
			}
		} // Else following lines are NOT comments
		else {
			// setFollowingLinesCommentFlag(startOffset,false);
			// We ended the comment
			if (endsComment) {
				if (!a.isDefined("endsComment")) {
					// mark line endsComment
					a.addAttribute("endsComment", "endsComment");
					// Make sure next line(s) are marked !inComment, until a startComment line
					setFollowingLinesCommentFlag(startOffset, false);
				}
			} else if (a.isDefined("endsComment")) {
				a.removeAttribute("endsComment");
			}
			// If we used to start a comment, but no longer do, fix that...
			if (a.isDefined("startsComment")) {
				// log("Removing(2) attribute start from line " + elementIndex);
				// mark line startsComment
				a.removeAttribute("startsComment");
				// make sure next line(s) are marked !inComment, until an endComment line
				setFollowingLinesCommentFlag(startOffset, false);
			} // For cut & paste we need to check anyway.
			else if (isDeltas) {
				setFollowingLinesCommentFlag(startOffset, false);
			}
		}
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2001 4:51:26 PM)
	 * @param newEditWindow robocode.editor.EditWindow
	 */
	public void setEditWindow(EditWindow newEditWindow) {
		editWindow = newEditWindow;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 1:25:57 PM)
	 * @param offset int
	 * @param commentFlag boolean
	 */
	public void setFollowingLinesCommentFlag(int offset, boolean commentFlag) {
		int elementIndex = getDefaultRootElement().getElementIndex(offset);

		// log("Marking lines after offset " + offset + " line " + elementIndex + " as " + commentFlag);
		elementIndex++;
		boolean done = false;

		while (!done) {
			// need to check for last line somehow...
			Element e = getDefaultRootElement().getElement(elementIndex);

			if (e == null) {
				done = true;
				// log("Last line reached.");
			} else {
				MutableAttributeSet a = (MutableAttributeSet) e.getAttributes();

				if (commentFlag) {
					if (a.isDefined("inComment")) {
						done = true;
					} else {
						a.addAttribute("inComment", "inComment");
						needsRedraw = true;
					}
					if (a.isDefined("endsComment")) {
						done = true;
					}
				} else {
					if (!a.isDefined("inComment")) {
						done = true;
					} else {
						a.removeAttribute("inComment");
						needsRedraw = true;
					}
					if (a.isDefined("startsComment")) {
						done = true;
					}
				}
				// log("Set line " + (elementIndex) + " comment: " + commentFlag);
				elementIndex++;
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 2:27:00 PM)
	 * @param newNeedsRedraw boolean
	 */
	public void setNeedsRedraw(boolean newNeedsRedraw) {
		needsRedraw = newNeedsRedraw;
	}

	/**
	 * Gets the editing.
	 * @return Returns a boolean
	 */
	public boolean getEditing() {
		return editing;
	}

	/**
	 * Sets the editing.
	 * @param editing The editing to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

}
