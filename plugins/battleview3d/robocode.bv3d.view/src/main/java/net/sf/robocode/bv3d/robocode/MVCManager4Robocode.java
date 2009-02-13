/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import javax.media.opengl.GLCanvas;

import net.sf.robocode.bv3d.MVCManager;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class MVCManager4Robocode extends MVCManager implements RobocodeOptionable {

	public MVCManager4Robocode(GLCanvas glcanvas) {
		super(glcanvas);
	}

	public boolean isBulletWakeEnable() {
		return ((RobocodeOptionable) animator).isBulletWakeEnable();
	}

	public boolean isExplosionEnable() {
		return ((RobocodeOptionable) animator).isExplosionEnable();
	}

	public boolean isTankTrackEnable() {
		return ((RobocodeOptionable) animator).isTankTrackEnable();
	}

	public void setBulletWakeEnable(boolean bw) {
		((RobocodeOptionable) animator).setBulletWakeEnable(bw);

	}

	public void setExplosionEnable(boolean exp) {
		((RobocodeOptionable) animator).setExplosionEnable(exp);
	}

	public void setTankTrackEnable(boolean tt) {
		((RobocodeOptionable) animator).setTankTrackEnable(tt);
	}
}
