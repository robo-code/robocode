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
import robocode.BattleResults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleResultsSaveTest {
	@TempDir
	Path directory;

	@Test
	void surfacesSaveFailureAndStillWritesToAnAvailableFile() throws IOException {
		BattleResultsTableModel results = new BattleResultsTableModel(new BattleResults[0], 3);
		Path unavailable = Files.createDirectory(directory.resolve("unavailable.csv"));

		assertThrows(IOException.class, () -> results.writeToFile(unavailable.toString(), false));

		Path available = directory.resolve("available.csv");
		results.writeToFile(available.toString(), false);
		assertTrue(Files.readString(available).contains("Results for 3 rounds"));
	}
}
