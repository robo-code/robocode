/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.host;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.Logger;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class CpuManager implements ICpuManager { // NO_UCD (use default)
	private final static int APPROXIMATE_CYCLES_ALLOWED = 6250;
	private final static int TEST_PERIOD_MILLIS = 5000;

	private long cpuConstant = -1;
	private final ISettingsManager properties;

	public CpuManager(ISettingsManager properties) { // NO_UCD (unused code)
		this.properties = properties;
	}

	public long getCpuConstant() {
		if (cpuConstant == -1) {
			cpuConstant = properties.getCpuConstant();
			if (cpuConstant == -1) {
				calculateCpuConstant();
			}
		}
		return cpuConstant;
	}

	public void calculateCpuConstant() {
		setStatus("Estimating CPU speed, please wait...");

		setCpuConstant();
		Logger.logMessage(
				"Each robot will be allowed a maximum of " + cpuConstant + " nanoseconds per turn on this system.");

		properties.setCpuConstant(cpuConstant);
		properties.saveProperties();

		setStatus("");
	}

	private void setCpuConstant() {
		long count = 0;
		double d = 0;

		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < TEST_PERIOD_MILLIS) {
			d += Math.hypot(Math.sqrt(Math.abs(Math.log(Math.atan(Math.random())))),
					Math.cbrt(Math.abs(Math.random() * 10)))
					/ Math.exp(Math.random());
			count++;
		}

		// to cheat optimizer, almost never happen
		if (d == 0.0) {
			Logger.logMessage("bingo!");
		}

		cpuConstant = Math.max(1, (long) (1000000.0 * APPROXIMATE_CYCLES_ALLOWED * TEST_PERIOD_MILLIS / count));
	}

	private void setStatus(String message) {
		IWindowManager windowManager = Container.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}

}
