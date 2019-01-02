/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;


/**
 * Undo manager containing actions for performing undo and redo edits.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class UndoManagerWithActions extends UndoManager {

	private Action undoAction = new UndoAction();
	private Action redoAction = new RedoAction();

	public UndoManagerWithActions() {
		super();
		updateUndoRedoState();
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		super.undo();
		updateUndoRedoState();
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		super.redo();
		updateUndoRedoState();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
		super.undoableEditHappened(undoableEditEvent);
		updateUndoRedoState();
	}

	public Action getUndoAction() {
		return undoAction;
	}

	public Action getRedoAction() {
		return redoAction;
	}

	protected void updateUndoRedoState() {
		undoAction.setEnabled(canUndo());
		redoAction.setEnabled(canRedo());
	}

	private class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
		}

		public void actionPerformed(ActionEvent ae) {
			try {
				undo();
			} catch (CannotUndoException e) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}


	private class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
		}

		public void actionPerformed(ActionEvent ae) {
			try {
				redo();
			} catch (CannotRedoException e) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
}
