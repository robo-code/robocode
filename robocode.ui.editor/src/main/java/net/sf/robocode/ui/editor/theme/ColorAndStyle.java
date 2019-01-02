/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.robocode.ui.editor.FontStyle;


/**
 * Class that is used for maintaining a text color and font style for an editor.
 *
 * @author Flemming N. Larsen (original)
 * @since 1.8.3.0
 */
public class ColorAndStyle {
	private final JLabel label;
	private final JButton coloredButton;
	private final JComboBox fontStyleComboBox;
	private final List<IColorAndStyleListener> listeners = new ArrayList<IColorAndStyleListener>();

	private Color oldColor;
	private FontStyle oldStyle;

	public ColorAndStyle(String label, final Color color, final FontStyle fontStyle) {
		this.label = new JLabel(label);

		coloredButton = createColoredButton(color);
		if (coloredButton != null) {
			coloredButton.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (!coloredButton.getBackground().equals(color)) {
						notifyColorChanged(coloredButton.getBackground());
					}
				}
			});
		}

		fontStyleComboBox = fontStyle == null ? null : ComboBoxUtil.createFontStyleComboBox(fontStyle);
		if (fontStyleComboBox != null) {
			fontStyleComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FontStyle newStyle = ComboBoxUtil.getSelectedStyle(fontStyleComboBox);
					if (oldStyle != newStyle) {
						notifyStyleChanged(newStyle);
						oldStyle = newStyle;
					}
				}
			});
		}
	}

	public JLabel getLabel() {
		return label;
	}

	public JButton getColoredButton() {
		return coloredButton;
	}

	public JComboBox getFontStyleComboBox() {
		return fontStyleComboBox;
	}

	public void addListener(IColorAndStyleListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IColorAndStyleListener listener) {
		listeners.remove(listener);
	}

	public Color getSelectedColor() {
		return (coloredButton == null) ? null : coloredButton.getBackground();
	}

	public void setSelectedColor(Color newColor) {
		if (coloredButton != null) {
			coloredButton.setBackground(newColor);
		}
		notifyColorChanged(newColor);
	}
	
	public FontStyle getSelectedStyle() {
		return (fontStyleComboBox == null) ? null : ComboBoxUtil.getSelectedStyle(fontStyleComboBox);
	}

	public void setSelectedStyle(FontStyle newStyle) {
		if (fontStyleComboBox != null) {
			ComboBoxUtil.setSelected(fontStyleComboBox, newStyle);
		}
		notifyStyleChanged(newStyle);
	}

	private void notifyColorChanged(Color newColor) {
		if (newColor == null || !newColor.equals(oldColor)) {
			for (IColorAndStyleListener listener : listeners) {
				listener.colorChanged(newColor);
			}
			oldColor = newColor;
		}
	}

	private void notifyStyleChanged(FontStyle newStyle) {
		if (newStyle == null || !newStyle.equals(oldStyle)) {
			for (IColorAndStyleListener listener : listeners) {
				listener.styleChanged(newStyle);
			}
			oldStyle = newStyle;
		}
	}

	private static JButton createColoredButton(Color color) {
		final JButton button = new JButton();
		button.setBackground(color);
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		Dimension size = new Dimension(24, 24);
		button.setPreferredSize(size);
		button.setSize(size);
		button.setMaximumSize(size);
		button.setMinimumSize(size);
		button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color selectedColor = JColorChooser.showDialog(null, "Pick a color", button.getBackground());
				if (selectedColor != null) {
					button.setBackground(selectedColor);
				}
			}
		});

		return button;
	}
}
