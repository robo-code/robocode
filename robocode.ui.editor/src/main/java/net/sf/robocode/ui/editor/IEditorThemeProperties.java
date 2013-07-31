/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import java.awt.Color;
import java.awt.Font;


/**
 * Interface for editor properties.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.2.0
 */
public interface IEditorThemeProperties {

	String getThemeName();

	Font getFont();
	String getFontName();
	int getFontSize();
	
	Color getBackgroundColor();

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
