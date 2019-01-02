/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.sf.robocode.ui.editor.FontStyle;


/**
 * Handles the editor theme properties.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public class EditorThemeProperties implements IEditorThemeProperties {

	// ---- Default Colors and Styles ----

	private static final String DEFAULT_THEME_NAME = EditorPropertiesManager.getEditorProperties().getThemeName();

	private static final String DEFAULT_FONT_NAME = "Monospaced";
	private static final int DEFAULT_FONT_SIZE = 14;

	private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

	private static final Color DEFAULT_LINE_NUMBER_BACKGROUND_COLOR = new Color(0xDD, 0xDD, 0xDD);
	private static final Color DEFAULT_LINE_NUMBER_TEXT_COLOR = Color.BLACK;

	private static final Color DEFAULT_HIGHLIGHTED_LINE_COLOR = new Color(0xFF, 0xFF, 0x7F);
	private static final Color DEFAULT_SELECTION_COLOR = new Color(0x33, 0x99, 0xFF);
	private static final Color DEFAULT_SELECTED_TEXT_COLOR = Color.WHITE;

	private static final Color DEFAULT_NORMAL_TEXT_COLOR = Color.BLACK;
	private static final FontStyle DEFAULT_NORMAL_TEXT_STYLE = FontStyle.PLAIN;
	
	private static final Color DEFAULT_COMMENT_TEXT_COLOR = new Color(0x00, 0xAF, 0x00);
	private static final FontStyle DEFAULT_COMMENT_TEXT_STYLE = FontStyle.PLAIN;

	private static final Color DEFAULT_QUOTED_TEXT_COLOR = new Color(0x7f, 0x00, 0x00);
	private static final FontStyle DEFAULT_QUOTED_TEXT_STYLE = FontStyle.PLAIN;
	
	private static final Color DEFAULT_KEYWORD_TEXT_COLOR = new Color(0x00, 0x00, 0xAF);
	private static final FontStyle DEFAULT_KEYWORD_TEXT_STYLE = FontStyle.BOLD;
	
	private static final Color DEFAULT_LITERAL_TEXT_COLOR = new Color(0x00, 0x00, 0xAF);
	private static final FontStyle DEFAULT_LITERAL_TEXT_STYLE = FontStyle.BOLD;
	
	private static final Color DEFAULT_ANNOTATION_TEXT_COLOR = new Color(0x7F, 0x7F, 0x7F);
	private static final FontStyle DEFAULT_ANNOTATION_TEXT_STYLE = FontStyle.BOLD;

	// ---- Property Names ----
	
	private static final String THEME_NAME = "editor.theme";

	private static final String FONT_NAME = "editor.font.name";
	private static final String FONT_SIZE = "editor.font.size";

	private static final String BACKGROUND_COLOR = "editor.background.color";

	private static final String LINE_NUMBER_BACKGROUND_COLOR = "editor.lineNumber.background.color";
	private static final String LINE_NUMBER_TEXT_COLOR = "editor.lineNumber.text.color";

	private static final String HIGHLIGHTED_LINE_COLOR = "editor.highlighted.line.color";

	private static final String SELECTION_COLOR = "editor.selected.background.color";
	private static final String SELECTED_TEXT_COLOR = "editor.selected.text.color";

	private static final String NORMAL_TEXT_COLOR = "editor.text.color.normal";
	private static final String NORMAL_TEXT_STYLE = "editor.text.style.normal";

	private static final String COMMENT_TEXT_COLOR = "editor.text.color.comment";
	private static final String COMMENT_TEXT_STYLE = "editor.text.style.comment";

	private static final String QUOTED_TEXT_COLOR = "editor.text.color.quoted";
	private static final String QUOTED_TEXT_STYLE = "editor.text.style.quoted";

	private static final String KEYWORD_TEXT_COLOR = "editor.text.color.keyword";
	private static final String KEYWORD_TEXT_STYLE = "editor.text.style.keyword";

	private static final String LITERAL_TEXT_COLOR = "editor.text.color.literal";
	private static final String LITERAL_TEXT_STYLE = "editor.text.style.literal";

	private static final String ANNOTATION_TEXT_COLOR = "editor.text.color.annotation";
	private static final String ANNOTATION_TEXT_STYLE = "editor.text.style.annotation";

	private String themeName;

	private String fontName;
	private Integer fontSize;

	private ColorPropertyStrategy backgroundColor = new ColorPropertyStrategy(BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
	private ColorPropertyStrategy lineNumberBackgroundColor = new ColorPropertyStrategy(LINE_NUMBER_BACKGROUND_COLOR,
			DEFAULT_LINE_NUMBER_BACKGROUND_COLOR);
	private ColorPropertyStrategy lineNumberTextColor = new ColorPropertyStrategy(LINE_NUMBER_TEXT_COLOR,
			DEFAULT_LINE_NUMBER_TEXT_COLOR);
	private ColorPropertyStrategy highlightedLineColor = new ColorPropertyStrategy(HIGHLIGHTED_LINE_COLOR,
			DEFAULT_HIGHLIGHTED_LINE_COLOR);
	private ColorPropertyStrategy selectionColor = new ColorPropertyStrategy(SELECTION_COLOR, DEFAULT_SELECTION_COLOR);
	private ColorPropertyStrategy selectedTextColor = new ColorPropertyStrategy(SELECTED_TEXT_COLOR,
			DEFAULT_SELECTED_TEXT_COLOR);
	private ColorPropertyStrategy normalTextColor = new ColorPropertyStrategy(NORMAL_TEXT_COLOR,
			DEFAULT_NORMAL_TEXT_COLOR);
	private FontStylePropertyStrategy normalTextStyle = new FontStylePropertyStrategy(NORMAL_TEXT_STYLE,
			DEFAULT_NORMAL_TEXT_STYLE);
	private ColorPropertyStrategy commentTextColor = new ColorPropertyStrategy(COMMENT_TEXT_COLOR,
			DEFAULT_COMMENT_TEXT_COLOR);
	private FontStylePropertyStrategy commentTextStyle = new FontStylePropertyStrategy(COMMENT_TEXT_STYLE,
			DEFAULT_COMMENT_TEXT_STYLE);
	private ColorPropertyStrategy quotedTextColor = new ColorPropertyStrategy(QUOTED_TEXT_COLOR,
			DEFAULT_QUOTED_TEXT_COLOR);
	private FontStylePropertyStrategy quotedTextStyle = new FontStylePropertyStrategy(QUOTED_TEXT_STYLE,
			DEFAULT_QUOTED_TEXT_STYLE);
	private ColorPropertyStrategy keywordTextColor = new ColorPropertyStrategy(KEYWORD_TEXT_COLOR,
			DEFAULT_KEYWORD_TEXT_COLOR);
	private FontStylePropertyStrategy keywordTextStyle = new FontStylePropertyStrategy(KEYWORD_TEXT_STYLE,
			DEFAULT_KEYWORD_TEXT_STYLE);
	private ColorPropertyStrategy literalTextColor = new ColorPropertyStrategy(LITERAL_TEXT_COLOR,
			DEFAULT_LITERAL_TEXT_COLOR);
	private FontStylePropertyStrategy literalTextStyle = new FontStylePropertyStrategy(LITERAL_TEXT_STYLE,
			DEFAULT_LITERAL_TEXT_STYLE);
	private ColorPropertyStrategy annotationTextColor = new ColorPropertyStrategy(ANNOTATION_TEXT_COLOR,
			DEFAULT_ANNOTATION_TEXT_COLOR);
	private FontStylePropertyStrategy annotationTextStyle = new FontStylePropertyStrategy(ANNOTATION_TEXT_STYLE,
			DEFAULT_ANNOTATION_TEXT_STYLE);
	
	private IPropertyStrategy<?>[] colorAndStyleProps = new IPropertyStrategy[] {
		backgroundColor, lineNumberBackgroundColor, lineNumberTextColor, highlightedLineColor, selectionColor,
		selectedTextColor, normalTextColor, normalTextStyle, commentTextColor, commentTextStyle, quotedTextColor,
		quotedTextStyle, keywordTextColor, keywordTextStyle, literalTextColor, literalTextStyle, annotationTextColor,
		annotationTextStyle
	};
	
	private final Properties props = new Properties();

	private boolean isChanged;

	public String getThemeName() {
		String themeName = this.themeName;
		if (themeName == null) {
			themeName = props.getProperty(THEME_NAME);
		}
		if (themeName == null) {
			themeName = DEFAULT_THEME_NAME;
		}
		this.themeName = themeName;
		return themeName;
	}

	public void setThemeName(String newName) {
		this.themeName = newName;
		EditorThemePropertiesManager.notifyThemeNameChanged(newName);
	}

	public Font getFont() {
		return new Font(getFontName(), getNormalTextStyle().getFontStyleFlags(), getFontSize());
	}
	
	public void setFontName(String newName) {
		this.fontName = newName;
		EditorThemePropertiesManager.notifyFontNameChanged(newName);
		isChanged = true;
	}

	@Override
	public String getFontName() {
		String fontName = this.fontName;
		if (fontName == null) {
			fontName = props.getProperty(FONT_NAME);
		}
		if (fontName == null) {
			fontName = DEFAULT_FONT_NAME;
		}
		this.fontName = fontName;
		return fontName;
	}

	public void setFontSize(int newSize) {
		this.fontSize = newSize;
		EditorThemePropertiesManager.notifyFontSizeChanged(newSize);
		isChanged = true;
	}

	@Override
	public int getFontSize() {
		Integer fontSize = this.fontSize;
		if (fontSize == null) {
			String value = props.getProperty(FONT_SIZE);
			try {
				fontSize = Integer.parseInt(value);
			} catch (NumberFormatException ignore) {}
		}
		if (fontSize == null) {
			fontSize = DEFAULT_FONT_SIZE;
		}
		this.fontSize = fontSize;
		return fontSize;
	}

	public void setBackgroundColor(Color newColor) {
		backgroundColor.set(newColor);
		EditorThemePropertiesManager.notifyBackgroundColorChanged(newColor);
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor.get();
	}

	public void setLineNumberBackgroundColor(Color newColor) {
		lineNumberBackgroundColor.set(newColor);
		EditorThemePropertiesManager.notifyLineNumberBackgroundColorChanged(newColor);
	}

	@Override
	public Color getLineNumberBackgroundColor() {
		return lineNumberBackgroundColor.get();
	}

	public void setLineNumberTextColor(Color newColor) {
		lineNumberTextColor.set(newColor);
		EditorThemePropertiesManager.notifyLineNumberTextColorChanged(newColor);
	}

	@Override
	public Color getLineNumberTextColor() {
		return lineNumberTextColor.get();
	}

	public void setHighlightedLineColor(Color newColor) {
		highlightedLineColor.set(newColor);
		EditorThemePropertiesManager.notifyHighlightedLineColorChanged(newColor);
	}

	@Override
	public Color getHighlightedLineColor() {
		return highlightedLineColor.get();
	}
	
	public void setSelectionColor(Color newColor) {
		selectionColor.set(newColor);
		EditorThemePropertiesManager.notifySelectionColorChanged(newColor);
	}

	@Override
	public Color getSelectionColor() {
		return selectionColor.get();
	}

	public void setSelectedTextColor(Color newColor) {
		selectedTextColor.set(newColor);
		EditorThemePropertiesManager.notifySelectedTextColorChanged(newColor);
	}

	@Override
	public Color getSelectedTextColor() {
		return selectedTextColor.get();
	}

	public void setNormalTextColor(Color newColor) {
		normalTextColor.set(newColor);
		EditorThemePropertiesManager.notifyNormalTextColorChanged(newColor);
	}

	@Override
	public Color getNormalTextColor() {
		return normalTextColor.get();
	}
	
	public void setNormalTextStyle(FontStyle newStyle) {
		normalTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyNormalTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getNormalTextStyle() {
		return normalTextStyle.get();
	}

	public void setCommentTextColor(Color newColor) {
		commentTextColor.set(newColor);
		EditorThemePropertiesManager.notifyCommentTextColorChanged(newColor);
	}

	@Override
	public Color getCommentTextColor() {
		return commentTextColor.get();
	}
	
	public void setCommentTextStyle(FontStyle newStyle) {
		commentTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyCommentTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getCommentTextStyle() {
		return commentTextStyle.get();
	}

	public void setQuotedTextColor(Color newColor) {
		quotedTextColor.set(newColor);
		EditorThemePropertiesManager.notifyQuotedTextColorChanged(newColor);
	}

	@Override
	public Color getQuotedTextColor() {
		return quotedTextColor.get();
	}
	
	public void setQuotedTextStyle(FontStyle newStyle) {
		quotedTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyQuotedTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getQuotedTextStyle() {
		return quotedTextStyle.get();
	}

	public void setKeywordTextColor(Color newColor) {
		keywordTextColor.set(newColor);
		EditorThemePropertiesManager.notifyKeywordTextColorChanged(newColor);
	}

	@Override
	public Color getKeywordTextColor() {
		return keywordTextColor.get();
	}
	
	public void setKeywordTextStyle(FontStyle newStyle) {
		keywordTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyKeywordTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getKeywordTextStyle() {
		return keywordTextStyle.get();
	}

	public void setLiteralTextColor(Color newColor) {
		literalTextColor.set(newColor);
		EditorThemePropertiesManager.notifyLiteralTextColorChanged(newColor);
	}

	@Override
	public Color getLiteralTextColor() {
		return literalTextColor.get();
	}
	
	public void setLiteralTextStyle(FontStyle newStyle) {
		literalTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyLiteralTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getLiteralTextStyle() {
		return literalTextStyle.get();
	}

	public void setAnnotationTextColor(Color newColor) {
		annotationTextColor.set(newColor);
		EditorThemePropertiesManager.notifyAnnotationTextColorChanged(newColor);
	}

	@Override
	public Color getAnnotationTextColor() {
		return annotationTextColor.get();
	}
	
	public void setAnnotationTextStyle(FontStyle newStyle) {
		annotationTextStyle.set(newStyle);
		EditorThemePropertiesManager.notifyAnnotationTextStyleChanged(newStyle);
	}

	@Override
	public FontStyle getAnnotationTextStyle() {
		return annotationTextStyle.get();
	}

	public void load(InputStream is) throws IOException {
		// Reset (clean) the property values before loading them
		for (IPropertyStrategy<?> propertyStrategy : colorAndStyleProps) {
			propertyStrategy.set(null); // clean value
			propertyStrategy.save(); // store cleaned value in property
		}

		props.load(is);

		themeName = props.getProperty(THEME_NAME, DEFAULT_THEME_NAME);
		fontName = props.getProperty(FONT_NAME, DEFAULT_FONT_NAME);
		try {
			fontSize = Integer.parseInt(props.getProperty(FONT_SIZE, "" + DEFAULT_FONT_SIZE));
		} catch (NumberFormatException e) {
			fontSize = DEFAULT_FONT_SIZE;
		}

		for (IPropertyStrategy<?> propertyStrategy : colorAndStyleProps) {
			propertyStrategy.load();
		}

		// Reset 'changed' flag
		isChanged = false;
	}

	public void store(OutputStream os, String header) throws IOException {
		props.setProperty(THEME_NAME, themeName);
		props.setProperty(FONT_NAME, fontName);
		props.setProperty(FONT_SIZE, "" + fontSize);

		for (IPropertyStrategy<?> propertyStrategy : colorAndStyleProps) {
			propertyStrategy.save();
		}

		props.store(os, header);

		// Reset 'changed' flag
		isChanged = false;
	}

	public boolean isChanged() {
		return isChanged;
	}

	interface IPropertyStrategy<V> {
		V get();
		void set(V value);
		void load();
		void save();
	}


	class ColorPropertyStrategy implements IPropertyStrategy<Color> {

		final String propertyName;
		final Color defaultColor;
		Color color;

		ColorPropertyStrategy(String propertyName, Color defaultColor) {
			this.propertyName = propertyName;
			this.defaultColor = defaultColor;
			this.color = defaultColor;
		}

		@Override
		public Color get() {
			return color == null ? defaultColor : color;
		}

		@Override
		public void set(Color color) {
			if ((color == null && this.color == null) || (color != null && color.equals(this.color))) {
				return;
			}
			this.color = color;
			isChanged = true;
		}

		@Override
		public void load() {
			color = fromPropertyToColor(propertyName);
		}

		@Override
		public void save() {
			props.setProperty(propertyName, toHexString(get()));
		}

		private Color fromPropertyToColor(String propertyName) {
			String hexValue = props.getProperty(propertyName);
			if (hexValue != null) {
				try {
					// Yes, we need to use a Long instead of an Integer here +
					// we mask out the alpha channel
					int rgb = (int) Long.parseLong(hexValue, 16) & 0x00FFFFFF;
					return new Color(rgb);
				} catch (NumberFormatException ignore) {}
			}
			return null;
		}
	}


	class FontStylePropertyStrategy implements IPropertyStrategy<FontStyle> {

		final String propertyName;
		final FontStyle defaultStyle;
		FontStyle style;

		FontStylePropertyStrategy(String propertyName, FontStyle defaultStyle) {
			this.propertyName = propertyName;
			this.defaultStyle = defaultStyle;
			this.style = defaultStyle;
		}

		@Override
		public FontStyle get() {
			return style == null ? defaultStyle : style;
		}
		
		@Override
		public void set(FontStyle style) {
			if ((style == null && this.style == null) || (style != null && style.equals(this.style))) {
				return;
			}
			this.style = style;
			isChanged = true;
		}

		@Override
		public void load() {
			style = fromPropertyToFontStyle(propertyName);
		}

		@Override
		public void save() {
			props.setProperty(propertyName, "" + get().getName());
		}

		private FontStyle fromPropertyToFontStyle(String propertyName) {
			String styleName = props.getProperty(propertyName);
			return (styleName == null) ? null : FontStyle.fromName(styleName);
		}
	}	
	
	private static String toHexString(Color color) {
		// We mask out the alpha channel and format as RRGGBB
		return String.format("%06X", color.getRGB() & 0x00FFFFFF);
	}
}
