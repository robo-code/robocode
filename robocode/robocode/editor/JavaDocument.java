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
 *     - Bugfix: Tabs were sometimes added at the end of the document when
 *       strings are inserted. Bug fixed with the insertString() method, were the
 *       tabCount is now decremented when a '}' is found in the current element
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package robocode.editor;


import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class JavaDocument extends PlainDocument {
	private boolean needsRedraw;
	private EditWindow editWindow;
	private boolean editing;

	public JavaDocument() {
		super();
	}

	protected JavaDocument(AbstractDocument.Content c) {
		super(c);
	}

	public EditWindow getEditWindow() {
		return editWindow;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (!editing) {
			super.insertString(offs, str, a);
			return;
		}

		if (str.equals("}")) {
			if (getText(offs - 1, 1).equals("\t")) {
				super.remove(offs - 1, 1);
				super.insertString(offs - 1, str, a);
			} else {
				super.insertString(offs, str, a);
			}
		} else if (str.equals("\n")) {
			int elementIndex = getDefaultRootElement().getElementIndex(offs);
			Element element = getDefaultRootElement().getElement(elementIndex);
			int startOffset = element.getStartOffset();
			int endOffset = element.getEndOffset();
			String elementText = null;

			elementText = getText(startOffset, endOffset - startOffset);
			int tabCount = 0;

			while (elementText.charAt(tabCount) == '\t') {
				tabCount++;
			}
			if (elementText.indexOf("{") >= 0) {
				tabCount++;
			}
			if (elementText.indexOf("}") >= 0) {
				tabCount--;
			}
			String tabs = "";

			for (int i = 0; i < tabCount; i++) {
				tabs += "\t";
			}
			super.insertString(offs, str + tabs, a);
		} else {
			super.insertString(offs, str, a);
		}
	}

	@Override
	protected void insertUpdate(DefaultDocumentEvent event, AttributeSet attributeSet) {
		if (editWindow != null) {
			editWindow.setModified(true);
		}
		int orgChangedIndex = getDefaultRootElement().getElementIndex(event.getOffset());

		super.insertUpdate(event, attributeSet);

		// Get the root element
		Element rootElement = getDefaultRootElement();

		// Determine what changes were made to the document
		DocumentEvent.ElementChange deltas = event.getChange(rootElement);

		if (deltas == null) {
			Element changedElement = getDefaultRootElement().getElement(orgChangedIndex);

			processMultilineComments(changedElement, false);
		} else {
			Element changedElements[] = deltas.getChildrenAdded();

			if (changedElements == null || changedElements.length == 0) {
				Logger.log("Unknown insert even, 0 children added.");
			} else {
				for (Element element : changedElements) {
					processMultilineComments(element, true);
				}
			}
		}
	}

	public boolean isNeedsRedraw() {
		return needsRedraw;
	}

	@Override
	protected void postRemoveUpdate(DefaultDocumentEvent event) {
		if (editWindow != null) {
			editWindow.setModified(true);
		}
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

	public void processMultilineComments(Element element, boolean isDeltas) {
		int elementIndex = getDefaultRootElement().getElementIndex(element.getStartOffset());

		int startOffset = element.getStartOffset();
		int endOffset = element.getEndOffset();
		String elementText = null;

		try {
			elementText = getText(startOffset, endOffset - startOffset);
		} catch (BadLocationException e) {
			Logger.log("Error processing updates: " + e);
			return;
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
		if (followingLineComment) {
			// We started the comment
			if (startsComment) {
				if (!a.isDefined("startsComment")) {
					// mark line startsComment
					a.addAttribute("startsComment", "startsComment");
					// make sure next line(s) are marked inComment, until an endComment line
					setFollowingLinesCommentFlag(startOffset, true);
				}
			} else if (a.isDefined("startsComment")) {
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

	public void setEditWindow(EditWindow newEditWindow) {
		editWindow = newEditWindow;
	}

	public void setFollowingLinesCommentFlag(int offset, boolean commentFlag) {
		int elementIndex = getDefaultRootElement().getElementIndex(offset);

		elementIndex++;
		boolean done = false;

		while (!done) {
			// need to check for last line somehow...
			Element e = getDefaultRootElement().getElement(elementIndex);

			if (e == null) {
				done = true;
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
				elementIndex++;
			}
		}
	}

	public void setNeedsRedraw(boolean newNeedsRedraw) {
		needsRedraw = newNeedsRedraw;
	}

	public boolean getEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}
}
