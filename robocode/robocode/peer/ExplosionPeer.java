/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer;

import robocode.battle.*;
/**
 * Insert the type's description here.
 * Creation date: (9/10/2001 6:23:18 PM)
 * @author: Administrator
 */
public class ExplosionPeer extends BulletPeer {
	private int whichExplosion = 1;
	private java.awt.Dimension dimension;
/**
 * Explosion constructor comment.
 * @param owner robocode.robot.PrivateRobot
 * @param battle robocode.Battle
 */
public ExplosionPeer(RobotPeer owner, Battle battle) {
	super(owner, battle);
	frame = 0;
	hitVictim = true;
	setVictim(owner);
	hitVictimTime = 0;
	//killedSelf = true;
	setPower(1);
	active=false;
	this.dimension = battle.getManager().getImageManager().getExplodeDimension(whichExplosion);
}
/**
 * Insert the method's description here.
 * Creation date: (12/18/2000 5:07:28 PM)
 */
public final void update()  {

	setX(getOwner().getX());
	setY(getOwner().getY());
	frame++;
	if (frame >= getBattle().getManager().getImageManager().getExplosionFrames(whichExplosion))
		battle.removeBullet(this);

/*		sBullet.setHitVictim(true);
		sBullet.setVictim(this);
		sBullet.setOwner(this);
		sBullet.setHitVictimTime(0);
		sBullet.killedSelf = true;
		sBullet.setPower(1);
		sBullet.active=false;
		*/
/*
		if (active)
	{
		updateMovement();

		checkBulletCollision();
		
	  	checkWallCollision();

	  	checkRobotCollision();
	}
	else if (hitVictim)
	{
		setX(victim.getX() + deltaX);
		setY(victim.getY() + deltaY);
		hitVictimTime++;
		frame = hitVictimTime;
		if (hitVictimTime > 16)
			hitVictim = false;
	}
	else if (hitBullet)
	{
		hitBulletTime++;
		frame = hitBulletTime;
		if (hitBulletTime > 16)
			hitBullet = false;
	}
	else if (dirtyRect == null)
	  battle.removeBullet(this);
*/		
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
