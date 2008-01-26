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
package robocode.robotinterfaces;

/**
 * @author Pavel Savara (original)
 */
public interface IJuniorEvents {
    /**
     * This event method is called from the game when the radar detects another
     * robot. When this event occurs the {@link @JuniorStructure#scannedDistance},
     * {@link @JuniorStructure#scannedAngle}, {@link @JuniorStructure#scannedBearing}, and {@link @JuniorStructure#scannedEnergy}
     * field values are automatically updated.
     *
     * @see @JuniorStructure#scannedDistance
     * @see @JuniorStructure#scannedAngle
     * @see @JuniorStructure#scannedBearing
     * @see @JuniorStructure#scannedEnergy
     */
    void onScannedRobot();

    /**
     * This event methods is called from the game when this robot has been hit
     * by another robot's bullet. When this event occurs the
     * {@link @JuniorStructure#hitByBulletAngle} and {@link @JuniorStructure#hitByBulletBearing} fields values
     * are automatically updated.
     *
     * @see @JuniorStructure#hitByBulletAngle
     * @see @JuniorStructure#hitByBulletBearing
     */
    void onHitByBullet();

    /**
     * This event methods is called from the game when a bullet from this robot
     * has hit another robot. When this event occurs the {@link @JuniorStructure#hitRobotAngle}
     * and {@link @JuniorStructure#hitRobotBearing} fields values are automatically updated.
     *
     * @see @JuniorStructure#hitRobotAngle
     * @see @JuniorStructure#hitRobotBearing
     */
    void onHitRobot();

    /**
     * This event methods is called from the game when this robot has hit a wall.
     * When this event occurs the {@link @JuniorStructure#hitWallAngle} and {@link @JuniorStructure#hitWallBearing}
     * fields values are automatically updated.
     *
     * @see @JuniorStructure#hitWallAngle
     * @see @JuniorStructure#hitWallBearing
     */
    void onHitWall();
}
