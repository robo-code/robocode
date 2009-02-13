/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


/**
 * The options that can be selected in the RobocodeAnimator
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public interface RobocodeOptionable {

	/**
	 * @param tt <code>true</code> if you want enable tracks, <code>false</code> if you want stop to see tracks and delete existing tracks
	 * @see Track
	 */
	public void setTankTrackEnable(boolean tt);
	
	/**
	 * @return <code>true</code> if tracks are enabled
	 * @see Track
	 */
	public boolean isTankTrackEnable();
	
	/**
	 * @param exp <code>true</code> if you want enable explosions, <code>false</code> if you want stop to see explosions and delete existing explosions
	 * @see BigExplosion 
	 * @see LittleExplosion
	 */
	public void setExplosionEnable(boolean exp);
	
	/**
	 * @return <code>true</code> if explosions are enabled
	 * @see BigExplosion
	 * @see LittleExplosion
	 */
	public boolean isExplosionEnable();
	
	/**
	 * @param bw <code>true</code> if you want enable wakes, <code>false</code> if you want stop to see wakes and delete existing wakes
	 * @see BulletWake 
	 */
	public void setBulletWakeEnable(boolean bw);
	
	/**
	 * @return <code>true</code> if bullet wakes are enabled
	 * @see BulletWake
	 */
	public boolean isBulletWakeEnable();
}
