/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.util;


import java.awt.*;
import java.awt.event.InputEvent;


/**
 * Utility for handling shortcut keys.
 *
 * @author Flemming N. Larsen (original)
 */
public class ShortcutUtil {

	/**
	 * The menu shortcut key mask.
	 */
	public final static int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	/**
	 * Returns the text for the shortcut modifier key.
	 *
	 * @return the text for the shortcut modifier key.
	 */
	public static String getModifierKeyText() {

		boolean isMac = (System.getProperty("os.name").startsWith("Mac"));

		String text = "";

		if ((MENU_SHORTCUT_KEY_MASK & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
			text = "Shift";
		}
		if ((MENU_SHORTCUT_KEY_MASK & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
			if (text.length() > 0) {
				text += '+';
			}
			text = "Ctrl";
		}
		if ((MENU_SHORTCUT_KEY_MASK & InputEvent.ALT_MASK) == InputEvent.ALT_MASK) {
			if (text.length() > 0) {
				text += '+';
			}
			text = isMac ? "Opt" : "Alt";
		}
		if ((MENU_SHORTCUT_KEY_MASK & InputEvent.META_MASK) == InputEvent.META_MASK) {
			if (text.length() > 0) {
				text += '+';
			}
			text = isMac ? "Cmd" : "Meta";
		}

		return text;
	}
}
