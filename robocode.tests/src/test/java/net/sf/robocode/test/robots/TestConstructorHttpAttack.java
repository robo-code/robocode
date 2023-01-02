/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestConstructorHttpAttack extends RobocodeTestBed {

	private boolean messagedInitialization;
	private boolean securityExceptionOccurred;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.ConstructorHttpAttack";
	}

	@Override
	public String getEnemyName() {
		return "sample.Target";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("An error occurred during initialization")) {
			messagedInitialization = true;	
		}	

		if (out.contains("java.lang.SecurityException:")) {
			securityExceptionOccurred = true;	
		}	
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("Error during initialization", messagedInitialization);
		Assert.assertTrue("Socket connection is not allowed", securityExceptionOccurred);
	}

	@Override
	protected int getExpectedErrors() {
		return 2;
	}
}
