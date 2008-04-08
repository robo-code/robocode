/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;

import pimods.MVCManager;
import pimods.MainFrame;

/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class MVCManager4Robocode extends MVCManager implements RobocodeOptionable {

	public MVCManager4Robocode( MainFrame frame ) {
		super(frame);
	}

	@Override
	public boolean isBulletWakeEnable() {
		return ((RobocodeOptionable)animator).isBulletWakeEnable();
	}

	@Override
	public boolean isExplosionEnable() {
		return ((RobocodeOptionable)animator).isExplosionEnable();
	}

	@Override
	public boolean isTankTrackEnable() {
		return ((RobocodeOptionable)animator).isTankTrackEnable();
	}

	@Override
	public void setBulletWakeEnable(boolean bw) {
		((RobocodeOptionable)animator).setBulletWakeEnable( bw );

	}

	@Override
	public void setExplosionEnable(boolean exp) {
		((RobocodeOptionable)animator).setExplosionEnable( exp );
	}

	@Override
	public void setTankTrackEnable(boolean tt) {
		((RobocodeOptionable)animator).setTankTrackEnable( tt );
	}
}
