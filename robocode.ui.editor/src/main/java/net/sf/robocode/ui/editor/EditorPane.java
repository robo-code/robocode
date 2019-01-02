/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;

import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;


/**
 * Editor pane used for editing source code.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class EditorPane extends JTextPane {

	private int tabSize = 4; // Default

	// Key bindings
	private static final KeyStroke CUT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
	private static final KeyStroke COPY_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);
	private static final KeyStroke PASTE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
	private static final KeyStroke UNDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
	private static final KeyStroke REDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);

	private final JavaDocument document;

	private final CompoundUndoManager undoManager = new CompoundUndoManager();

	private final TextTool textTool = new TextTool();

	private JViewport viewport;

	public EditorPane(JViewport viewport) {
		super();
		this.viewport = viewport;
		document = new JavaDocument(this);

		DefaultCaret caret = (DefaultCaret) getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		new LineNumberArea(this);

		LineHighlighter.install(this);

		EditorKit editorKit = new StyledEditorKit() {
			@Override
			public Document createDefaultDocument() {
				return document;
			}
		};

		setEditorKitForContentType("text/java", editorKit);
		setContentType("text/java");

		addPropertyChangeListener("font", new FontHandler());
		addKeyListener(new KeyHandler());

		setKeyBindings();
		setActionBindings();

		getDocument().addUndoableEditListener(undoManager);
	}

	public JViewport getViewport() {
		return viewport;
	}

	// No line wrapping!
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getUI().getPreferredSize(this).width <= getParent().getSize().width;
	}

	// Don't enforce height -> Avoid line height changes
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	// Make sure to discard all undo/redo edits when text completely replaced
	@Override
	public void setText(final String text) {
		// Adding this call to the EDT prevents problems with deleting text that eats text before the deletion
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JavaDocument javaDocument = (JavaDocument) getDocument();

				// Bug-357: Tab characters are inserted in the last line of a robot source file when opening it.
				// This bug was fixed by disabling auto-indentation while reading the file.
				javaDocument.setReplacingText(true);
				EditorPane.super.setText(text);
				javaDocument.setReplacingText(false);

				resetCaretPosition();

				undoManager.discardAllEdits();
			}
		});
	}

	// Make sure to discard all undo/redo edits when text is read as one block
	@Override
	public void read(Reader in, Object desc) throws IOException {
		JavaDocument javaDocument = (JavaDocument) getDocument();

		// Bug-357: Tab characters are inserted in the last line of a robot source file when opening it.
		// This bug was fixed by disabling auto-indentation while reading the file.
		javaDocument.setReplacingText(true);
		super.read(in, desc);
		javaDocument.setReplacingText(false);

		resetCaretPosition();

		undoManager.discardAllEdits();
	}

	public boolean isModified() {
		return undoManager.canUndo();
	}

	public void undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}
	
	public void redo() {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}

	private void setKeyBindings() {
		InputMap inputMap = this.getInputMap();

		inputMap.put(CUT_KEYSTROKE, DefaultEditorKit.cutAction);
		inputMap.put(COPY_KEYSTROKE, DefaultEditorKit.copyAction);
		inputMap.put(PASTE_KEYSTROKE, DefaultEditorKit.pasteAction);
		inputMap.put(UNDO_KEYSTROKE, undoManager.getUndoAction());
		inputMap.put(REDO_KEYSTROKE, undoManager.getRedoAction());
	}

	private void setActionBindings() {
		getActionMap().put("undo-keystroke", undoManager.getUndoAction());
		getActionMap().put("redo-keystroke", undoManager.getRedoAction());
	}

	private void setTabSize(int tabSize) {
		document.setTabSize(tabSize);

		FontMetrics fm = getFontMetrics(getFont());

		int charWidth = fm.charWidth('#');
		int tabWidth = charWidth * tabSize;

		TabStop[] tabs = new TabStop[100];
		for (int j = 0; j < tabs.length; j++) {
			tabs[j] = new TabStop((j + 1) * tabWidth);
		}

		SimpleAttributeSet attributes = new SimpleAttributeSet();

		StyleConstants.setTabSet(attributes, new TabSet(tabs));

		getDocument().removeUndoableEditListener(undoManager); // Avoid this change to be undone
		getStyledDocument().setParagraphAttributes(0, getDocument().getLength(), attributes, false);
		getDocument().addUndoableEditListener(undoManager);
	}

	private void resetCaretPosition() {
		// Make sure to put this request on the EDT or the caret position might not be reset after all.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setCaretPosition(0);
			}
		});	
	}

	private void onTabCharPressed(boolean isUnindent) {

		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();

		if (selectionStart == selectionEnd) {
			// No selection -> Handle normal tab indentation

			if (isUnindent) {
				textTool.removeLastTab(getCaretPosition());
			} else {
				textTool.insertString(getCaretPosition(), "\t");
			}
		} else {
			// Start block indentation

			// Make sure that the start selection is lesser than the end selection
			if (selectionStart > selectionEnd) {
				// Swap selection start and selection end
				int tmp = selectionStart;

				selectionStart = selectionEnd;
				selectionEnd = tmp;
			}

			StringBuilder newText = new StringBuilder();
			int count = 0;
			boolean stopUnindent = false;

			// Handle each line

			String selectedText = textTool.getSelectedText();

			selectedText = selectedText.replaceAll("\\s*$", "");

			String[] lines = selectedText.split(String.valueOf('\n'));

			// iterate lines, rebuilding tabs and newlines
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				if (isUnindent) {
					if (line.charAt(0) != '\t') {
						stopUnindent = true;
						break;
					} else {
						newText.append(line.substring(1, line.length()));
						count++;
					}
				} else {
					newText.append('\t').append(line);
					count++;
				}
				if (i != lines.length - 1) {
					newText.append('\n');
				}
			}

			if (!stopUnindent) {
				try {
					// Replace the indented/unindented text in one single compound edit
					undoManager.markCompoundStart();
					getDocument().remove(selectionStart, selectedText.length());
					textTool.insertString(selectionStart, newText.toString());
					undoManager.markCompoundStart();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				// Compute the new selection
				int mod = isUnindent ? -1 : 1;

				// Make new selection using the EDT, as replacing the text above must run before a new selection can be made 

				final int newSelectionStart = selectionStart;
				final int newSelectionEnd = selectionStart + selectedText.length() + count * mod;

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setSelectionStart(newSelectionStart);
						setSelectionEnd(newSelectionEnd);
					}
				});
			}
		}
	}

	private class FontHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			setTabSize(tabSize);
		}
	}


	private class KeyHandler extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_TAB:
				onTabCharPressed(e.isShiftDown());
				e.consume();
				break;
			}
		}
	}


	/**
	 * Handy text tool.
	 */
	private class TextTool {

		String getSelectedText() {
			int start = getSelectionStart();
			int end = getSelectionEnd();

			try {
				return getDocument().getText(start, end - start);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return null;
		}

		void insertString(int offset, String str) {
			insertString(offset, str, null);
		}

		void insertString(final int offset, final String str, final AttributeSet a) {
			try {
				getDocument().insertString(offset, str, a);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		void removeLastTab(int offset) {
			int pos = offset;

			while (--pos >= 0) {
				try {
					char ch = getDocument().getText(pos, 1).charAt(0);

					if (ch == '\t') {
						remove(pos, 1);
						return;
					}
					if (ch == '\n') {
						return;
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		
		void remove(final int offset, final int len) {
			try {
				getDocument().remove(offset, len);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
