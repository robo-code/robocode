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
import robocode.control.events.TurnEndedEvent;

import java.io.File;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFileWriteSize extends RobocodeTestBed {

	boolean messagedDataQuota;
	boolean messagedDataDirectory;
	boolean messageQuotaReached;
	boolean robotTerminated;
	
	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotNames() {
		return "tested.robotscs.FileWriteSize,SampleCs.Target";
	}
	
	File file;
	@Override
	protected void runSetup() {
		file = new File(robotsPath + "\\target\\classes\\.data\\tested\\robotscs\\test.txt");
		if (file.exists()) {
			if (!file.delete()) {
				Logger.logError("Can't delete" + file);
			}
		}
	}

	@Override
	protected int getExpectedErrors() {
		return 1;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Data quota: 200000\r\n")) {
			messagedDataQuota = true;	
		}
		
		if (out.contains("Data directory: " + robotsPath + "\\target\\classes\\.data\\tested\\robotscs")) {
			messagedDataDirectory = true;	
		}

		if (out.contains("You have reached your filesystem quota: 200000 bytes per robot")) {
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
		Assert.assertTrue("Error must be output that file quota has been exceeded", messageQuotaReached);
		Assert.assertTrue("Game must terminate the robot", robotTerminated);
		Assert.assertTrue(file.exists());
		if (!file.delete()) {
			Logger.logError("Can't delete" + file);
		}
	}
}
