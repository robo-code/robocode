/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;


/**
 * This class is used by the system as a placeholder for all *Radians calls in
 * {@link AdvancedRobot}. You may refer to this class for documentation only.
 * <p>
 * You should create a {@link AdvancedRobot} instead.
 * <p>
 * (The Radians methods themselves will continue work, however).
 *
 * @see Robot
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class _AdvancedRadiansRobot extends _AdvancedRobot {

	_AdvancedRadiansRobot() {}

	public double getHeadingRadians() {
		if (peer != null) {
			return peer.getBodyHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	public void setTurnLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(-radians);
		} else {
			uninitializedException();
		}
	}

	public void setTurnRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(radians);
		} else {
			uninitializedException();
		}
	}

	public void turnLeftRadians(double radians) {
		if (peer != null) {
			peer.turnBody(-radians);
		} else {
			uninitializedException();
		}
	}

	public void turnRightRadians(double radians) {
		if (peer != null) {
			peer.turnBody(radians);
		} else {
			uninitializedException();
		}
	}

	public double getGunHeadingRadians() {
		if (peer != null) {
			return peer.getGunHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	public double getRadarHeadingRadians() {
		if (peer != null) {
			return peer.getRadarHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	public void setTurnGunLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(-radians);
		} else {
			uninitializedException();
		}
	}

	public void setTurnGunRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(radians);
		} else {
			uninitializedException();
		}
	}

	public void setTurnRadarLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(-radians);
		} else {
			uninitializedException();
		}
	}

	public void setTurnRadarRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(radians);
		} else {
			uninitializedException();
		}
	}

	public void turnGunLeftRadians(double radians) {
		if (peer != null) {
			peer.turnGun(-radians);
		} else {
			uninitializedException();
		}
	}

	public void turnGunRightRadians(double radians) {
		if (peer != null) {
			peer.turnGun(radians);
		} else {
			uninitializedException();
		}
	}

	public void turnRadarLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).turnRadar(-radians);
		} else {
			uninitializedException();
		}
	}

	public void turnRadarRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).turnRadar(radians);
		} else {
			uninitializedException();
		}
	}

	public double getGunTurnRemainingRadians() {
		if (peer != null) {
			return peer.getGunTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}

	public double getRadarTurnRemainingRadians() {
		if (peer != null) {
			return peer.getRadarTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}

	public double getTurnRemainingRadians() {
		if (peer != null) {
			return peer.getBodyTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}
}
