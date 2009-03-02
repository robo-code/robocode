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

// TODO non sono usate

public class CameraAnchorTrack extends CameraAnchor {
	private CameraTrack cameraTrack;
	private float time;

	public CameraAnchorTrack(Camera camera) {
		super(camera);
		this.cameraTrack = null;
		this.time = 0;
	}

	@Override
	public void refresh(GLU glu) {
		if (this.time < this.cameraTrack.getEndPeriod() && this.cameraTrack != null) {
			this.camera.setEye(this.cameraTrack.getPosition(this.time));
			this.camera.setView(this.cameraTrack.getView(this.time));
			this.camera.setUp(this.cameraTrack.getUp(this.time, this.camera.getUp()));
			this.time++;
		}
		this.camera.refresh(glu);
	}

	public void setCameraTrack(CameraTrack ct) {
		this.cameraTrack = ct;
	}

	@Override
	public String toString() {
		return("CameraAnchorTrack");
	}
}
