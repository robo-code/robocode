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
package robocode.peer;

/**
 * @author Pavel Savara (original)
 */
public class R52obotPeerLast extends R51obotPeerCommands {
    private double lastGunHeading;
    private double lastHeading;
    private double lastRadarHeading;
    private double lastX;
    private double lastY;

    protected void updateScan() {
        if (!getScan()) {
            setScan( (lastHeading != getHeading() || lastGunHeading != getGunHeading() || lastRadarHeading != getRadarHeading()
                    || lastX != getX() || lastY != getY() || getWaitCondition() != null));
        }
    }

    public double getLastGunHeading() {
        return lastGunHeading;
    }

    public double getLastRadarHeading() {
        return lastRadarHeading;
    }

    protected void updateLast() {
        lastGunHeading=getGunHeading();
        lastHeading=getHeading();
        lastRadarHeading=getRadarHeading();
        lastX=getX();
        lastY=getY();
        lastHeading = getHeading();
    }

}
