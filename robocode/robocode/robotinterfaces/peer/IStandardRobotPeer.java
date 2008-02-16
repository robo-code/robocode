/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotinterfaces.peer;


/**
 * @author Pavel Savara (original)
 */
public interface IStandardRobotPeer extends IBasicRobotPeer {

	// blocking actions
	void stop(boolean overwrite);
	void resume();
	void scanReset();
	void turnRadar(double radians);

	// fast setters
	void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn);
	void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn);
	void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn);
}
