package net.sf.robocode.ui.editor;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.IEditorThemeProperties;


public class HighlightLinePainter implements Highlighter.HighlightPainter {

	private JTextComponent component;
	private Color color;
	private Rectangle lastView;

	public HighlightLinePainter(JTextComponent component) {
		this.component = component;

		color = EditorThemePropertiesManager.getCurrentEditorThemeProperties().getHighlightedLineColor();

		EditorThemePropertiesManager.addListener(new IEditorPropertyChangeListener() {
			@Override
			public void onChange(IEditorThemeProperties properties) {
				Color highlightedLineColor = properties.getHighlightedLineColor();
				if (!color.equals(highlightedLineColor)) {
					setColor(highlightedLineColor);
				}
			}
		});

		component.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				resetHighlight();
			}
		});

		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				resetHighlight();
			}
		});
		
		// Turn highlighting on by adding a dummy highlight
		try {
			component.getHighlighter().addHighlight(0, 0, this);
		} catch (BadLocationException ignore) {}
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle r = c.modelToView(c.getCaretPosition());

			g.setColor(color);
			g.fillRect(0, r.y, c.getWidth(), r.height);

			if (lastView == null) {
				lastView = r;
			}
		} catch (BadLocationException ignore) {}
	}

	private void setColor(Color color) {
		this.color = color;
	}

	private void resetHighlight() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int pos = component.getCaretPosition();
					Rectangle currentView = component.modelToView(pos);

					if (currentView != null && lastView != null) {
						// Remove the highlighting from the previously highlighted line
						if (lastView.y != currentView.y) {
							component.repaint(0, lastView.y, component.getWidth(), lastView.height);
							lastView = currentView;
						}
					}
				} catch (BadLocationException ignore) {}
			}
		});
	}
}
