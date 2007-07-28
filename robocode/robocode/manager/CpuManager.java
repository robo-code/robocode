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

		long start = System.currentTimeMillis();
		long count = 0;
		double d = 0;

		while (System.currentTimeMillis() - start < TEST_PERIOD_MILLIS && d >= 0) {
			d = Math.random() * Math.random();
			count++;
		}

		double cyclesPerMS = count / (double) TEST_PERIOD_MILLIS;

		double msPerCycle = 1 / cyclesPerMS;

		cpuConstant = (int) (APPROXIMATE_CYCLES_ALLOWED * msPerCycle + .5);

		if (cpuConstant < 1) {
			cpuConstant = 1;
		}

		Logger.log("Each robot will be allowed a maximum of " + cpuConstant + " milliseconds per turn on this system.");
		manager.getProperties().setCpuConstant(cpuConstant);
		manager.saveProperties();

		WindowUtil.setStatus("");

		/*
		 17 MFlops:  32 ms
		 35 MFlops:  16 ms
		 70 MFlops:  8 ms
		 */
	}
}
