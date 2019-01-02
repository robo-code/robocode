/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robotscs;


import net.sf.robocode.io.Logger;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestFileWrite extends RobocodeTestBed {

	@Test
	public void run() {
		super.run();
	}
	File file;

	public String getRobotNames() {
		return "SampleCs.Walls,SampleCs.SittingDuck";
	}

	@Override
	protected void runSetup() {
		file = new File("../../../robocode.tests.robots/target/classes/.data/SampleCs/count.dat");
		if (file.exists()) {
			if (!file.delete()) {
				Logger.logError("Can't delete" + file);
			}
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(file.exists());
		if (!file.delete()) {
			Logger.logError("Can't delete" + file);
		}
	}
}
