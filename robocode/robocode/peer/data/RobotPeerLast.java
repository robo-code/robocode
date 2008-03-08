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
package robocode.peer.data;

/**
 * @author Pavel Savara (original)
 */
public class RobotPeerLast extends RobotPeerCommands {
    private double lastGunHeading;
    private double lastHeading;
    private double lastRadarHeading;
    private double lastX;
    private double lastY;

    public final void updateScan() {
        checkWriteLock();
        if (!getScan()) {
            setScan( (lastHeading != getHeading() || lastGunHeading != getGunHeading() || lastRadarHeading != getRadarHeading()
                    || lastX != getX() || lastY != getY() || getWaitCondition() != null));
        }
    }

    public final double getLastGunHeading() {
        checkReadLock();
        return lastGunHeading;
    }

    public final double getLastRadarHeading() {
        checkReadLock();
        return lastRadarHeading;
    }

    public final void updateLast() {
        checkWriteLock();
        lastGunHeading=getGunHeading();
        lastHeading=getHeading();
        lastRadarHeading=getRadarHeading();
        lastX=getX();
        lastY=getY();
        lastHeading = getHeading();
    }
}
