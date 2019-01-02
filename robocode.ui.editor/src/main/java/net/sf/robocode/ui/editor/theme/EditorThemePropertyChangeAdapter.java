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

import net.sf.robocode.ui.editor.FontStyle;


public class EditorThemePropertyChangeAdapter implements IEditorThemePropertyChangeListener {

	public void onThemeNameChanged(String newThemeName) {}

	public void onFontChanged(Font newFont) {}

	public void onFontNameChanged(String newFontName) {}

	public void onFontSizeChanged(int newFontSize) {}

	public void onBackgroundColorChanged(Color newColor) {}

	public void onLineNumberBackgroundColorChanged(Color newColor) {}

	public void onLineNumberTextColorChanged(Color newColor) {}

	public void onHighlightedLineColorChanged(Color newColor) {}

	public void onSelectionColorChanged(Color newColor) {}

	public void onSelectedTextColorChanged(Color newColor) {}

	public void onNormalTextColorChanged(Color newColor) {}

	public void onNormalTextStyleChanged(FontStyle newStyle) {}

	public void onQuotedTextColorChanged(Color newColor) {}

	public void onQuotedTextStyleChanged(FontStyle newStyle) {}

	public void onKeywordTextColorChanged(Color newColor) {}

	public void onKeywordTextStyleChanged(FontStyle newStyle) {}

	public void onLiteralTextColorChanged(Color newColor) {}

	public void onLiteralTextStyleChanged(FontStyle newStyle) {}

	public void onAnnotationTextColorChanged(Color newColor) {}

	public void onAnnotationTextStyleChanged(FontStyle newStyle) {}

	public void onCommentTextColorChanged(Color newColor) {}

	public void onCommentTextStyleChanged(FontStyle newStyle) {}
}
