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

public class LittleExplosion extends Explosion {

	public LittleExplosion(float x, float z) {
		super(80, 5, 5, 0.8f, 20, 10, 3f, new Vertex3f(0, 1, 0));
		this.setTranslate(x, 16, z);
	}

}
