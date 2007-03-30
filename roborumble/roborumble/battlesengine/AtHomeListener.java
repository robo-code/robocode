/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed unused imports
 *     - Added getResults() that returns the results when a battle is complete
 *     - Coordinator is now created inside this listener
 *******************************************************************************/
package roborumble.battlesengine;


import robocode.control.*;
import roborumble.battlesengine.Coordinator;


/**
 * Listener used for receiving battle results.
 * 
 * @author Albert Perez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class AtHomeListener implements RobocodeListener {
	private Coordinator coord = new Coordinator();

	private RobotResults[] results;

	public void battleComplete(BattleSpecification battleSpec, RobotResults[] results) {
		this.results = results;

		// Notify that results are ready
		coord.put();
	}

	public void battleAborted(BattleSpecification battleSpec) {}

	public void battleMessage(String string) {}

	public RobotResults[] getResults() {
		// Wait till the result become available
		coord.get();

		return results;
	}
}
