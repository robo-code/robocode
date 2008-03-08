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

import java.awt.*;

/**
 * @author Pavel Savara (original)
 */
public class R40obotPeerRecord extends R32obotPeerTeam {

    private Color bodyColor;
    private Color gunColor;
    private Color radarColor;
    private Color bulletColor;
    private Color scanColor;

    public Color getBodyColor() {
        return bodyColor;
    }

    public Color getRadarColor() {
        return radarColor;
    }

    public Color getGunColor() {
        return gunColor;
    }

    public Color getBulletColor() {
        return bulletColor;
    }

    public Color getScanColor() {
        return scanColor;
    }

    public void setBodyColor(Color color) {
        bodyColor = color;
    }

    public void setRadarColor(Color color) {
        radarColor = color;
    }

	public void setGunColor(Color color) {
		gunColor = color;
	}

    public void setBulletColor(Color color) {
        bulletColor = color;
    }

	public void setScanColor(Color color) {
		scanColor = color;
	}

}
