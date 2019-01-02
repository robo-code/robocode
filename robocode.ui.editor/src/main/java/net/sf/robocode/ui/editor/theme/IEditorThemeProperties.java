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


/**
 * Interface for editor theme properties.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public interface IEditorThemeProperties {

	String getThemeName();

	Font getFont();
	String getFontName();
	int getFontSize();
	
	Color getBackgroundColor();

	Color getLineNumberBackgroundColor();
	Color getLineNumberTextColor();

	Color getHighlightedLineColor();

	Color getSelectionColor();
	Color getSelectedTextColor();

	Color getNormalTextColor();
	FontStyle getNormalTextStyle();
	
	Color getQuotedTextColor();
	FontStyle getQuotedTextStyle();

	Color getKeywordTextColor();
	FontStyle getKeywordTextStyle();

	Color getLiteralTextColor();
	FontStyle getLiteralTextStyle();

	Color getAnnotationTextColor();
	FontStyle getAnnotationTextStyle();

	Color getCommentTextColor();
	FontStyle getCommentTextStyle();
}
