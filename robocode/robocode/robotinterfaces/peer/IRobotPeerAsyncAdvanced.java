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
public interface IRobotPeerAsyncAdvanced extends IRobotPeerAsync{
	void setResume();
	void setStop(boolean overwrite);
	void setMove(double distance);
	void setTurnChassis(double radians);
	void setTurnGun(double radians);
	void setTurnRadar(double radians);
}
