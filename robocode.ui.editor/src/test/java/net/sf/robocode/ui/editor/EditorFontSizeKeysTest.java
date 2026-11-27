/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;

import net.sf.robocode.ui.editor.theme.EditorThemePropertiesManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Tests that the font size of the editor can be changed with Ctrl+Plus, Ctrl+Minus and Ctrl+0 (Cmd on macOS), where
 * Plus works with both a keyboard where Plus has its own key, and a US keyboard, where Plus is Shift+'='.
 */
class EditorFontSizeKeysTest {

	private RobocodeEditorMenuBar menuBar;

	@BeforeEach
	void setUp() {
		// The menu shortcut key cannot be read without a display, e.g. on a build server
		assumeFalse(GraphicsEnvironment.isHeadless(), "Needs a display");

		EditorThemePropertiesManager.setEditorFontSize(null);
		menuBar = new RobocodeEditorMenuBar(null);
	}

	@AfterEach
	void tearDown() {
		EditorThemePropertiesManager.setEditorFontSize(null);
	}

	@Test
	void menuShowsTheStandardKeys() {
		assertEquals(key(KeyEvent.VK_PLUS, 0), menuItem("Increase Editor Font Size").getAccelerator());
		assertEquals(key(KeyEvent.VK_MINUS, 0), menuItem("Decrease Editor Font Size").getAccelerator());
		assertEquals(key(KeyEvent.VK_0, 0), menuItem("Reset Editor Font Size").getAccelerator());
	}

	@Test
	void plusOnAUsKeyboardIncreasesTheFontSize() {
		int themeSize = EditorThemePropertiesManager.getEditorFont().getSize();

		pressKey(key(KeyEvent.VK_EQUALS, 0));
		int afterEquals = EditorThemePropertiesManager.getEditorFont().getSize();
		pressKey(key(KeyEvent.VK_EQUALS, InputEvent.SHIFT_MASK));
		int afterShiftEquals = EditorThemePropertiesManager.getEditorFont().getSize();

		assertEquals(EditorThemeConfigDialog.adjacentFontSize(themeSize, true), afterEquals);
		assertEquals(EditorThemeConfigDialog.adjacentFontSize(afterEquals, true), afterShiftEquals);
	}

	@Test
	void menuItemsChangeAndResetTheFontSize() {
		int themeSize = EditorThemePropertiesManager.getEditorFont().getSize();

		menuItem("Increase Editor Font Size").doClick();
		assertEquals(EditorThemeConfigDialog.adjacentFontSize(themeSize, true),
				EditorThemePropertiesManager.getEditorFont().getSize());

		menuItem("Reset Editor Font Size").doClick();
		assertEquals(themeSize, EditorThemePropertiesManager.getEditorFont().getSize());

		menuItem("Decrease Editor Font Size").doClick();
		assertEquals(EditorThemeConfigDialog.adjacentFontSize(themeSize, false),
				EditorThemePropertiesManager.getEditorFont().getSize());
	}

	private static KeyStroke key(int keyCode, int extraModifiers) {
		return KeyStroke.getKeyStroke(keyCode, MENU_SHORTCUT_KEY_MASK | extraModifiers);
	}

	private JMenuItem menuItem(String text) {
		JMenu viewMenu = menuBar.getMenu(2);
		for (int i = 0; i < viewMenu.getItemCount(); i++) {
			JMenuItem item = viewMenu.getItem(i);
			if (item != null && text.equals(item.getText())) {
				return item;
			}
		}
		throw new AssertionError("No menu item: " + text);
	}

	private void pressKey(KeyStroke key) {
		Object actionKey = menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(key);
		assertNotNull(actionKey, "No action for " + key);
		Action action = menuBar.getActionMap().get(actionKey);
		assertNotNull(action, "No action for " + key);
		action.actionPerformed(new ActionEvent(menuBar, ActionEvent.ACTION_PERFORMED, null));
	}
}
