/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditorFontSizeTest {
	@Test
	void stepsThroughThemeFontSizesAndRespectsTheLimits() {
		assertEquals(16, EditorThemeConfigDialog.adjacentFontSize(14, true));
		assertEquals(14, EditorThemeConfigDialog.adjacentFontSize(15, false));
		assertEquals(16, EditorThemeConfigDialog.adjacentFontSize(15, true));
		assertEquals(8, EditorThemeConfigDialog.adjacentFontSize(8, false));
		assertEquals(72, EditorThemeConfigDialog.adjacentFontSize(72, true));
	}
}
