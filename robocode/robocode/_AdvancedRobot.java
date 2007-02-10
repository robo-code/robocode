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
 *******************************************************************************/
package robocode;


/**
 * This class is used by the system as a placeholder for all deprecated calls.
 * <P>You should create a {@link robocode.AdvancedRobot AdvancedRobot} instead.
 * <P>There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see robocode.AdvancedRobot
 *
 * @author Mathew A. Nelson (original)
 */
public class _AdvancedRobot extends Robot {

	protected _AdvancedRobot() {}

	/**
	 * @deprecated use #setTurnGunLeft
	 */
	@Deprecated
	public void setTurnGunLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunLeft");
		}
	}

	/**
	 * @deprecated use #setTurnGunRight
	 */
	@Deprecated
	public void setTurnGunRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunRight");
		}
	}

	/**
	 * @deprecated use robocode.Robot#turnRadarRight
	 */
	@Deprecated
	public void turnRadarRightDegrees(double degrees) {
		turnRadarRight(degrees);
	}

	/**
	 * @deprecated use #setTurnRight
	 */
	@Deprecated
	public void setTurnRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRight");
		}
	}

	/**
	 * @deprecated use #setTurnRadarLeft
	 */
	@Deprecated
	public void setTurnRadarLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnLeft");
		}
	}

	/**
	 * @deprecated use #setTurnLeft
	 */
	@Deprecated
	public void setTurnLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnLeft");
		}
	}

	/**
	 * @deprecated use getHeading
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @deprecated use robocode.Robot#turnLeft
	 */
	@Deprecated
	public void turnLeftDegrees(double degrees) {
		turnLeft(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnRight
	 */
	@Deprecated
	public void turnRightDegrees(double degrees) {
		turnRight(degrees);
	}

	/**
	 * @deprecated use execute() instead.
	 */
	@Deprecated
	public void endTurn() {
		if (peer != null) {
			peer.tick();
		} else {
			uninitializedException("execute");
		}
	}

	/**
	 * @deprecated use getGunHeading
	 */
	@Deprecated
	public double getGunHeadingDegrees() {
		return getGunHeading();
	}

	/**
	 * @deprecated use getRadarHeading
	 */
	@Deprecated
	public double getRadarHeadingDegrees() {
		return getRadarHeading();
	}

	/**
	 * @deprecated Override onSkippedTurn instead.
	 */
	@Deprecated
	public int getWaitCount() {
		return 0;
	}

	/**
	 * @deprecated use #setTurnRadarRight
	 */
	@Deprecated
	public void setTurnRadarRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRadarRight");
		}
	}

	/**
	 * @deprecated use robocode.Robot#turnGunLeft
	 */
	@Deprecated
	public void turnGunLeftDegrees(double degrees) {
		turnGunLeft(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnGunRight
	 */
	@Deprecated
	public void turnGunRightDegrees(double degrees) {
		turnGunRight(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnRadarLeft
	 */
	@Deprecated
	public void turnRadarLeftDegrees(double degrees) {
		turnRadarLeft(degrees);
	}

	/**
	 * @deprecated This method is no longer functional.
	 * @see #getWaitCount
	 */
	@Deprecated
	public int getMaxWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}
}
