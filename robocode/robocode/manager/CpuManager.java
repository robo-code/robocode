/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;

import robocode.util.*;

public class CpuManager {
	private int cpuConstant = -1;
	private RobocodeManager manager = null;
	
	public CpuManager(RobocodeManager manager)
	{
		this.manager = manager;
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	public int getCpuConstant() {
	
		if (cpuConstant == -1)
		{
			cpuConstant = manager.getProperties().getCpuConstant();
			if (cpuConstant == -1)
			{
				Utils.setStatus("Estimating CPU speed, please wait...");
				
				//TODO:  Run a benchmark to set this.
				cpuConstant = 10;

				log("Each robot will be allowed a maximum of " + cpuConstant + " milliseconds per turn on this system.");
				manager.getProperties().setCpuConstant(cpuConstant);
				manager.saveProperties();
			}
			/*
			17 MFlops:  32 ms 
			35 MFlops:  16 ms
			70 MFlops:  8 ms
			*/
		}
		return cpuConstant;
	}
	

}

