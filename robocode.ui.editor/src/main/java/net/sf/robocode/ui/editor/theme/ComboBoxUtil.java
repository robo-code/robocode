/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sf.robocode.ui.editor.FontStyle;


/**
 * Class for utilizing JComboBoxes.
 *
 * @author Flemming N. Larsen (original)
 * @since 1.8.3.0
 */
public class ComboBoxUtil {

	public static void setSelected(JComboBox comboBox, String name) {
		ComboBoxModel model = comboBox.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String itemName = (String) model.getElementAt(i);
			if (itemName.equalsIgnoreCase(name)) {
				model.setSelectedItem(itemName);
				break;
			}
		}
	}

	public static void setSelected(JComboBox comboBox, FontStyle fontStyle) {
		ComboBoxModel model = comboBox.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String name = (String) model.getElementAt(i);
			FontStyle style = FontStyle.fromName(name);
			if (style != null && style == fontStyle) {
				model.setSelectedItem(name);
				break;
			}
		}
	}

	public static String getSelectedName(JComboBox comboBox) {
		return (String) comboBox.getSelectedItem();
	}

	public static JComboBox createFontStyleComboBox(FontStyle fontStyle) {
		List<String> fontStyles = new ArrayList<String>();
		for (FontStyle style : FontStyle.values()) {
			fontStyles.add(style.getName());
		}
		JComboBox comboBox = new JComboBox(fontStyles.toArray());
		comboBox.setRenderer(new FontStyleCellRenderer());

		ComboBoxUtil.setSelected(comboBox, fontStyle);

		return comboBox;
	}

	public static FontStyle getSelectedStyle(JComboBox comboBox) {
		return FontStyle.fromName((String) comboBox.getSelectedItem());
	}

	private static class FontStyleCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			FontStyle fontStyle = FontStyle.fromName((String) value);
			int styleFlags = (fontStyle == null) ? Font.PLAIN : fontStyle.getFontStyleFlags();

			Font font = new Font((String) value, styleFlags, 12);
			label.setFont(font);
			return label;
		}
	}
}
