/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;


/**
 * Manages the editor theme property files.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public class EditorThemePropertiesManager {

	private static final String EDITOR_THEME_PROPERTIES_FILE_EXT = ".properties";

	private static final List<IEditorPropertyChangeListener> listeners = new ArrayList<IEditorPropertyChangeListener>();

	private static EditorThemeProperties editorThemeProperties;

	public static void addListener(IEditorPropertyChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static void removeListener(IEditorPropertyChangeListener listener) {
		listeners.remove(listener);
	}

	public static void notifyChange(IEditorThemeProperties properties) {
		for (IEditorPropertyChangeListener listener : listeners) {
			listener.onChange(properties);
		}
	}

	/**
	 * Returns the current editor theme properties.
	 *
	 * @param filepath the filepath of an existing editor theme properties file used for initializing the
	 *                 theme properties or null if the current values must be used as is.
	 * @return editor theme properties.
	 */
	public static EditorThemeProperties getEditorThemeProperties(File filepath) {
		if (editorThemeProperties == null) {
			editorThemeProperties = new EditorThemeProperties();
		}
		if (filepath != null) {
			FileInputStream in = null;
			File file = null;
			try {
				in = new FileInputStream(filepath);
				editorThemeProperties.load(in);
			} catch (FileNotFoundException e) {
				logMessage("Editor theme properties file was not found. A new one will be created.");
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
		return editorThemeProperties;
	}
	
	public static void saveEditorThemeProperties(File filepath) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filepath);
			getEditorThemeProperties(null).store(out, "Robocode Editor Theme Properties");
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
