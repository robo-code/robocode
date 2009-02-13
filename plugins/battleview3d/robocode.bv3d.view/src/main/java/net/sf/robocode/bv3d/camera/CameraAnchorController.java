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


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class CameraAnchorController extends CameraAnchor {
	private float width;
	private float height;
	private float sensitivity;

	public CameraAnchorController(Camera camera) {
		super(camera);
		this.setDefault();
		this.sensitivity = 30;
	}

	@Override
	public void refresh(GLU glu) {
		this.camera.refresh(glu);
	}

	public void setDirection(float x, float y) {
		float relX = -(x - this.width / 2);
		float relY = -(y - this.height / 2);

		// System.out.println( "relX: " + relX + ", relY: " + relY );
		// System.out.println( "x: " + x + ", y: " + y + " - width: " + this.width + ", height: " + this.height );

		camera.rotateX((float) (relY / this.height * this.sensitivity));
		camera.rotateY((float) (relX / this.width * this.sensitivity));

	}

	public void setMove(float x, float y, float z) {
		this.camera.move(x, y, z);
	}

	public void setDimension(float w, float h) {
		this.width = w;
		this.height = h;
	}

	public void setDefault() {
		camera.setEye(new Vertex3f(0, 1f, 1f));
		camera.setView(new Vertex3f(0, -1f, -1f));
		camera.setUp(new Vertex3f(0, 1, 0));
	}

	@Override
	public String toString() {
		return("CameraAnchorController");
	}
}
