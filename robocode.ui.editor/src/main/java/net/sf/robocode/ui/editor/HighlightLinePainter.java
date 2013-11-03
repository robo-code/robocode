package net.sf.robocode.ui.editor;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemePropertyChangeAdapter;


public class HighlightLinePainter implements Highlighter.HighlightPainter {

	private JTextComponent component;
	private Color color;
	private Stack<Rectangle> repaintRects = new Stack<Rectangle>();

	public HighlightLinePainter(JTextComponent component) {
		this.component = component;

		color = EditorThemePropertiesManager.getCurrentEditorThemeProperties().getHighlightedLineColor();

		EditorThemePropertiesManager.addListener(new EditorThemePropertyChangeAdapter() {
			@Override
			public void onHighlightedLineColorChanged(Color newColor) {
				if (!color.equals(newColor)) {
					setColor(newColor);
				}
			}
		});

		component.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				resetHighlight();
			}
		});

		component.addMouseListener(new MouseAdapter() {
			@Override
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
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent component) {

		resetHighlight();

		try {
			Rectangle viewRect = component.modelToView(component.getCaretPosition());

			Rectangle paintRect = new Rectangle(0, viewRect.y, component.getWidth(), viewRect.height);

			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(color);
			g2.fill(paintRect);

			repaintRects.push(paintRect);

		} catch (BadLocationException ignore) {}
	}

	private void setColor(Color color) {
		this.color = color;
	}

	private void resetHighlight() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Remove the highlighting from the previously highlighted lines
				while (repaintRects.size() > 0) {
					component.repaint(repaintRects.pop());
				}
			}
		});
	}
}
