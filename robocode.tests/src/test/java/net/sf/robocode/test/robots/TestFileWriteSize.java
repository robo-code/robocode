/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Assert;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFileWriteSize extends RobotTestBed {

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

		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Data quota: 200000\n")) {
			messagedDataQuota = true;	
		}
		
		if (out.contains("Data directory: " + absolutePath + "\\target\\classes\\tested\\robots\\FileWriteSize.data")) {
			messagedDataDirectory = true;	
		} else if (out.contains("Data directory: " + absolutePath + "/target/classes/tested/robots/FileWriteSize.data")) {
			messagedDataDirectory = true;	
		}

		if (out.contains("Data file: " + absolutePath + "\\target\\classes\\tested\\robots\\FileWriteSize.data\\test")) {
			messagedDataFile = true;	
		} else if (out.contains("Data files: " + absolutePath + "/target/classes/tested/robots/FileWriteSize.data/test")) {
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
