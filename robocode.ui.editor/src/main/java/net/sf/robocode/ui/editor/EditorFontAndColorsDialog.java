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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;

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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author Flemming N. Larsen (original)
 *
 * @since 1.8.3.0
 */
@SuppressWarnings("serial")
public class EditorFontAndColorsDialog extends JDialog {

	private static final boolean ONLY_MONOSPACED = true;

	private static final String[] FONT_SIZES = {
		"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"
	};

	private JButton saveButton;
	private JButton closeButton;

	private JComboBox fontNameComboBox;
	private JComboBox fontStyleComboBox;
	private JComboBox fontSizeComboBox;

	private JLabel previewLabel;

	private JComboBox themeComboBox;

	private ColorAndStyle backgroundColorAndStyle;
	private ColorAndStyle normalTextColorAndStyle;
	private ColorAndStyle quotedTextColorAndStyle;
	private ColorAndStyle keywordTextColorAndStyle;
	private ColorAndStyle literalTextColorAndStyle;
	private ColorAndStyle annotationTextColorAndStyle;
	private ColorAndStyle commentTextColorAndStyle;
	
	private final EventHandler eventHandler = new EventHandler();

	public EditorFontAndColorsDialog(JFrame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		setTitle("Font & Color Chooser");
		setLayout(new GridBagLayout());
		setResizable(false);

		// Initialize the font and theme ----
		initializeFontAndTheme();

		getSaveButton().setEnabled(false);
		
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
		addColoredButtonAndFontStyleToPanel(getCommentTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getQuotedTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getKeywordTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getLiteralTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getAnnotationTextColorAndStyle(), colorButtonsPanel, gbc2);

		// ------ OK & Cancel buttons ----

		JPanel okCancelPanel = new JPanel();
		okCancelPanel.add(getSaveButton());
		okCancelPanel.add(getCloseButton(), gbc);

		gbc.gridy = ++gridy;
		add(okCancelPanel, gbc);
	}

	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton("Save");
			saveButton.setMnemonic('S');
			saveButton.addActionListener(eventHandler);
		}
		return saveButton;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton("Close");
			closeButton.setMnemonic('C');
			closeButton.addActionListener(eventHandler);
		}
		return closeButton;
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
			if (!fontNameList.contains("Monospaced")) {
				fontNameList.add("Monospaced");
			}
			Collections.sort(fontNameList);
			
			fontNameComboBox = new JComboBox(fontNameList.toArray());
			fontNameComboBox.setRenderer(new FontCellRenderer());
			
			fontNameComboBox.addActionListener(
					new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Enable the save button, if and only if the selected font name in the font combo-box has change,
					// but no new theme was selected in the theme combo-box.
					EditorThemeProperties currentThemeProps = EditorThemePropertiesManager.getEditorThemeProperties(null);
					String currentFontName = currentThemeProps.getFontName();
					if (!((String) fontNameComboBox.getSelectedItem()).equals(currentFontName)) {
						String currentThemeName = currentThemeProps.getThemeName();
						if (((String) getThemeComboBox().getSelectedItem()).equals(currentThemeName)) {
							getSaveButton().setEnabled(true);
						}
					}
				}
			});
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
					EditorThemeProperties currentThemeProps = EditorThemePropertiesManager.getEditorThemeProperties(null);
					FontStyle currentFontStyle = currentThemeProps.getNormalTextStyle();
					if (FontStyle.fromName((String) getFontStyleComboBox().getSelectedItem()) != currentFontStyle) {
						setSelected(getNormalTextColorAndStyle().fontStyleComboBox,
								(String) getFontStyleComboBox().getSelectedItem());
						getSaveButton().setEnabled(true);
					}
				}				
			});
		}
		return fontStyleComboBox;
	}

	private JComboBox getFontSizeComboBox() {
		if (fontSizeComboBox == null) {
			fontSizeComboBox = new JComboBox(FONT_SIZES);
			fontSizeComboBox.setSelectedIndex(5);

			fontSizeComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getSaveButton().setEnabled(true);
				}
			});
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
			Color themeColor = getThemeProperties().getBackgroundColor();
			backgroundColorAndStyle = new ColorAndStyle("Background Color", themeColor, null);
		}
		return backgroundColorAndStyle;
	}

	private ColorAndStyle getNormalTextColorAndStyle() {
		if (normalTextColorAndStyle == null) {
			Color themeColor = getThemeProperties().getNormalTextColor();
			normalTextColorAndStyle = new ColorAndStyle("Normal Text Color", themeColor, FontStyle.PLAIN);

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

	private ColorAndStyle getKeywordTextColorAndStyle() {
		if (keywordTextColorAndStyle == null) {
			Color themeColor = getThemeProperties().getKeywordTextColor();
			keywordTextColorAndStyle = new ColorAndStyle("Keyword Color", themeColor, FontStyle.BOLD);
		}
		return keywordTextColorAndStyle;
	}

	private ColorAndStyle getLiteralTextColorAndStyle() {
		if (literalTextColorAndStyle == null) {
			Color themeColor = getThemeProperties().getLiteralTextColor();
			literalTextColorAndStyle = new ColorAndStyle("Literal Color", themeColor, FontStyle.BOLD);
		}
		return literalTextColorAndStyle;
	}

	private ColorAndStyle getAnnotationTextColorAndStyle() {
		if (annotationTextColorAndStyle == null) {
			Color themeColor = getThemeProperties().getAnnotationTextColor();
			annotationTextColorAndStyle = new ColorAndStyle("Annotation Color", themeColor, FontStyle.PLAIN);
		}
		return annotationTextColorAndStyle;
	}

	private ColorAndStyle getCommentTextColorAndStyle() {
		if (commentTextColorAndStyle == null) {
			Color themeColor = getThemeProperties().getCommentTextColor();
			commentTextColorAndStyle = new ColorAndStyle("Comment Color", themeColor, FontStyle.PLAIN);
		}
		return commentTextColorAndStyle;
	}

	private JComboBox getThemeComboBox() {
		if (themeComboBox == null) {
			File[] themeFiles = FileUtil.getEditorThemeConfigDir().listFiles(new java.io.FileFilter() {
				public boolean accept(File filepath) {
					return filepath.getName().endsWith(EditorThemePropertiesManager.getFileExt());
				}
			});
	
			List<String> themeNames = new ArrayList<String>();
			for (File file : themeFiles) {
				String themeName = file.getName();
				themeName = themeName.substring(0, themeName.lastIndexOf(EditorThemePropertiesManager.getFileExt()));
				themeNames.add(themeName);
			}
	
			themeComboBox = new JComboBox(themeNames.toArray());	
			themeComboBox.addActionListener(eventHandler);
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

	private void initializeFontAndTheme() {
		// Read the theme properties
		IEditorThemeProperties themeProps = getThemeProperties();

		// Set selected font name
		setSelected(getFontNameComboBox(), themeProps.getFontName());

		// Set selected font style
		setSelected(getFontStyleComboBox(), themeProps.getNormalTextStyle());

		// Set selected font size
		setSelected(getFontSizeComboBox(), "" + themeProps.getFontSize());

		// Set selected theme
		setSelected(getThemeComboBox(), themeProps.getThemeName());
	}

	private IEditorThemeProperties getThemeProperties() {
		// Get the selected theme name
		String themeName = EditorPropertiesManager.getEditorProperties().getThemeName();

		// Read the theme properties
		File themeFile = EditorThemePropertiesManager.getFilepath(themeName);
		EditorThemeProperties themeProps = EditorThemePropertiesManager.getEditorThemeProperties(themeFile);

		// Make sure the theme name is set if it has not been set already
		String themePropsName = themeProps.getThemeName();
		if (themePropsName == null || themePropsName.trim().isEmpty()) {
			themeProps.setThemeName(themeName);
		}
		return themeProps;
	}
	
	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(getThemeComboBox())) {
				performThemeComboBoxAction();
			} else if (e.getSource().equals(getSaveButton())) {
				performSaveButtonAction();
			} else if (e.getSource().equals(getCloseButton())) {
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

	private static String getSelectedName(JComboBox comboBox) {
		return (String) comboBox.getSelectedItem();
	}

	private static FontStyle getSelectedStyle(JComboBox comboBox) {
		return FontStyle.fromName((String) comboBox.getSelectedItem());
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

	private void performThemeComboBoxAction() {
		String themeName = (String) getThemeComboBox().getSelectedItem();
		File file = EditorThemePropertiesManager.getFilepath(themeName);

		EditorPropertiesManager.getEditorProperties().setThemeName(themeName);
		EditorPropertiesManager.saveEditorProperties();

		EditorThemeProperties themeProps = EditorThemePropertiesManager.getEditorThemeProperties(file);

		setSelected(getFontNameComboBox(), themeProps.getFontName());
		setSelected(getFontSizeComboBox(), "" + themeProps.getFontSize());

		getBackgroundColorAndStyle().setSelectedColor(themeProps.getBackgroundColor());

		getNormalTextColorAndStyle().setSelectedColor(themeProps.getNormalTextColor());
		getNormalTextColorAndStyle().setSelectedStyle(themeProps.getNormalTextStyle());

		getQuotedTextColorAndStyle().setSelectedColor(themeProps.getQuotedTextColor());
		getQuotedTextColorAndStyle().setSelectedStyle(themeProps.getQuotedTextStyle());

		getKeywordTextColorAndStyle().setSelectedColor(themeProps.getKeywordTextColor());
		getKeywordTextColorAndStyle().setSelectedStyle(themeProps.getKeywordTextStyle());

		getLiteralTextColorAndStyle().setSelectedColor(themeProps.getLiteralTextColor());
		getLiteralTextColorAndStyle().setSelectedStyle(themeProps.getLiteralTextStyle());

		getAnnotationTextColorAndStyle().setSelectedColor(themeProps.getAnnotationTextColor());
		getAnnotationTextColorAndStyle().setSelectedStyle(themeProps.getAnnotationTextStyle());

		getCommentTextColorAndStyle().setSelectedColor(themeProps.getCommentTextColor());
		getCommentTextColorAndStyle().setSelectedStyle(themeProps.getCommentTextStyle());
		
		themeProps.notifyChange();
	}
	
	private void performSaveButtonAction() {
		String themeName = "My theme";
		File file = EditorThemePropertiesManager.getFilepath(themeName);

		JFileChooser saveThemeDialog = new JFileChooser(FileUtil.getEditorThemeConfigDir());
		saveThemeDialog.setDialogTitle("Save Theme");
		saveThemeDialog.setSelectedFile(file);

		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File filepath) {
				return filepath.getName().endsWith(EditorThemePropertiesManager.getFileExt());
			}

			@Override
			public String getDescription() {
				return "Robocode Editor Theme properties";
			}
		};

		saveThemeDialog.addChoosableFileFilter(fileFilter);
		saveThemeDialog.setAcceptAllFileFilterUsed(false);

		int returnState = saveThemeDialog.showSaveDialog(this);

		file = saveThemeDialog.getSelectedFile();

		if (!file.getName().endsWith(EditorThemePropertiesManager.getFileExt())) {
			file = new File(file.getAbsolutePath() + EditorThemePropertiesManager.getFileExt());
		}

		if (!FileUtil.isFilenameValid(file.getName())) {
			JOptionPane.showMessageDialog(this, "Not a valid file name:\n" + file.getName(), "Error",
					JOptionPane.ERROR_MESSAGE);

		} else if (returnState == JFileChooser.APPROVE_OPTION) {
			
			if (file.exists()) {
				Object[] options = { "Yes, overwrite", "No, do not overwrite"};
				
				int option = JOptionPane.showOptionDialog(this,
						"File already exists:\n" + file.getName() + "\nDo you want to overwrite this file?", "Warning",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				
				if (option == JOptionPane.NO_OPTION) {
					return;
				}
			} else {
				try {
					file.createNewFile(); // File must be created if it does not exist already
				} catch (IOException e) {
					Logger.logError(e);
					return;
				}
			}

			setSelected(getThemeComboBox(), themeName);
			EditorPropertiesManager.getEditorProperties().setThemeName(themeName);
			EditorPropertiesManager.saveEditorProperties();

			EditorThemeProperties themeProps = EditorThemePropertiesManager.getEditorThemeProperties(null);
			themeProps.setThemeName(themeName);

			themeProps.setFontName(getSelectedName(getFontNameComboBox()));
			themeProps.setFontSize(Integer.parseInt(getSelectedName(getFontSizeComboBox())));
	
			themeProps.setBackgroundColor(getBackgroundColorAndStyle().getSelectedColor());
	
			themeProps.setNormalTextColor(getNormalTextColorAndStyle().getSelectedColor());
			themeProps.setNormalTextStyle(getNormalTextColorAndStyle().getSelectedStyle());
	
			themeProps.setQuotedTextColor(getQuotedTextColorAndStyle().getSelectedColor());
			themeProps.setQuotedTextStyle(getQuotedTextColorAndStyle().getSelectedStyle());
	
			themeProps.setKeywordTextColor(getKeywordTextColorAndStyle().getSelectedColor());
			themeProps.setKeywordTextStyle(getKeywordTextColorAndStyle().getSelectedStyle());
	
			themeProps.setLiteralTextColor(getLiteralTextColorAndStyle().getSelectedColor());
			themeProps.setLiteralTextStyle(getLiteralTextColorAndStyle().getSelectedStyle());
	
			themeProps.setAnnotationTextColor(getAnnotationTextColorAndStyle().getSelectedColor());
			themeProps.setAnnotationTextStyle(getAnnotationTextColorAndStyle().getSelectedStyle());
	
			themeProps.setCommentTextColor(getCommentTextColorAndStyle().getSelectedColor());
			themeProps.setCommentTextStyle(getCommentTextColorAndStyle().getSelectedStyle());
	
			EditorThemePropertiesManager.saveEditorThemeProperties(file);

			dispose(); // Only when save was successful

		} else if (returnState == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(this, "The editor theme was not saved.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private class ColorAndStyle {
		final JLabel label;
		final JButton coloredButton;
		final JComboBox fontStyleComboBox;

		ColorAndStyle(String label, final Color color, final FontStyle fontStyle) {
			this.label = new JLabel(label);
			this.coloredButton = createColoredButton(color);
			this.fontStyleComboBox = fontStyle == null ? null : createFontStyleComboBox(fontStyle);

			if (coloredButton != null) {
				coloredButton.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (!coloredButton.getBackground().equals(color)) {
							getSaveButton().setEnabled(true);
						}
					}
				});
			}

			if (fontStyleComboBox != null) {
				fontStyleComboBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (fontStyle != FontStyle.fromName((String) fontStyleComboBox.getSelectedItem())) {
							getSaveButton().setEnabled(true);						
						}
					}
				});
			}
		}
		
		public Color getSelectedColor() {
			return (coloredButton == null) ? null : coloredButton.getBackground();
		}

		public void setSelectedColor(Color color) {
			if (coloredButton != null) {
				coloredButton.setBackground(color);
			}
		}
		
		public FontStyle getSelectedStyle() {
			return (fontStyleComboBox == null) ? null : EditorFontAndColorsDialog.getSelectedStyle(fontStyleComboBox);
		}

		public void setSelectedStyle(FontStyle style) {
			if (fontStyleComboBox != null) {
				EditorFontAndColorsDialog.setSelected(fontStyleComboBox, style);
			}
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
