/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Flemming N. Larsen (original)
 *
 * @since 1.8.2.0
 */
@SuppressWarnings("serial")
public class EditorFontAndColorsDialog extends JDialog {

	private static final boolean ONLY_MONOSPACED = true;

	private JButton okButton;
	private JButton cancelButton;

	private JComboBox fontNameComboBox;
	private JComboBox fontStyleComboBox;
	private JComboBox fontSizeComboBox;

	private JLabel previewLabel;

	private JComboBox themeComboBox;

	private ColorAndStyle backgroundColorAndStyle;
	private ColorAndStyle normalTextColorAndStyle;
	private ColorAndStyle quotedTextColorAndStyle;
	private ColorAndStyle keywordColorAndStyle;
	private ColorAndStyle literalColorAndStyle;
	private ColorAndStyle annotationColorAndStyle;
	private ColorAndStyle commentColorAndStyle;
	
	private final EventHandler eventHandler = new EventHandler();

	public EditorFontAndColorsDialog(JFrame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		setTitle("Font & Color Chooser");
		setLayout(new GridBagLayout());
		setResizable(false);

		// Set the font that must be used initially ----
		setInitialSelectedFont();
		
		// ---- General font settings ----

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		int gridy = 0;

		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.weightx = 0.6;
		add(getFontNamePanel(), gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.3;
		add(getFontStylePanel(), gbc);

		gbc.gridx = 2;
		gbc.weightx = 0.1;
		add(getFontSizePanel(), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = ++gridy;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(getPreviewPanel(), gbc);

		// ----- Theme settings ----

		gbc.weightx = 0;
		gbc.weighty = 0;

		gbc.gridx = 0;
		gbc.gridy = ++gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		add(getThemeComboBox(), gbc);

		// ---- Color buttons and font styles ----
		
		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = ++gridy;
		gbc.gridwidth = 1;

		gbc.fill = GridBagConstraints.NONE;

		JPanel colorButtonsPanel = new JPanel();
		colorButtonsPanel.setLayout(new GridBagLayout());
		add(colorButtonsPanel, gbc);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.insets = new Insets(5, 5, 5, 5);
		gbc2.anchor = GridBagConstraints.WEST;

		int gridy2 = 0;

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getBackgroundColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getNormalTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getCommentColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getQuotedTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getKeywordColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getLiteralColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getAnnotationColorAndStyle(), colorButtonsPanel, gbc2);

		// ------ OK & Cancel buttons ----

		JPanel okCancelPanel = new JPanel();
		okCancelPanel.add(getOkButton());
		okCancelPanel.add(getCancelButton(), gbc);

		gbc.gridy = ++gridy;
		add(okCancelPanel, gbc);
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.setMnemonic('O');
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.setMnemonic('C');
			cancelButton.addActionListener(eventHandler);
		}
		return cancelButton;
	}

	private JComboBox getFontNameComboBox() {
		if (fontNameComboBox == null) {
			String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			List<String> fontFamilyNameList = Arrays.asList(fontFamilyNames);

			List<String> fontNameList = new ArrayList<String>();

			if (ONLY_MONOSPACED) {
				for (String monospacedFontName : getMonospacedFontNames()) {
					if (fontFamilyNameList.contains(monospacedFontName)) {
						fontNameList.add(monospacedFontName);
					}
				}
			} else {
				fontNameList.addAll(fontFamilyNameList);
			}
			
			fontNameComboBox = new JComboBox(fontNameList.toArray());
			fontNameComboBox.setRenderer(new FontCellRenderer());
		}
		return fontNameComboBox;
	}

	private JComboBox getFontStyleComboBox() {
		if (fontStyleComboBox == null) {
			fontStyleComboBox = createFontStyleComboBox(FontStyle.PLAIN);

			fontStyleComboBox.addActionListener(
					new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setSelected(getNormalTextColorAndStyle().fontStyleComboBox,
							(String) fontStyleComboBox.getSelectedItem());
				}				
			});
		}
		return fontStyleComboBox;
	}

	private JComboBox getFontSizeComboBox() {
		if (fontSizeComboBox == null) {
			String[] sizes = {
				"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"	};
			fontSizeComboBox = new JComboBox(sizes);
			fontSizeComboBox.setSelectedIndex(5);
		}
		return fontSizeComboBox;
	}

	private JPanel getFontNamePanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Font Name");

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(label, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL; 
		gbc.weightx = 1.0;
		panel.add(getFontNameComboBox(), gbc);

		return panel;
	}

	private JPanel getFontStylePanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Style");

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(label, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL; 
		gbc.weightx = 1.0;
		panel.add(getFontStyleComboBox(), gbc);

		return panel;
	}

	private JPanel getFontSizePanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Size");

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(label, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL; 
		gbc.weightx = 1.0;
		panel.add(getFontSizeComboBox(), gbc);

		return panel;
	}

	private JLabel getPreviewLabel() {
		if (previewLabel == null) {
			previewLabel = new JLabel("AaBbYyZz");
		}
		return previewLabel;
	}
	
	private JPanel getPreviewPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setBackground(Color.WHITE);
		panel.setForeground(Color.BLACK);
		panel.setPreferredSize(new Dimension(400, 120));

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPreviewLabelFont(panel);
				panel.repaint();
			}
		};

		getFontNameComboBox().addActionListener(actionListener);
		getFontStyleComboBox().addActionListener(actionListener);
		getFontSizeComboBox().addActionListener(actionListener);

		setPreviewLabelFont(panel);
		panel.add(getPreviewLabel(), SwingConstants.CENTER);
		
		return panel;
	}

	private ColorAndStyle getBackgroundColorAndStyle() {
		if (backgroundColorAndStyle == null) {
			backgroundColorAndStyle = new ColorAndStyle("Background Color", Color.WHITE, null);
		}
		return backgroundColorAndStyle;
	}

	private ColorAndStyle getNormalTextColorAndStyle() {
		if (normalTextColorAndStyle == null) {
			normalTextColorAndStyle = new ColorAndStyle("Normal Text Color", Color.BLACK, FontStyle.PLAIN);

			normalTextColorAndStyle.fontStyleComboBox.addActionListener(
					new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setSelected(getFontStyleComboBox(),
							(String) normalTextColorAndStyle.fontStyleComboBox.getSelectedItem());
				}
			});
		}
		return normalTextColorAndStyle;
	}

	private ColorAndStyle getQuotedTextColorAndStyle() {
		if (quotedTextColorAndStyle == null) {
			quotedTextColorAndStyle = new ColorAndStyle("Quoted Text Color", new Color(0x7F, 0x00, 0x00),
					FontStyle.PLAIN);
		}
		return quotedTextColorAndStyle;
	}

	private ColorAndStyle getKeywordColorAndStyle() {
		if (keywordColorAndStyle == null) {
			keywordColorAndStyle = new ColorAndStyle("Keyword Color", new Color(0x00, 0x00, 0xAF), FontStyle.BOLD);
		}
		return keywordColorAndStyle;
	}

	private ColorAndStyle getLiteralColorAndStyle() {
		if (literalColorAndStyle == null) {
			literalColorAndStyle = new ColorAndStyle("Literal Color", new Color(0x00, 0x00, 0xAF), FontStyle.BOLD);
		}
		return literalColorAndStyle;
	}

	private ColorAndStyle getAnnotationColorAndStyle() {
		if (annotationColorAndStyle == null) {
			annotationColorAndStyle = new ColorAndStyle("Annotation Color", new Color(0x7F, 0x7F, 0x7F), FontStyle.PLAIN);
		}
		return annotationColorAndStyle;
	}

	private ColorAndStyle getCommentColorAndStyle() {
		if (commentColorAndStyle == null) {
			commentColorAndStyle = new ColorAndStyle("Comment Color", new Color(0x00, 0xAF, 0x00), FontStyle.PLAIN);
		}
		return commentColorAndStyle;
	}

	private JComboBox getThemeComboBox() {
		if (themeComboBox == null) {
			themeComboBox = new JComboBox(new String[] { "Theme 1", "Theme 2", "Theme 3" });
		}
		return themeComboBox;
	}

	private void setPreviewLabelFont(JPanel panel) {
		String fontName = (String) getFontNameComboBox().getSelectedItem();
		int fontStyleFlags = FontStyle.fromName((String) getFontStyleComboBox().getSelectedItem()).getFontStyleFlags();
		int fontSize = Integer.parseInt((String) getFontSizeComboBox().getSelectedItem());

		Font font = new Font(fontName, fontStyleFlags, fontSize);
		
		getPreviewLabel().setFont(font);

		FontMetrics fontMetrics = panel.getFontMetrics(font);

		int width = fontMetrics.stringWidth(getPreviewLabel().getText());
		int height = fontMetrics.getHeight();

		getPreviewLabel().setPreferredSize(new Dimension(width, height));		
	}

	private void setInitialSelectedFont() {
		Font font = EditorPropertiesManager.getEditorProperties().getFont();

		// Set selected font name
		String fontName = font.getFamily();
		setSelected(getFontNameComboBox(), fontName);

		// Set selected font style
		int fontStyleFlags = font.getStyle();
		setSelected(getFontStyleComboBox(), FontStyle.fromStyleFlags(fontStyleFlags));

		// Set selected font size
		int fontSize = font.getSize();
		setSelected(getFontSizeComboBox(), "" + fontSize);
	}
	
	private Font getSelectedFont() {
		String fontName = (String) fontNameComboBox.getSelectedItem();
		int fontStyle = FontStyle.fromName((String) fontStyleComboBox.getSelectedItem()).getFontStyleFlags();
		int fontSize = Integer.parseInt((String) fontSizeComboBox.getSelectedItem());
		
		return new Font(fontName, fontStyle, fontSize);
	}

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(getOkButton())) {
				EditorProperties editorProperties = EditorPropertiesManager.getEditorProperties();
				editorProperties.setFont(getSelectedFont());
				EditorPropertiesManager.saveEditorProperties();
				dispose();
			}
			if (e.getSource().equals(getCancelButton())) {
				dispose();
			}
		}
	}

	private List<String> getMonospacedFontNames() {
		List<String> monospacedFontNames = new ArrayList<String>();
		
		Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font font : allFonts) {
			if (isMonospaced(font)) {
				monospacedFontNames.add(font.getFontName());
			}
		}
		return monospacedFontNames;
	}
	
	private boolean isMonospaced(Font font) {
		FontMetrics fontMetrics = getFontMetrics(font);

		int lastCharWidth = -1;

		for (int charCode = 0; charCode < 128; charCode++) {
			if (Character.isValidCodePoint(charCode) && (Character.isLetter(charCode) || Character.isDigit(charCode))) {
				int charWidth = fontMetrics.charWidth((char) charCode);
				if (lastCharWidth >= 0 && charWidth != lastCharWidth) {
					return false; // font is not mono-spaced
				}
				lastCharWidth = charWidth;
			}
		}
		return true; // font is mono-spaced
	}

	private static void setSelected(JComboBox comboBox, String name) {
		ComboBoxModel model = comboBox.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String itemName = (String) model.getElementAt(i);
			if (itemName.equalsIgnoreCase(name)) {
				model.setSelectedItem(itemName);
				break;
			}
		}
	}

	private static void setSelected(JComboBox comboBox, FontStyle fontStyle) {
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

	private static JComboBox createFontStyleComboBox(FontStyle fontStyle) {
		List<String> fontStyles = new ArrayList<String>();
		for (FontStyle style : FontStyle.values()) {
			fontStyles.add(style.getName());
		}
		JComboBox comboBox = new JComboBox(fontStyles.toArray());
		comboBox.setRenderer(new FontStyleCellRenderer());

		setSelected(comboBox, fontStyle);

		return comboBox;
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

	private static void addColoredButtonAndFontStyleToPanel(ColorAndStyle group, JPanel panel, GridBagConstraints gbc) {
		gbc.gridx = 0;
		panel.add(group.coloredButton, gbc);

		gbc.gridx = 1;
		panel.add(group.label, gbc);

		if (group.fontStyleComboBox != null) {
			gbc.gridx = 2;
			panel.add(group.fontStyleComboBox, gbc);
		}
	}

	private static class ColorAndStyle {
		final JLabel label;
		final JButton coloredButton;
		final JComboBox fontStyleComboBox;

		ColorAndStyle(String label, Color color, FontStyle fontStyle) {
			this.label = new JLabel(label);
			this.coloredButton = createColoredButton(color);
			this.fontStyleComboBox = fontStyle == null ? null : createFontStyleComboBox(fontStyle);
		}
	}
	

	private class FontCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			Font font = new Font((String) value, Font.PLAIN, 20);
			if (font.canDisplayUpTo(label.getText()) == -1) { // -1 means, can display all of the string
				label.setFont(font);
			}
			return label;
		}
	}


	private static class FontStyleCellRenderer extends DefaultListCellRenderer {
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
