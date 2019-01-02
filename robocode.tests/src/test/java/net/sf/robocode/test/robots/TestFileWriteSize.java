/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import java.io.File;

import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFileWriteSize extends RobocodeTestBed {

	boolean messagedDataQuota;
	boolean messagedDataDirectory;
	boolean messagedDataFile;
	boolean messageQuotaReached;
	boolean robotTerminated;
	
	@Override
	public String getRobotNames() {
		return "tested.robots.FileWriteSize,sample.Target";
	}
	
	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();
		String path = robotsPath;

		if (File.separatorChar == '\\') {
			out = out.replaceAll("[\\\\]", "/");
			path = path.replaceAll("[\\\\]", "/");
		}
		if (out.contains("Data quota: 200000\n")) {
			messagedDataQuota = true;
		}
		if (out.contains("Data directory: " + path + "/target/classes/tested/robots/FileWriteSize.data")) {
			messagedDataDirectory = true;	
		}
		if (out.contains("Data file: " + path + "/target/classes/tested/robots/FileWriteSize.data/test")) {
			messagedDataFile = true;	
		}
		if (out.contains("You have reached your filesystem quota of: 200000 bytes")) {
			messageQuotaReached = true;
		}
		if (event.getTurnSnapshot().getRobots()[0].getEnergy() == 0) {
			robotTerminated = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("Data quota must be 200000", messagedDataQuota);
		Assert.assertTrue("Data directory path is wrong", messagedDataDirectory);
		Assert.assertTrue("Data file path is wrong", messagedDataFile);
		Assert.assertTrue("Error must be output that file quota has been exceeded", messageQuotaReached);
		Assert.assertTrue("Game must terminate the robot", robotTerminated);
	}
}
