/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class BulletWake extends Explosion {

	public BulletWake(float oldX, float oldZ, float newX, float newZ) {
		
		super(30, 1, 4, 0.8f, 10, 5, 0.6f, new Vertex3f(newX - oldX, 0, newZ - oldZ));
		this.setTranslate(newX, 16, newZ);
	}
	
}
