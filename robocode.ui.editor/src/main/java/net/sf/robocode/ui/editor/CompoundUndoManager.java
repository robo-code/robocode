/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import javax.swing.event.UndoableEditEvent;

import java.lang.reflect.Field;

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
	private EventType lastEventType;
	private boolean isCompoundMarkStart;

	public CompoundUndoManager() {
		super();
		reset();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
		UndoableEdit edit = undoableEditEvent.getEdit();

		DefaultDocumentEvent event = null;
		
		// Make sure this event is a document event
		if (edit instanceof DefaultDocumentEvent) {
			// Get the event type
			event = (DefaultDocumentEvent) edit;
		} else {
			try {
				Class<?> clazz = Class.forName("javax.swing.text.AbstractDocument$DefaultDocumentEventUndoableWrapper");
				if (UndoableEdit.class.isAssignableFrom(clazz)) {
					Field f = clazz.getDeclaredField("dde"); // DefaultDocumentEvent
					f.setAccessible(true);
					event = (DefaultDocumentEvent) f.get(edit);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (event != null) {
			EventType eventType = event.getType();

			// Check if the event type is not a change on character attributes, but instead an insertion or removal of
			// text.
			if (eventType != EventType.CHANGE) {
				boolean isEndCompoundEdit = false;

				// Check if current compound edit must be ended as it contains at least one new line
				if (eventType == EventType.INSERT) {
					try {
						// Check if the inserted text contains a new line character
						String insertedText = event.getDocument().getText(event.getOffset(), event.getLength());
						isEndCompoundEdit = insertedText.contains("\n");
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}

				// Make sure we are not in an explicit marked compound edit
				if (!isCompoundMarkStart) {
					// Check if current compound edit must be ended due to change between insertion or removal change
					isEndCompoundEdit |= (eventType != lastEventType);

					// Check if the current compound edit should be ended and a new one started
					if (isEndCompoundEdit) {
						endCurrentCompoundEdit();
					}
					// Save the last event type
					lastEventType = eventType;
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

	/**
	 * Ends the current compound edit, and mark the start for combining the next insertions or removals of text to be
	 * put into the same compound edit so that these combined operations acts like as one single edit.
	 *
	 * @see #markCompoundEnd()
	 */
	public void markCompoundStart() {
		endCurrentCompoundEdit();
		isCompoundMarkStart = true;
	}

	/**
	 * Ends the current compound edit so that previous edits acts like a single edit.
	 * 
	 * @see #markCompoundStart()
	 */
	public void markCompoundEnd() {
		endCurrentCompoundEdit();
		isCompoundMarkStart = false;
	}

	private void reset() {
		currentCompoundEdit = null;
		lastEventType = EventType.INSERT; // important
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
