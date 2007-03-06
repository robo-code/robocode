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
 *     Flemming N. Larsen
 *     - Added a peer.getCall() to getWaitCount()
 *     - Fixed wrong names used in uninitializedException() calls
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * This class is used by the system, as well as being a placeholder for all deprecated
 * (meaning, you should not use them) calls for AdvancedRobot.
 * <P>
 * You should create a {@link AdvancedRobot} instead.
 * <P>
 * There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see AdvancedRobot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class _AdvancedRobot extends Robot {

	protected _AdvancedRobot() {}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnGunLeft(double)
	 *    setTurnGunLeft} instead.
	 */
	@Deprecated
	public void setTurnGunLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunLeftDegrees");
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnGunRight(double)
	 *    setTurnGunRight} instead.
	 */
	@Deprecated
	public void setTurnGunRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnGunRightDegrees");
		}
	}

	/**
	 * @deprecated Use {@link Robot#turnRadarRight(double) turnRadarRight}
	 *    instead.
	 */
	@Deprecated
	public void turnRadarRightDegrees(double degrees) {
		turnRadarRight(degrees);
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRight(double) setTurnRight}
	 *    instead.
	 */
	@Deprecated
	public void setTurnRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRightDegrees");
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarLeft(double)
	 *    setTurnRadarLeft} instead.
	 */
	@Deprecated
	public void setTurnRadarLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRadarLeftDegrees");
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnLeft(double) setTurnLeft}
	 *    instead.
	 */
	@Deprecated
	public void setTurnLeftDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnLeftDegrees");
		}
	}

	/**
	 * @deprecated Use {@link Robot#getHeading() getHeading} instead.
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @deprecated Use {@link Robot#turnLeft(double) turnLeft} instead.
	 */
	@Deprecated
	public void turnLeftDegrees(double degrees) {
		turnLeft(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnRight(double) turnRight} instead.
	 */
	@Deprecated
	public void turnRightDegrees(double degrees) {
		turnRight(degrees);
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#execute() execute} instead.
	 */
	@Deprecated
	public void endTurn() {
		if (peer != null) {
			peer.tick();
		} else {
			uninitializedException("endTurn");
		}
	}

	/**
	 * @deprecated Use {@link Robot#getGunHeading() getGunHeading} instead.
	 */
	@Deprecated
	public double getGunHeadingDegrees() {
		return getGunHeading();
	}

	/**
	 * @deprecated Use {@link Robot#getRadarHeading() getRadarHeading} instead.
	 */
	@Deprecated
	public double getRadarHeadingDegrees() {
		return getRadarHeading();
	}

	/**
	 * @deprecated This method is no longer functional.
	 * Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
	 */
	@Deprecated
	public int getWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarRight(double)
	 *    setTurnRadarRight} instead.
	 */
	@Deprecated
	public void setTurnRadarRightDegrees(double degrees) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException("setTurnRadarRightDegrees");
		}
	}

	/**
	 * @deprecated Use {@link Robot#turnGunLeft(double) turnGunLeft} instead.
	 */
	@Deprecated
	public void turnGunLeftDegrees(double degrees) {
		turnGunLeft(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnGunRight(double) turnGunRight} instead.
	 */
	@Deprecated
	public void turnGunRightDegrees(double degrees) {
		turnGunRight(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnRadarLeft(double) turnRadarLeft} instead.
	 */
	@Deprecated
	public void turnRadarLeftDegrees(double degrees) {
		turnRadarLeft(degrees);
	}

	/**
	 * @deprecated This method is no longer functional.
	 * Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
	 */
	@Deprecated
	public int getMaxWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}
}
