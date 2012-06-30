/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.event.UndoableEditEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;


/**
 * Undo manager that compounds undo and redo edits.
 * 
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class CompoundUndoManager extends UndoManagerWithActions {

	private CompoundEdit currentCompoundEdit;
	private EventType lastInsertRemoveEventType;

	public CompoundUndoManager() {
		super();
		reset();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
		UndoableEdit edit = undoableEditEvent.getEdit();

		if (edit instanceof DefaultDocumentEvent) {
			DefaultDocumentEvent event = (DefaultDocumentEvent) edit;
			EventType eventType = event.getType();

			if (eventType != EventType.CHANGE) {

				boolean isEndCompoundEdit = false;
	
				// Check if current compound edit must be ended due to starting a new line
				if (eventType == EventType.INSERT) {
					try {
						// Check if the inserted text contains a new line character
						String insertedText = event.getDocument().getText(event.getOffset(), event.getLength());
	
						isEndCompoundEdit = insertedText.contains("\n");
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				// Check if current compound edit must be ended due to change between insertion or removal change
				isEndCompoundEdit |= (eventType != lastInsertRemoveEventType);
				lastInsertRemoveEventType = eventType;
	
				// Check if the current compound edit should be ended and a new one started
				if (isEndCompoundEdit) {
					endCurrentCompoundEdit();
				}
				// Create new compound edit if the current one has been ended or does not exist
				if (currentCompoundEdit == null) {
					newCurrentCompoundEdit();
				}
			}
			// Added event edit to the current compound edit
			if (currentCompoundEdit != null) {
				currentCompoundEdit.addEdit(edit);
			}
		}
		// Update the state of the actions
		updateUndoRedoState();
	}

	@Override
	public void discardAllEdits() {
		super.discardAllEdits();
		reset();
	}

	private void reset() {
		currentCompoundEdit = null;
		lastInsertRemoveEventType = EventType.INSERT; // important
	}

	private void endCurrentCompoundEdit() {
		if (currentCompoundEdit != null) {
			currentCompoundEdit.end();
			currentCompoundEdit = null;
		}
	}

	private void newCurrentCompoundEdit() {
		// Set current compound edit to a new one
		currentCompoundEdit = new CompoundEdit() {
			// Make sure canUndo() and canRedo() works
			@Override
			public boolean isInProgress() {
				return false;
			}

			@Override
			public void undo() throws CannotUndoException {
				endCurrentCompoundEdit();
				super.undo();
			}
		};
		// Add the current compound edit to the internal edits
		addEdit(currentCompoundEdit);
	}
}
