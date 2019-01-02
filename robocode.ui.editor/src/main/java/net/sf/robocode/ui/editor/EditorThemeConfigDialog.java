/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.ui.editor.theme.ColorAndStyle;
import net.sf.robocode.ui.editor.theme.ColorAndStyleAdapter;
import net.sf.robocode.ui.editor.theme.ComboBoxUtil;
import net.sf.robocode.ui.editor.theme.EditorPropertiesManager;
import net.sf.robocode.ui.editor.theme.EditorThemeProperties;
import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import net.sf.robocode.ui.editor.theme.IEditorThemeProperties;

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
public class EditorThemeConfigDialog extends JDialog {

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
	private ColorAndStyle lineNumberBackgroundColorAndStyle;
	private ColorAndStyle lineNumberTextColorAndStyle;
	private ColorAndStyle highlightedLineColorAndStyle;
	private ColorAndStyle selectionColorAndStyle;
	private ColorAndStyle selectedTextColorAndStyle;
	private ColorAndStyle normalTextColorAndStyle;
	private ColorAndStyle quotedTextColorAndStyle;
	private ColorAndStyle keywordTextColorAndStyle;
	private ColorAndStyle literalTextColorAndStyle;
	private ColorAndStyle annotationTextColorAndStyle;
	private ColorAndStyle commentTextColorAndStyle;
	
	private final EventHandler eventHandler = new EventHandler();

	public EditorThemeConfigDialog(JFrame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		setTitle("Editor Theme Configurator");
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
		addColoredButtonAndFontStyleToPanel(getLineNumberBackgroundColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getLineNumberTextColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getHighlightedLineColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getSelectionColorAndStyle(), colorButtonsPanel, gbc2);

		gbc2.gridy = gridy2++;
		addColoredButtonAndFontStyleToPanel(getSelectedTextColorAndStyle(), colorButtonsPanel, gbc2);

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
			saveButton = new JButton("Save & Close");
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

			fontNameComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

					String oldFontName = themeProps.getFontName();
					String newFontName = ComboBoxUtil.getSelectedName(fontNameComboBox);
					if (!newFontName.equals(oldFontName)) {
						String oldThemeName = themeProps.getThemeName();
						String newThemeName = ComboBoxUtil.getSelectedName(getThemeComboBox());
						if (newThemeName.equals(oldThemeName)) {
							themeProps.setFontName(newFontName);
							updateSaveButton();
						}
					}
				}
			});
		}
		return fontNameComboBox;
	}

	private JComboBox getFontStyleComboBox() {
		if (fontStyleComboBox == null) {
			fontStyleComboBox = ComboBoxUtil.createFontStyleComboBox(FontStyle.PLAIN);

			fontStyleComboBox.addActionListener(
					new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

					FontStyle oldFontStyle = themeProps.getNormalTextStyle();
					FontStyle newFontStyle = ComboBoxUtil.getSelectedStyle(getFontStyleComboBox());
					if (newFontStyle != oldFontStyle) {
						themeProps.setNormalTextStyle(newFontStyle);

						ComboBoxUtil.setSelected(getNormalTextColorAndStyle().getFontStyleComboBox(),
								(String) getFontStyleComboBox().getSelectedItem());

						updateSaveButton();
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
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

					int oldFontSize = themeProps.getFontSize();
					int newFontSize = Integer.parseInt((String) getFontSizeComboBox().getSelectedItem());
					if (newFontSize != oldFontSize) {
						themeProps.setFontSize(newFontSize);
						updateSaveButton();
					}
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
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			backgroundColorAndStyle = new ColorAndStyle("Background Color", props.getBackgroundColor(), null);
			backgroundColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setBackgroundColor(newColor);

					updateSaveButton();
				}
			});
		}
		return backgroundColorAndStyle;
	}

	private ColorAndStyle getLineNumberBackgroundColorAndStyle() {
		if (lineNumberBackgroundColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			lineNumberBackgroundColorAndStyle = new ColorAndStyle("Line Number Background Color",
					props.getLineNumberBackgroundColor(), null);
			lineNumberBackgroundColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setLineNumberBackgroundColor(newColor);

					updateSaveButton();
				}
			});
		}
		return lineNumberBackgroundColorAndStyle;
	}

	private ColorAndStyle getLineNumberTextColorAndStyle() {
		if (lineNumberTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			lineNumberTextColorAndStyle = new ColorAndStyle("Line Number Text Color", props.getLineNumberTextColor(),
					null);
			lineNumberTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setLineNumberTextColor(newColor);

					updateSaveButton();
				}
			});
		}
		return lineNumberTextColorAndStyle;
	}

	private ColorAndStyle getHighlightedLineColorAndStyle() {
		if (highlightedLineColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			highlightedLineColorAndStyle = new ColorAndStyle("Highlighted Line Color", props.getHighlightedLineColor(),
					null);
			highlightedLineColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setHighlightedLineColor(newColor);
					
					updateSaveButton();
				}
			});
		}
		return highlightedLineColorAndStyle;
	}

	private ColorAndStyle getSelectionColorAndStyle() {
		if (selectionColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			selectionColorAndStyle = new ColorAndStyle("Selection Color", props.getSelectionColor(), null);
			selectionColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setSelectionColor(newColor);

					updateSaveButton();
				}
			});
		}
		return selectionColorAndStyle;
	}

	private ColorAndStyle getSelectedTextColorAndStyle() {
		if (selectedTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			selectedTextColorAndStyle = new ColorAndStyle("Selected Text Color", props.getSelectedTextColor(), null);
			selectedTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setSelectedTextColor(newColor);
				}
			});
		}
		return selectedTextColorAndStyle;
	}

	private ColorAndStyle getNormalTextColorAndStyle() {
		if (normalTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			normalTextColorAndStyle = new ColorAndStyle("Normal Text Color", props.getNormalTextColor(),
					props.getNormalTextStyle());
			normalTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setNormalTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setNormalTextStyle(newStyle);

					updateSaveButton();

					// Make sure to update the font style combo box
					ComboBoxUtil.setSelected(getFontStyleComboBox(), newStyle);
				}
			});
		}
		return normalTextColorAndStyle;
	}

	private ColorAndStyle getQuotedTextColorAndStyle() {
		if (quotedTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			quotedTextColorAndStyle = new ColorAndStyle("Quoted Text Color", props.getQuotedTextColor(),
					props.getQuotedTextStyle());
			quotedTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setQuotedTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setQuotedTextStyle(newStyle);

					updateSaveButton();
				}
			});
		}
		return quotedTextColorAndStyle;
	}

	private ColorAndStyle getKeywordTextColorAndStyle() {
		if (keywordTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			keywordTextColorAndStyle = new ColorAndStyle("Keyword Color", props.getKeywordTextColor(),
					props.getKeywordTextStyle());
			keywordTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setKeywordTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setKeywordTextStyle(newStyle);

					updateSaveButton();
				}
			});
		}
		return keywordTextColorAndStyle;
	}

	private ColorAndStyle getLiteralTextColorAndStyle() {
		if (literalTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			literalTextColorAndStyle = new ColorAndStyle("Literal Color", props.getLiteralTextColor(),
					props.getLiteralTextStyle());
			literalTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setLiteralTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setLiteralTextStyle(newStyle);

					updateSaveButton();
				}
			});
		}
		return literalTextColorAndStyle;
	}

	private ColorAndStyle getAnnotationTextColorAndStyle() {
		if (annotationTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			annotationTextColorAndStyle = new ColorAndStyle("Annotation Color", props.getAnnotationTextColor(),
					props.getAnnotationTextStyle());
			annotationTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setAnnotationTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setAnnotationTextStyle(newStyle);

					updateSaveButton();
				}
			});
		}
		return annotationTextColorAndStyle;
	}

	private ColorAndStyle getCommentTextColorAndStyle() {
		if (commentTextColorAndStyle == null) {
			EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			commentTextColorAndStyle = new ColorAndStyle("Comment Color", props.getCommentTextColor(),
					props.getCommentTextStyle());
			commentTextColorAndStyle.addListener(new ColorAndStyleAdapter() {
				@Override
				public void colorChanged(Color newColor) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setCommentTextColor(newColor);

					updateSaveButton();
				}

				@Override
				public void styleChanged(FontStyle newStyle) {
					EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
					themeProps.setCommentTextStyle(newStyle);

					updateSaveButton();
				}
			});
		}
		return commentTextColorAndStyle;
	}

	private JComboBox getThemeComboBox() {
		if (themeComboBox == null) {
			final String fileExt = EditorThemePropertiesManager.getFileExt();
			
			File[] themeFiles = FileUtil.getEditorThemeConfigDir().listFiles(new java.io.FileFilter() {
				public boolean accept(File filepath) {
					return filepath.getName().endsWith(fileExt);
				}
			});

			List<String> themeNames = new ArrayList<String>();
			for (File file : themeFiles) {
				String themeName = file.getName();
				themeName = themeName.substring(0, themeName.lastIndexOf(fileExt));
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
		IEditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

		// Set selected theme
		ComboBoxUtil.setSelected(getThemeComboBox(), themeProps.getThemeName());

		// Set selected font name
		ComboBoxUtil.setSelected(getFontNameComboBox(), themeProps.getFontName());

		// Set selected font style
		ComboBoxUtil.setSelected(getFontStyleComboBox(), themeProps.getNormalTextStyle());

		// Set selected font size
		ComboBoxUtil.setSelected(getFontSizeComboBox(), "" + themeProps.getFontSize());
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

	private static void addColoredButtonAndFontStyleToPanel(ColorAndStyle group, JPanel panel, GridBagConstraints gbc) {
		gbc.gridx = 0;
		panel.add(group.getColoredButton(), gbc);

		gbc.gridx = 1;
		panel.add(group.getLabel(), gbc);

		if (group.getFontStyleComboBox() != null) {
			gbc.gridx = 2;
			panel.add(group.getFontStyleComboBox(), gbc);
		}
	}

	private void udateComponentsTheme(final String themeName) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Store current theme name
				EditorPropertiesManager.getEditorProperties().setThemeName(themeName);
				EditorPropertiesManager.saveEditorProperties();

				// Load theme properties
				File filepath = EditorThemePropertiesManager.getFilepath(themeName);
				EditorThemePropertiesManager.loadEditorThemeProperties(filepath);

				EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

				ComboBoxUtil.setSelected(getFontNameComboBox(), themeProps.getFontName());
				ComboBoxUtil.setSelected(getFontSizeComboBox(), "" + themeProps.getFontSize());

				getBackgroundColorAndStyle().setSelectedColor(themeProps.getBackgroundColor());

				getLineNumberBackgroundColorAndStyle().setSelectedColor(themeProps.getLineNumberBackgroundColor());
				getLineNumberTextColorAndStyle().setSelectedColor(themeProps.getLineNumberTextColor());

				getHighlightedLineColorAndStyle().setSelectedColor(themeProps.getHighlightedLineColor());

				getSelectionColorAndStyle().setSelectedColor(themeProps.getSelectionColor());
				getSelectedTextColorAndStyle().setSelectedColor(themeProps.getSelectedTextColor());

				getNormalTextColorAndStyle().setSelectedColor(themeProps.getNormalTextColor());
				getNormalTextColorAndStyle().setSelectedStyle(themeProps.getNormalTextStyle());

				getCommentTextColorAndStyle().setSelectedColor(themeProps.getCommentTextColor());
				getCommentTextColorAndStyle().setSelectedStyle(themeProps.getCommentTextStyle());

				getQuotedTextColorAndStyle().setSelectedColor(themeProps.getQuotedTextColor());
				getQuotedTextColorAndStyle().setSelectedStyle(themeProps.getQuotedTextStyle());

				getKeywordTextColorAndStyle().setSelectedColor(themeProps.getKeywordTextColor());
				getKeywordTextColorAndStyle().setSelectedStyle(themeProps.getKeywordTextStyle());

				getLiteralTextColorAndStyle().setSelectedColor(themeProps.getLiteralTextColor());
				getLiteralTextColorAndStyle().setSelectedStyle(themeProps.getLiteralTextStyle());

				getAnnotationTextColorAndStyle().setSelectedColor(themeProps.getAnnotationTextColor());
				getAnnotationTextColorAndStyle().setSelectedStyle(themeProps.getAnnotationTextStyle());

				updateSaveButton();
			}
		});
	}

	private void updateSaveButton() {
		EditorThemeProperties props = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
		boolean enabled = props.isChanged();
		getSaveButton().setEnabled(enabled);
	}
	
	private void performThemeComboBoxAction() {
		EditorThemeProperties currentThemeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();

		String oldThemeName = currentThemeProps.getThemeName();
		String newThemeName = (String) getThemeComboBox().getSelectedItem();
		if (!oldThemeName.equals(newThemeName)) {
			if (currentThemeProps.isChanged()) {
				Object[] options = { "Yes, save changes", "No, forget changes", "Cancel"};

				int option = JOptionPane.showOptionDialog(null,
						"Changes have been made to the theme:\n" + oldThemeName
						+ "\nDo you want save the changes to this theme?",
						"Warning",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);

				if (option == JOptionPane.YES_OPTION) {
					File filepath = EditorThemePropertiesManager.getFilepath(oldThemeName);
					if (filepath != null && !filepath.exists()) {
						try {
							filepath.createNewFile();
						} catch (IOException e) {
							Logger.logError(e);
							return;
						}
					}
					EditorThemePropertiesManager.saveEditorThemeProperties(filepath);

				} else if (option == JOptionPane.CANCEL_OPTION) {
					ComboBoxUtil.setSelected(getThemeComboBox(), oldThemeName);
					return;
				}
			}
			// Switch to new theme
			udateComponentsTheme(newThemeName);
		}
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

			ComboBoxUtil.setSelected(getThemeComboBox(), themeName);
			EditorPropertiesManager.getEditorProperties().setThemeName(themeName);
			EditorPropertiesManager.saveEditorProperties();

			EditorThemeProperties themeProps = EditorThemePropertiesManager.getCurrentEditorThemeProperties();
			themeProps.setThemeName(themeName);

			themeProps.setFontName(ComboBoxUtil.getSelectedName(getFontNameComboBox()));
			themeProps.setFontSize(Integer.parseInt(ComboBoxUtil.getSelectedName(getFontSizeComboBox())));

			EditorThemePropertiesManager.saveEditorThemeProperties(file);

			dispose(); // Only when save was successful

		} else if (returnState == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(this, "The editor theme was not saved.", "Warning",
					JOptionPane.WARNING_MESSAGE);
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
}
