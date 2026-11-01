/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;


/**
 * @author Robert Maupin (original)
 */
public class BattlesRunnerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void deletesEverythingInTheDirectoryAndKeepsIt() throws IOException {
		File data = folder.newFolder("data");
		File robot = new File(data, "a/b/One.data");

		Assert.assertTrue(robot.mkdirs());
		Assert.assertTrue(new File(robot, "#1").createNewFile());
		Assert.assertTrue(new File(data, "loose").createNewFile());

		Assert.assertTrue(BattlesRunner.deleteContents(data));

		Assert.assertTrue(data.isDirectory());
		Assert.assertEquals(0, data.list().length);
	}

	@Test
	public void aMissingDirectoryIsFine() {
		Assert.assertTrue(BattlesRunner.deleteContents(new File(folder.getRoot(), "missing")));
	}

	@Test
	public void aPathThatCannotBeListedIsAFailure() throws IOException {
		Assert.assertFalse(BattlesRunner.deleteContents(folder.newFile("not-a-directory")));
	}
}
