/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import static net.sf.robocode.io.Logger.logError;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.ui.editor.FontStyle;


/**
 * Manages the editor theme property files.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public class EditorThemePropertiesManager {

	private static final String EDITOR_THEME_PROPERTIES_FILE_EXT = ".properties";

	private static final List<IEditorThemePropertyChangeListener> listeners = new ArrayList<IEditorThemePropertyChangeListener>();

	private static EditorThemeProperties editorThemeProperties;

	public static void addListener(IEditorThemePropertyChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static void removeListener(IEditorThemePropertyChangeListener listener) {
		listeners.remove(listener);
	}

	public static void notifyThemeNameChanged(String newThemeName) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onThemeNameChanged(newThemeName);
		}
	}

	public static void notifyFontChanged(Font newFont) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onFontChanged(newFont);
		}
	}

	public static void notifyFontNameChanged(String newFontName) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onFontNameChanged(newFontName);
		}
	}

	public static void notifyFontSizeChanged(int newFontSize) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onFontSizeChanged(newFontSize);
		}
	}

	public static void notifyBackgroundColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onBackgroundColorChanged(newColor);
		}
	}

	public static void notifyLineNumberBackgroundColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onLineNumberBackgroundColorChanged(newColor);
		}
	}

	public static void notifyLineNumberTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onLineNumberTextColorChanged(newColor);
		}
	}

	public static void notifyHighlightedLineColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onHighlightedLineColorChanged(newColor);
		}
	}

	public static void notifySelectionColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onSelectionColorChanged(newColor);
		}
	}

	public static void notifySelectedTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onSelectedTextColorChanged(newColor);
		}
	}

	public static void notifyNormalTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onNormalTextColorChanged(newColor);
		}
	}

	public static void notifyNormalTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onNormalTextStyleChanged(newStyle);
		}
	}

	public static void notifyQuotedTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onQuotedTextColorChanged(newColor);
		}
	}

	public static void notifyQuotedTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onQuotedTextStyleChanged(newStyle);
		}
	}

	public static void notifyKeywordTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onKeywordTextColorChanged(newColor);
		}
	}

	public static void notifyKeywordTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onKeywordTextStyleChanged(newStyle);
		}
	}

	public static void notifyLiteralTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onLiteralTextColorChanged(newColor);
		}
	}

	public static void notifyLiteralTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onLiteralTextStyleChanged(newStyle);
		}
	}

	public static void notifyAnnotationTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onAnnotationTextColorChanged(newColor);
		}
	}

	public static void notifyAnnotationTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onAnnotationTextStyleChanged(newStyle);
		}
	}

	public static void notifyCommentTextColorChanged(Color newColor) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onCommentTextColorChanged(newColor);
		}
	}

	public static void notifyCommentTextStyleChanged(FontStyle newStyle) {
		for (IEditorThemePropertyChangeListener listener : listeners) {
			listener.onCommentTextStyleChanged(newStyle);
		}
	}

	public static EditorThemeProperties getCurrentEditorThemeProperties() {
		if (editorThemeProperties == null) {
			editorThemeProperties = new EditorThemeProperties();

			String themeName = EditorPropertiesManager.getEditorProperties().getThemeName();
			File filepath = EditorThemePropertiesManager.getFilepath(themeName);

			loadEditorThemeProperties(filepath);
		}
		return editorThemeProperties;
	}

	public static void loadEditorThemeProperties(File filepath) {
		if (filepath != null) {
			FileInputStream in = null;
			File file = null;
			try {
				in = new FileInputStream(filepath);
				getCurrentEditorThemeProperties().load(in);
			} catch (FileNotFoundException e) {
				logError("Could not find file: " + file, e);
			} catch (IOException e) {
				logError("Error while reading file: " + file, e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ignored) {}
				}
			}
		}
	}

	public static void saveEditorThemeProperties(File filepath) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filepath);
			getCurrentEditorThemeProperties().store(out, "Robocode Editor Theme Properties");
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
		}
	}
	
	public static File getFilepath(String themeName) {
		if (themeName == null) {
			return null;
		}
		File configDirPath = FileUtil.getEditorThemeConfigDir();

		String themeFileName = themeName + EDITOR_THEME_PROPERTIES_FILE_EXT;

		return new File(configDirPath, themeFileName);
	}

	public static String getFileExt() {
		return EDITOR_THEME_PROPERTIES_FILE_EXT;
	}
}
