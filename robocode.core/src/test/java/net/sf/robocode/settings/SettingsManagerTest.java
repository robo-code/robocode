/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.settings;

import net.sf.robocode.io.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;

import static net.sf.robocode.settings.ISettingsManager.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the settings when there is no robocode.properties yet, e.g. the first time Robocode runs after installing it.
 */
class SettingsManagerTest {

	private static final String[] SYSTEM_PROPERTIES = {
			OPTIONS_BATTLE_DESIREDTPS, OPTIONS_DEVELOPMENT_PATH, OPTIONS_COMMON_ENABLE_REPLAY_RECORDING,
			OPTIONS_COMMON_ENABLE_AUTO_RECORDING, OPTIONS_COMMON_RECORDING_FORMAT
	};

	@TempDir
	Path robocodeDir;

	private File oldCwd;

	@BeforeEach
	void setUp() throws Exception {
		oldCwd = FileUtil.getCwd();
		FileUtil.setCwd(robocodeDir.toFile());
		clearSystemProperties();
	}

	@AfterEach
	void tearDown() throws Exception {
		clearSystemProperties();
		if (oldCwd != null) {
			FileUtil.setCwd(oldCwd);
		}
	}

	@Test
	void usesTheDefaultsWithoutSystemProperties() {
		SettingsManager settings = new SettingsManager();

		assertEquals(30, settings.getOptionsBattleDesiredTPS());
		assertTrue(settings.getOptionsDevelopmentPaths().isEmpty());
		assertFalse(settings.getOptionsCommonEnableReplayRecording());
		assertFalse(settings.getOptionsCommonEnableAutoRecording());
		assertEquals("bin", settings.getOptionsCommonRecordingFormat());
		assertEquals("", settings.getFileThemeMusic());
	}

	@Test
	void usesTheSystemProperties() {
		String developmentPath = robocodeDir.resolve("dev").toString();

		System.setProperty(OPTIONS_BATTLE_DESIREDTPS, "10000");
		System.setProperty(OPTIONS_DEVELOPMENT_PATH, developmentPath);
		System.setProperty(OPTIONS_COMMON_ENABLE_REPLAY_RECORDING, "true");
		System.setProperty(OPTIONS_COMMON_ENABLE_AUTO_RECORDING, "true");
		System.setProperty(OPTIONS_COMMON_RECORDING_FORMAT, "csv");

		SettingsManager settings = new SettingsManager();

		assertEquals(10000, settings.getOptionsBattleDesiredTPS());
		assertEquals(Collections.singleton(developmentPath), settings.getOptionsDevelopmentPaths());
		assertTrue(settings.getOptionsCommonEnableReplayRecording());
		assertTrue(settings.getOptionsCommonEnableAutoRecording());
		assertEquals("csv", settings.getOptionsCommonRecordingFormat());
	}

	private static void clearSystemProperties() {
		for (String property : SYSTEM_PROPERTIES) {
			System.clearProperty(property);
		}
	}
}
