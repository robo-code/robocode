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
 *******************************************************************************/
package roborumble.battlesengine;


import robocode.control.*;
import roborumble.battlesengine.Coordinator;


/**
 * TeamsListener by Albert Perez
 */
public class AtHomeListener implements RobocodeListener {
	private Coordinator coord;

	public AtHomeListener(Coordinator c) {
		coord = c;
	}

	public void battleComplete(BattleSpecification battleSpec, RobotResults[] results) {
		if (coord != null) {
			coord.put();
		}
	}
    
	public void battleAborted(BattleSpecification battleSpec) {}
    
	public void battleMessage(String string) {}
}
