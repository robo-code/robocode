/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
 *     Robert D. Maupin
 *     - The "heavy math" algorithm for calculation the CPU constant
 *     Pavel Savara
 *     - Cheating the optimizer with the setCpuConstant() so the optimizer does
 *       not throw the rational computation away  
 *******************************************************************************/
package robocode.manager;


import robocode.dialog.WindowUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class CpuManager {
	private final static int APPROXIMATE_CYCLES_ALLOWED = 6250;
	private final static int TEST_PERIOD_MILLIS = 5000;

	private long cpuConstant = -1;
    private long waitConstant = -1;
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
        Logger.logMessage("Each robot will be allowed a maximum of " + cpuConstant + " nanoseconds per turn on this system.");

        //TODO ZAMO setWaitConstant();
        //TODO ZAMO Logger.logError("Java is able to sleep minimum " + waitConstant + " nanoseconds on this system.");

		manager.getProperties().setCpuConstant(cpuConstant);
		manager.saveProperties();

		WindowUtil.setStatus("");
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

    private void setWaitConstant() {
        long count = 0;

        long start = System.currentTimeMillis();
        Object l = new Object();
        try {
            synchronized (l) {
                while (System.currentTimeMillis() - start < TEST_PERIOD_MILLIS) {
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    l.wait(0, 1);
                    count+=10;
                }
            }
        } catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
        }

        waitConstant = Math.max(1, (long) (1000.0 * TEST_PERIOD_MILLIS / count));
    }


}
