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

public class Camera {
	private Vertex3f eye;
	private Vertex3f view;
	private Vertex3f up;

	public Camera(Vertex3f eye, Vertex3f view, Vertex3f up) {
		this.eye = eye;
		this.view = view;
		this.up = up;
	}

	public Camera() {
		eye = new Vertex3f(0, 0, 0);
		view = new Vertex3f(0, 0, 1);
		up = new Vertex3f(0, 1, 0);
	}

	public void setEye(Vertex3f eye) {
		if (eye != null) {
			this.eye = eye;
		}
	}

	public void setView(Vertex3f view) {
		if (view != null) {
			this.view = view;
		}
	}

	public void setUp(Vertex3f up) {
		if (up != null) {
			this.up = up;
		}
	}

	public Vertex3f getEye() {
		return((Vertex3f) eye.clone());
	}

	public Vertex3f getView() {
		return((Vertex3f) view.clone());
	}

	public Vertex3f getUp() {
		return((Vertex3f) up.clone());
	}

	public void move(float x, float y, float z) {
		// Yes, view*up is correct for moving along the x direction
		Vertex3f perpendicolar = view.getVectorProduct(up);

		this.eye.x += perpendicolar.x * x + this.up.x * y + this.view.x * z;
		this.eye.y += perpendicolar.y * x + this.up.y * y + this.view.y * z;
		this.eye.z += perpendicolar.z * x + this.up.z * y + this.view.z * z;
	}

	public void rotateX(float angle) {
		float pitchAngle = this.getPitch(this.view) + angle;
		float yawAngle = this.getYaw(this.view);
		
		pitchAngle = pitchAngle > 90 ? 90 : pitchAngle;
		pitchAngle = pitchAngle < -90 ? -90 : pitchAngle;
		Vertex3f newSys[] = { new Vertex3f(0, 0, 1), new Vertex3f(0, 1, 0) };

		newSys = rotateSystemX(pitchAngle, newSys[0], newSys[1]);
		newSys = rotateSystemY(yawAngle, newSys[0], newSys[1]);

		this.view = newSys[0];
		this.up = newSys[1];
	}

	public void rotateY(float angle) {
		float pitchAngle = this.getPitch(this.view);
		float yawAngle = (this.getYaw(this.view) + angle) % 360;
		
		Vertex3f newSys[] = { new Vertex3f(0, 0, 1), new Vertex3f(0, 1, 0) };

		newSys = rotateSystemX(pitchAngle, newSys[0], newSys[1]);
		newSys = rotateSystemY(yawAngle, newSys[0], newSys[1]);

		this.view = newSys[0];
		this.up = newSys[1];
	}

	public void rotateZ(float angle) {/* not used */}

	public void refresh(GLU glu) {
		glu.gluLookAt(eye.x, eye.y, eye.z, eye.x + view.x, eye.y + view.y, eye.z + view.z, up.x, up.y, up.z);
	}

	private Vertex3f[] rotateSystemX(float angle, Vertex3f v, Vertex3f u) {
		Vertex3f newV = new Vertex3f();
		Vertex3f newU = new Vertex3f();
		double rad = (double) angle * Math.PI / 180.0;
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		float y = (float) (v.y * cos - v.z * sin);
		float z = (float) (v.y * sin + v.z * cos);

		newV.x = v.x;
		newV.y = y;
		newV.z = z;

		y = (float) (u.y * cos - u.z * sin);
		z = (float) (u.y * sin + u.z * cos);
		newU.x = u.x;
		newU.y = y;
		newU.z = z;

		return(new Vertex3f[] { newV, newU });
	}

	private Vertex3f[] rotateSystemY(float angle, Vertex3f v, Vertex3f u) {
		Vertex3f newV = new Vertex3f();
		Vertex3f newU = new Vertex3f();
		double rad = (double) angle * Math.PI / 180.0;
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		float x = (float) (v.x * cos + v.z * sin);
		float z = (float) (-v.x * sin + v.z * cos);

		newV.x = x;
		newV.y = v.y;
		newV.z = z;

		x = (float) (u.x * cos + u.z * sin);
		z = (float) (u.x * sin + u.z * cos);
		newU.x = x;
		newU.y = u.y;
		newU.z = z;

		return(new Vertex3f[] { newV, newU });
	}

	private Vertex3f[] rotateSystemZ(float angle, Vertex3f v, Vertex3f u) {
		Vertex3f newV = new Vertex3f();
		Vertex3f newU = new Vertex3f();
		double rad = (double) angle * Math.PI / 180.0;
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		float x = (float) (v.x * cos - v.y * sin);
		float y = (float) (v.x * sin + v.y * cos);

		newV.x = x;
		newV.y = y;
		newV.z = v.z;

		x = (float) (u.x * cos - u.y * sin);
		y = (float) (u.x * sin + u.y * cos);
		newU.x = x;
		newU.y = y;
		newU.z = u.z;

		return(new Vertex3f[] { newV, newU });
	}

	private float getYaw(Vertex3f v) {
		return((float) ((Math.atan2(-v.x, -v.z)) * 180.0f / Math.PI));
	}

	private float getPitch(Vertex3f v) {
		return((float) (Math.asin(v.y / v.getLength()) * 180.0f / Math.PI));
	}

	private float getRoll(Vertex3f v) {

		/* not used */
		return(0);
	}

}
