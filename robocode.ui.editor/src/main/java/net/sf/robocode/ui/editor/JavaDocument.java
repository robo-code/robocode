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
 *     Flemming N. Larsen
 *     - Bugfix: Tabs were sometimes added at the end of the document when
 *       strings are inserted. Bug fixed with the insertString() method, were the
 *       tabCount is now decremented when a '}' is found in the current element
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class JavaDocument extends PlainDocument {
	private static final String IN_COMMENT = "inComment";
	private static final String ENDS_COMMENT = "endsComment";
	private static final String STARTS_COMMENT = "startsComment";

	private UndoHandler undoHandler;
	private boolean needsRedraw;
	private EditWindow editWindow;
	private boolean editing;

	public JavaDocument() {
		super();
		init();
	}

	public EditWindow getEditWindow() {
		return editWindow;
	}

	private void init() {
		addUndoableEditListener(getUndoHandler());
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
			String elementText;

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
				Logger.logError("Unknown insert even, 0 children added.");
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

		processMultilineComments(event);
	}

	public void processMultilineComments(DefaultDocumentEvent event) {
		Element rootElement = getDefaultRootElement();
		// Determine what changes were made to the document
		int changedIndex = getDefaultRootElement().getElementIndex(event.getOffset());
		Element changedElement = getDefaultRootElement().getElement(changedIndex);

		processMultilineComments(changedElement, event.getChange(rootElement) != null);
	}

	public void processMultilineComments(Element element, boolean isDeltas) {
		int elementIndex = getDefaultRootElement().getElementIndex(element.getStartOffset());

		int startOffset = element.getStartOffset();
		int endOffset = element.getEndOffset();
		String elementText;

		try {
			elementText = getText(startOffset, endOffset - startOffset);
		} catch (BadLocationException e) {
			Logger.logError("Error processing updates: ", e);
			return;
		}
		boolean followingLineComment,
				previousLineComment = false,
				startsComment = false,
				endsComment = false;

		// If we already had a comment flag, then the last line must
		// have ended "still in a comment"
		MutableAttributeSet a = (MutableAttributeSet) element.getAttributes();

		if (a.isDefined(IN_COMMENT)) {
			previousLineComment = true;
		} // we don't have a comment flag, so check the last line.
		// note:  This should only happen on new lines!
		else if (isDeltas) {
			int lastElementIndex = elementIndex - 1;

			if (lastElementIndex >= 0) {
				AbstractElement lastElement = (AbstractElement) getDefaultRootElement().getElement(lastElementIndex);

				if (!lastElement.isDefined(ENDS_COMMENT) && lastElement.isDefined(IN_COMMENT)
						|| lastElement.isDefined(STARTS_COMMENT)) {
					a.addAttribute(IN_COMMENT, IN_COMMENT);
					previousLineComment = true;
				}
			}
		}
		followingLineComment = previousLineComment;

		int cIndex = elementText.indexOf("//");
		int sIndex, eIndex;

		if (cIndex >= 0) {
			sIndex = elementText.lastIndexOf("/*", cIndex);
			eIndex = elementText.lastIndexOf("*/", cIndex);
		} else {
			sIndex = elementText.lastIndexOf("/*");
			eIndex = elementText.lastIndexOf("*/");
		}
		if (eIndex > sIndex) {
			followingLineComment = false;
			startsComment = false;
			endsComment = true;
		} else if (sIndex > eIndex) {
			followingLineComment = true;
			startsComment = true;
			endsComment = false;
		}
		// Following lines should be comments.
		if (followingLineComment) {
			// We started the comment
			if (startsComment) {
				if (!a.isDefined(STARTS_COMMENT)) {
					// mark line startsComment
					a.addAttribute(STARTS_COMMENT, STARTS_COMMENT);
					// make sure next line(s) are marked inComment, until an endComment line
					setFollowingLinesCommentFlag(startOffset, true);
				}
			} else if (a.isDefined(STARTS_COMMENT)) {
				a.removeAttribute(STARTS_COMMENT);
			}
			// If we used to end the comment but no longer do, fix that...
			if (a.isDefined(ENDS_COMMENT)) {
				// unmark line as endsComment
				a.removeAttribute(ENDS_COMMENT);
				// make sure next line(s) are marked inComment, until a endsComment line
				setFollowingLinesCommentFlag(startOffset, true);
			} // For cut & paste we need to check anyway.
			else if (isDeltas) {
				setFollowingLinesCommentFlag(startOffset, true);
			}
		} // Else following lines are NOT comments
		else {
			// We ended the comment
			if (endsComment) {
				if (!a.isDefined(ENDS_COMMENT)) {
					// mark line endsComment
					a.addAttribute(ENDS_COMMENT, ENDS_COMMENT);
					// Make sure next line(s) are marked !inComment, until a startComment line
					setFollowingLinesCommentFlag(startOffset, false);
				}
			} else if (a.isDefined(ENDS_COMMENT)) {
				a.removeAttribute(ENDS_COMMENT);
			}
			// If we used to start a comment, but no longer do, fix that...
			if (a.isDefined(STARTS_COMMENT)) {
				// mark line startsComment
				a.removeAttribute(STARTS_COMMENT);
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
		int elementIndex = getDefaultRootElement().getElementIndex(offset) + 1;

		boolean done = false;

		while (!done) {
			// need to check for last line somehow...
			Element e = getDefaultRootElement().getElement(elementIndex);

			if (e == null) {
				done = true;
			} else {
				MutableAttributeSet a = (MutableAttributeSet) e.getAttributes();

				if (commentFlag) {
					if (a.isDefined(IN_COMMENT)) {
						done = true;
					} else {
						a.addAttribute(IN_COMMENT, IN_COMMENT);
						needsRedraw = true;
					}
					if (a.isDefined(ENDS_COMMENT)) {
						done = true;
					}
				} else {
					if (!a.isDefined(IN_COMMENT)) {
						done = true;
					} else {
						a.removeAttribute(IN_COMMENT);
						needsRedraw = true;
					}
					if (a.isDefined(STARTS_COMMENT)) {
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
		// This check is a bugfix for [2643448] - Editor UNDO does delete the line when no undo left.
		// All undo edits are discarded, when the editor is going from not editing to editing.
		if (editing && !this.editing) {
			getUndoHandler().discardAllEdits();
		}

		this.editing = editing;
	}

	/**
	 * Returns the UndoHandler
	 *
	 * @return the UndoHandler
	 */
	private UndoHandler getUndoHandler() {
		if (undoHandler == null) {
			undoHandler = new UndoHandler();
		}
		return undoHandler;
	}

	/**
	 * Undo
	 */
	public void undo() {
		if (getUndoHandler().canUndo()) {
			writeLock();
			try {
				getUndoHandler().undo();
			} finally {
				writeUnlock();
			}
			needsRedraw = true;
		}
	}

	/**
	 * Redo
	 */
	public void redo() {
		if (getUndoHandler().canRedo()) {
			writeLock();
			try {
				getUndoHandler().redo();
			} finally {
				writeUnlock();
			}
			needsRedraw = true;
		}
	}

	private class UndoHandler extends UndoManager {
		@Override
		public synchronized void undo() {
			super.undo();
			processMultilineComments((DefaultDocumentEvent) lastEdit());
		}

		@Override
		public synchronized void redo() {
			super.redo();
			processMultilineComments((DefaultDocumentEvent) lastEdit());
		}
	}
}
