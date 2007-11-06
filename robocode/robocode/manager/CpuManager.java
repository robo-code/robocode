/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Changed to accept 50000 cycles instead of 1000 (introduced in v1.07),
 *       which caused too many skipped turns on existing bots that ran just fine
 *       under v1.06.
 *     - Updated to use methods from WindowUtil and Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Added calculateCpuConstant() used for (re)calculating the CPU constant
 *     - Added setCpuConstant() for calculating and setting the CPU constant
 *     - Added getMillisGranularity() for calculating the time millis
 *       granularity
 *     - Limited the CPU constant to be >= millis granularity in order to prevent
 *       robots from skipping turns as the granularity might be coarse
 *******************************************************************************/
package robocode.manager;


import robocode.dialog.WindowUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class CpuManager {
	private final static int APPROXIMATE_CYCLES_ALLOWED = 50000;
	private final static int TEST_PERIOD_MILLIS = 5000;

	private int cpuConstant = -1;
	private RobocodeManager manager;

	public CpuManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public int getCpuConstant() {
		if (cpuConstant == -1) {
			cpuConstant = manager.getProperties().getCpuConstant();
			if (cpuConstant == -1) {
				calculateCpuConstant();
			}
		}
		return cpuConstant;
	}
	
	public void calculateCpuConstant() {
		WindowUtil.setStatus("Estimating CPU speed, please wait...");

		setCpuConstant();

		Logger.log("Each robot will be allowed a maximum of " + cpuConstant + " milliseconds per turn on this system.");
		manager.getProperties().setCpuConstant(cpuConstant);
		manager.saveProperties();

		WindowUtil.setStatus("");
	}
	
	private void setCpuConstant() {
		long start = System.currentTimeMillis();
		long count = 0;
		double d = 0;

		while (System.currentTimeMillis() - start < TEST_PERIOD_MILLIS && d >= 0) {
			d = Math.random() * Math.random();
			count++;
		}

		double cyclesPerMS = count / (double) TEST_PERIOD_MILLIS;

		double msPerCycle = 1 / cyclesPerMS;

		cpuConstant = Math.max((int) (APPROXIMATE_CYCLES_ALLOWED * msPerCycle + .5), getMillisGranularity());

		if (cpuConstant < 1) {
			cpuConstant = 1;
		}
	}

	private int getMillisGranularity() {
		int granularity = 0;
		int delta = 0, i;

		// Run several times, so we find the most representative granularity
		// Sometimes, e.g. 1 out of 100 times we get a lower granularity.
		for (i = 0; i < 10; i++) {
			long time = System.currentTimeMillis();

			while ((delta = (int) (System.currentTimeMillis() - time)) == 0);
			
			granularity = Math.max(granularity, delta);
		}

		return granularity;
	}
}
