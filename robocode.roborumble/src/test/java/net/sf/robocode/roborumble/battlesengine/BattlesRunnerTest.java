/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * @author Robert Maupin (original)
 */
public class BattlesRunnerTest {

	@TempDir
	File folder;

	private File newFolder(String name) {
		File directory = new File(folder, name);

		Assertions.assertTrue(directory.mkdir());
		return directory;
	}

	private File newFile(String name) throws IOException {
		File file = new File(folder, name);

		Assertions.assertTrue(file.createNewFile());
		return file;
	}

	@Test
	public void deletesEverythingInTheDirectoryAndKeepsIt() throws IOException {
		File data = newFolder("data");
		File robot = new File(data, "a/b/One.data");

		Assertions.assertTrue(robot.mkdirs());
		Assertions.assertTrue(new File(robot, "#1").createNewFile());
		Assertions.assertTrue(new File(data, "loose").createNewFile());

		Assertions.assertTrue(BattlesRunner.deleteContents(data));

		Assertions.assertTrue(data.isDirectory());
		Assertions.assertEquals(0, data.list().length);
	}

	@Test
	public void aMissingDirectoryIsFine() {
		Assertions.assertTrue(BattlesRunner.deleteContents(new File(folder, "missing")));
	}

	@Test
	public void aPathThatCannotBeListedIsAFailure() throws IOException {
		Assertions.assertFalse(BattlesRunner.deleteContents(newFile("not-a-directory")));
	}

	@Test
	public void aFileLeftOpenForReadingIsDeletedAfterACollection() throws IOException {
		File data = newFolder("data");

		leaveOpenForReading(new File(data, "kept"));

		Assertions.assertTrue(BattlesRunner.clearRobotData(data, 0));
		Assertions.assertEquals(0, data.list().length);
	}

	@Test
	public void dataThatCannotBeDeletedIsAFailure() throws IOException {
		Assertions.assertFalse(BattlesRunner.clearRobotData(newFile("not-a-directory"), 0));
	}

	private static void leaveOpenForReading(File file) throws IOException {
		Assertions.assertTrue(file.createNewFile());
		new FileInputStream(file).read();
	}
}
