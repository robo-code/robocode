/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;

import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.version.IVersionManager;
import org.junit.jupiter.api.Test;
import robocode.BattleResults;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each CSV file of a battle record gets its own content.
 */
class RecordManagerCsvTest {

	@Test
	void eachCsvFileGetsItsOwnContent() throws Exception {
		HiddenAccess.init();

		IVersionManager versionManager = (IVersionManager) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { IVersionManager.class }, (proxy, method, args) -> "1.0.0");
		RecordManager recordManager = new RecordManager(null, versionManager);

		recordManager.createRecordInfo(HiddenAccess.createRules(800, 600, 1, 0.1, 450, false, 100), 1, UUID.randomUUID());
		recordManager.updateRecordInfoResults(
				Collections.singletonList(new BattleResults("sample.Walls", 1, 100, 0, 0, 0, 0, 0, 0, 1, 0, 0)));
		recordManager.cleanupStreams(); // Writes the empty list of turns to the file

		ByteArrayOutputStream results = new ByteArrayOutputStream();
		ByteArrayOutputStream rounds = new ByteArrayOutputStream();
		ByteArrayOutputStream robots = new ByteArrayOutputStream();
		ByteArrayOutputStream bullets = new ByteArrayOutputStream();

		recordManager.generateCsvRecord(results, rounds, robots, bullets, new SerializableOptions(false), null);

		assertTrue(text(results).startsWith("version,battleId,roundsCount,"), text(results));
		assertTrue(text(results).contains("sample.Walls"), text(results));
		assertTrue(text(rounds).startsWith("version,battleId,roundIndex,robotCount,"), text(rounds));
		assertTrue(text(robots).startsWith("version,battleId,roundIndex,turnIndex,robotIndex,robotName,"), text(robots));
		assertTrue(text(bullets).startsWith("version,battleId,roundIndex,turnIndex,bulletId,"), text(bullets));
	}

	private static String text(ByteArrayOutputStream output) {
		return new String(output.toByteArray(), StandardCharsets.UTF_8);
	}
}
