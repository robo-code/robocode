/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.sf.robocode.ui.editor.IEditorProperties;


/**
 * Handles the editor properties.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.8.3.0
 */
public class EditorProperties implements IEditorProperties {

	private static final String DEFAULT_THEME_NAME = "Robocode White Theme";

	private static final String THEME_NAME = "editor.theme";
	
	private String themeName;

	private final Properties props = new Properties();

	@Override
	public String getThemeName() {
		String themeName = this.themeName;
		if (themeName == null) {
			themeName = props.getProperty(THEME_NAME);
		}
		if (themeName == null) {
			themeName = DEFAULT_THEME_NAME;
		} else {
			File file = EditorThemePropertiesManager.getFilepath(themeName);
			if (!file.exists()) {
				themeName = DEFAULT_THEME_NAME;
			}
		}
		this.themeName = themeName;
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public void load(InputStream is) throws IOException {
		props.load(is);

		themeName = props.getProperty(THEME_NAME);
	}

	public void store(OutputStream os, String header) throws IOException {
		props.setProperty(THEME_NAME, themeName);
		
		props.store(os, header);
	}
}
