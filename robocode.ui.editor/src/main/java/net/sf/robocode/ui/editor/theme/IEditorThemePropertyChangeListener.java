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


public interface IEditorThemePropertyChangeListener {

	void onThemeNameChanged(String newThemeName);

	void onFontChanged(Font newFont);
	void onFontNameChanged(String newFontName);
	void onFontSizeChanged(int newFontSize);

	void onBackgroundColorChanged(Color newColor);

	void onLineNumberBackgroundColorChanged(Color newColor);
	void onLineNumberTextColorChanged(Color newColor);

	void onHighlightedLineColorChanged(Color newColor);

	void onSelectionColorChanged(Color newColor);
	void onSelectedTextColorChanged(Color newColor);

	void onNormalTextColorChanged(Color newColor);
	void onNormalTextStyleChanged(FontStyle newStyle);

	void onQuotedTextColorChanged(Color newColor);
	void onQuotedTextStyleChanged(FontStyle newStyle);

	void onKeywordTextColorChanged(Color newColor);
	void onKeywordTextStyleChanged(FontStyle newStyle);

	void onLiteralTextColorChanged(Color newColor);
	void onLiteralTextStyleChanged(FontStyle newStyle);

	void onAnnotationTextColorChanged(Color newColor);
	void onAnnotationTextStyleChanged(FontStyle newStyle);

	void onCommentTextColorChanged(Color newColor);
	void onCommentTextStyleChanged(FontStyle newStyle);
}
