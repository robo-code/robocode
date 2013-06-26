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


public class EditorPropertiesManager {

	private static final List<IEditorPropertyChangeListener> listeners = new ArrayList<IEditorPropertyChangeListener>();

	private static EditorProperties editorProperties;

	public static void addListener(IEditorPropertyChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static void removeListener(IEditorPropertyChangeListener listener) {
		listeners.remove(listener);
	}

	public static void notifyFontChanged(Font newFont) {
		for (IEditorPropertyChangeListener listener : listeners) {
			listener.onFontChanged(newFont);
		}
	}

	public static EditorProperties getEditorProperties() {
		if (editorProperties == null) {
			editorProperties = new EditorProperties();

			FileInputStream in = null;
			File file = null;
			try {
				file = FileUtil.getEditorConfigFile();
				in = new FileInputStream(file);
				editorProperties.load(in);
			} catch (FileNotFoundException e) {
				logMessage("Editor configuration file was not found. A new one will be created.");
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
		return editorProperties;
	}
	
	public static void saveEditorProperties() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(FileUtil.getEditorConfigFile());
			getEditorProperties().store(out, "Robocode Compiler Properties");
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
}
