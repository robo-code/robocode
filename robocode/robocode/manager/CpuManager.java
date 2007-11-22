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
 *     - Updated to use methods from WindowUtil and Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Added calculateCpuConstant() used for (re)calculating the CPU constant
 *     - Added setCpuConstant() for calculating and setting the CPU constant
 *     - Limited the CPU constant to be >= millis granularity in order to prevent
 *       robots from skipping turns as the granularity might be coarse
 *     - Changed CPU constant to be measured in nanoseconds instead of
 *       milliseconds
 *******************************************************************************/
package robocode.manager;


import robocode.dialog.WindowUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class CpuManager {
	private final static int APPROXIMATE_CYCLES_ALLOWED = 1000;
	private final static long TEST_PERIOD_NANOSECS = 1000000000L; // 1 second

	private long cpuConstant = -1;
	private RobocodeManager manager;

	public CpuManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public long getCpuConstant() {
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

		Logger.log("Each robot will be allowed a maximum of " + cpuConstant + " nanoseconds per turn on this system.");
		manager.getProperties().setCpuConstant(cpuConstant);
		manager.saveProperties();

		WindowUtil.setStatus("");
	}
	
	private void setCpuConstant() {
		long start = System.nanoTime();
		long count = 0;
		double d = 0;

		while (System.nanoTime() - start < TEST_PERIOD_NANOSECS && d >= 0) {
			d = Math.random() * Math.random();
			count++;
		}

		cpuConstant = Math.max(1, APPROXIMATE_CYCLES_ALLOWED * TEST_PERIOD_NANOSECS / count);
	}
}