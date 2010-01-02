/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robotscs;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.Logger;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.version.VersionManager;
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
		return "samplecs.Walls,samplecs.SittingDuck";
	}

	@Override
	protected void runSetup() {
        final IVersionManager vm = new VersionManager(null);
        file = new File("../../../robocode.tests.robots/target/classes/.robotcache/robocode.dotnet.samples-" + vm.getVersion() + ".dll_/samplecs/count.dat");
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