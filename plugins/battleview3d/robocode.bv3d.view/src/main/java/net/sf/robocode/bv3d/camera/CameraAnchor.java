/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.camera;


import javax.media.opengl.glu.GLU;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public abstract class CameraAnchor {
	protected Camera camera;

	public CameraAnchor(Camera camera) {
		this.camera = camera;
	}

	public void refresh(GLU glu) {}

	public String toString() {
		return("CameraAnchor");
	}
}
