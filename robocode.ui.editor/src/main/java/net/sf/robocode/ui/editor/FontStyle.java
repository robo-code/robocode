/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.awt.Font;


/**
 * Font style enumeration type, which provides an abstraction over the style flags from
 * the integer flags from the {@link Font#getStyle()} method.
 * 
 * @author Flemming N. Larsen (original)
 */
public enum FontStyle {

	PLAIN(Font.PLAIN, "Plain"),
	BOLD(Font.BOLD, "Bold"),
	ITALIC(Font.ITALIC, "Italic"),
	BOLD_ITALIC(Font.BOLD + Font.ITALIC, "Bold Italic");

	private final int fontStyleFlags;
	private final String name;

	private FontStyle(int fontStyleFlags, String name) {
		this.fontStyleFlags = fontStyleFlags;
		this.name = name;
	}

	public static FontStyle fromName(String name) {
		for (FontStyle value : values()) {
			if (value.name.equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}

	public static FontStyle fromStyleFlags(int flags) {
		for (FontStyle value : values()) {
			if (value.fontStyleFlags == flags) {
				return value;
			}
		}
		return null;
	}
	
	public int getFontStyleFlags() {
		return fontStyleFlags;
	}

	public String getName() {
		return name;
	}

	public boolean isBold() {
		return this == BOLD || this == BOLD_ITALIC;
	}

	public boolean isItalic() {
		return this == ITALIC || this == BOLD_ITALIC;
	}
}
