/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
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
 * @author Mathew A. Nelson
 */
public class _AdvancedRobot extends Robot {
	
	protected _AdvancedRobot() {}

	/**
	 * @deprecated use #setTurnGunLeft
	 */
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
	public void turnRadarRightDegrees(double degrees) {
		turnRadarRight(degrees);
	}

	/**
	 * @deprecated use #setTurnRight
	 */
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
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @deprecated use robocode.Robot#turnLeft
	 */
	public void turnLeftDegrees(double degrees) {
		turnLeft(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnRight
	 */
	public void turnRightDegrees(double degrees) {
		turnRight(degrees);
	}

	/**
	 * @deprecated use execute() instead.
	 */
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
	public double getGunHeadingDegrees() {
		return getGunHeading();
	}

	/**
	 * @deprecated use getRadarHeading
	 */
	public double getRadarHeadingDegrees() {
		return getRadarHeading();
	}

	/**
	 * @deprecated Override onSkippedTurn instead.
	 */
	public int getWaitCount() {
		return 0;
	}

	/**
	 * @deprecated use #setTurnRadarRight
	 */
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
	public void turnGunLeftDegrees(double degrees) {
		turnGunLeft(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnGunRight
	 */
	public void turnGunRightDegrees(double degrees) {
		turnGunRight(degrees);
	}

	/**
	 * @deprecated use robocode.Robot#turnRadarLeft
	 */
	public void turnRadarLeftDegrees(double degrees) {
		turnRadarLeft(degrees);
	}

	/**
	 * @deprecated This method is no longer functional.
	 * @see #getWaitCount
	 */
	public int getMaxWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}
}
