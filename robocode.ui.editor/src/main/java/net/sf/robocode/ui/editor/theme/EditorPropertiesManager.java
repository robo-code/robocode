/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;


/**
 * Manages the editor property file.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public class EditorPropertiesManager {

	private static EditorProperties editorProperties;

	public static EditorProperties getEditorProperties() {
		if (editorProperties == null) {
			editorProperties = new EditorProperties();

			FileInputStream in = null;
			File file = null;
			try {
				in = new FileInputStream(FileUtil.getEditorConfigFile());
				getEditorProperties().load(in);
			} catch (FileNotFoundException e) {
				logMessage("Editor properties file was not found. A new one will be created.");
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
			getEditorProperties().store(out, "Robocode Editor Properties");
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
