/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import net.sf.robocode.ui.editor.theme.EditorThemeProperties;
import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemePropertyChangeAdapter;


/**
 * Editor panel containing editor pane in a scroll pane, a line number area, and a statusTextField text field.
 *
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class EditorPanel extends JPanel {

	private JTextField statusTextField;
	private final JScrollPane scrollPane;
	private final EditorPane editorPane;
	private final LineNumberArea lineNumberArea;

	public EditorPanel() {
		super();

		setLayout(new BorderLayout());

		statusTextField = new JTextField();
		statusTextField.setEditable(false);

		scrollPane = new JScrollPane();

		editorPane = new EditorPane(scrollPane.getViewport());
		editorPane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateStatus(getRow(e.getDot(), editorPane), getColumn(e.getDot(), editorPane));
			}
		});

		scrollPane.setViewportView(editorPane);

		EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
		setBackgroundColor(themeProps.getBackgroundColor());
		setSelectionColor(themeProps.getSelectionColor());
		setSelectedTextColor(themeProps.getSelectedTextColor());

		lineNumberArea = new LineNumberArea(editorPane);
		scrollPane.setRowHeaderView(lineNumberArea);

		add(scrollPane, BorderLayout.CENTER);
		add(statusTextField, BorderLayout.SOUTH);

		updateStatus(1, 1);

		EditorThemePropertiesManager.addListener(new EditorThemePropertyChangeAdapter() {
			@Override
			public void onBackgroundColorChanged(Color newColor) {
				setBackgroundColor(newColor);
			}

			@Override
			public void onSelectionColorChanged(Color newColor) {
				setSelectionColor(newColor);
			}

			@Override
			public void onSelectedTextColorChanged(Color newColor) {
				setSelectedTextColor(newColor);
			}
		});
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		if (editorPane != null) {
			editorPane.requestFocus();
			editorPane.requestFocusInWindow();
		}
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (editorPane != null) {
			editorPane.setFont(font);

			Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
			editorPane.setBorder(border);
		}
		if (lineNumberArea != null) {
			lineNumberArea.setFont(font);

			FontMetrics fm = getFontMetrics(font);
			int delta = fm.getHeight() - fm.getDescent() - fm.getAscent();
			Border border = BorderFactory.createEmptyBorder(delta + 3, 3, 3, 3);
			lineNumberArea.setBorder(border);
		}
	}

	public EditorPane getEditorPane() {
		return editorPane;
	}
	
	private void updateStatus(int linenumber, int columnnumber) {
		statusTextField.setText("Line: " + linenumber + " Column: " + columnnumber);
	}

	private static int getRow(int pos, JTextComponent editor) {
		int rn = (pos == 0) ? 1 : 0;

		try {
			int offs = pos;

			while (offs > 0) {
				offs = Utilities.getRowStart(editor, offs) - 1;
				rn++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return rn;
	}

	private static int getColumn(int pos, JTextComponent editor) {
		try {
			return pos - Utilities.getRowStart(editor, pos) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void setBackgroundColor(final Color backgroundColor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollPane.getViewport().setBackground(backgroundColor);
				editorPane.setBackground(backgroundColor);
			}
		});		
	}

	private void setSelectionColor(final Color selectionColor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				editorPane.setSelectionColor(selectionColor);
			}
		});		
	}

	private void setSelectedTextColor(final Color selectedTextColor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				editorPane.setSelectedTextColor(selectedTextColor);
			}
		});
	}
}
