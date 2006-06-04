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
package robocode.peer;


import robocode.battle.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class ExplosionPeer extends BulletPeer {
	private int whichExplosion = 1;
	private java.awt.Dimension dimension;

	public ExplosionPeer(RobotPeer owner, Battle battle) {
		super(owner, battle);
		frame = 0;
		hitVictim = true;
		setVictim(owner);
		hitVictimTime = 0;
		setPower(1);
		active = false;
		this.dimension = battle.getManager().getImageManager().getExplodeDimension(whichExplosion);
	}

	public final void update() {
		setX(getOwner().getX());
		setY(getOwner().getY());
		frame++;
		if (frame >= getBattle().getManager().getImageManager().getExplosionFrames(whichExplosion)) {
			battle.removeBullet(this);
		}
	}

	public int getWhichExplosion() {
		return whichExplosion;
	}
	
	public int getWidth() {
		return dimension.width;
	}
	
	public int getHeight() {
		return dimension.height;
	}
}
