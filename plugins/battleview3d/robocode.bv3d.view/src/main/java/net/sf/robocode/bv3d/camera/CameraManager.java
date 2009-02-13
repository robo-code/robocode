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
import net.sf.robocode.bv3d.math.Vertex3f;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class CameraManager {
	public static final int CAMERAANCHOR_CONTROLLER = 1;
	public static final int CAMERAANCHOR_FOLLOWER = 2;
	public static final int CAMERAANCHOR_TRACK = 3;
	private Camera camera;
	private CameraAnchor cameraAnchor;

	public CameraManager() {
		this.camera = new Camera();
		this.setAnchor(CAMERAANCHOR_CONTROLLER);
	}

	public void refresh(GLU glu) {
		if (this.cameraAnchor != null) {
			cameraAnchor.refresh(glu);
		}
	}

	public void setAnchor(int type) {
		switch (type) {
		case CAMERAANCHOR_CONTROLLER:
			this.cameraAnchor = new CameraAnchorController(this.camera);
			break;

		case CAMERAANCHOR_FOLLOWER:
			this.cameraAnchor = new CameraAnchorFollower(this.camera);
			break;

		case CAMERAANCHOR_TRACK:
			this.cameraAnchor = new CameraAnchorTrack(this.camera);
			break;
		}
	}

	public void tryToSetDirection(float x, float y) {
		if (this.cameraAnchor.toString().equals("CameraAnchorController")) {
			((CameraAnchorController) this.cameraAnchor).setDirection(x, y);
		}
	}

	public void tryToSetMove(float x, float y, float z) {
		if (this.cameraAnchor.toString().equals("CameraAnchorController")) {
			((CameraAnchorController) this.cameraAnchor).setMove(x, y, z);
		}
	}

	public void tryToSetCameraDimension(float w, float h) {
		if (this.cameraAnchor.toString().equals("CameraAnchorController")) {
			((CameraAnchorController) this.cameraAnchor).setDimension(w, h);
		}
	}

	public void tryToSetFollowedNode(TransformationNode fn) {
		if (this.cameraAnchor.toString().equals("CameraAnchorFollower")) {
			((CameraAnchorFollower) this.cameraAnchor).setFollowedNode(fn);
		}
	}

	public void tryToSetCameraTrack(CameraTrack ct) {
		if (this.cameraAnchor.toString().equals("CameraAnchorTrack")) {
			((CameraAnchorTrack) this.cameraAnchor).setCameraTrack(ct);
		}
	}

	public Vertex3f getCameraPosition() {
		return(this.camera.getEye());
	}

	public Vertex3f getCameraView() {
		return(this.camera.getView());
	}
	
	public Vertex3f getCameraUp() {
		return(this.camera.getUp());
	}

}
