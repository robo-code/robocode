/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlePropertiesTest {
	@TempDir
	Path directory;

	@Test
	void reportsTheInvalidPropertyAndValue() throws Exception {
		Path battleFile = directory.resolve("invalid.battle");
		Files.writeString(battleFile, "robocode.battle.numRounds=ten\n");

		try (FileInputStream input = new FileInputStream(battleFile.toFile())) {
			NumberFormatException error = assertThrows(NumberFormatException.class,
					() -> new BattleProperties().load(input));
			assertTrue(error.getMessage().contains("robocode.battle.numRounds"));
			assertTrue(error.getMessage().contains("ten"));
		}
	}
}
