/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.math;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 * @author Alessandro Martinelli - Universita' di Pavia
 */

public class CurveBeizer3 extends Curve3D {
	private Vertex3f A, B, C;

	public CurveBeizer3(Vertex3f a, Vertex3f b, Vertex3f c) {
		super();
		this.A = a;
		this.B = b;
		this.C = c;
	}

	public float getDxDt(float t) {
		float tm = 1 - t;

		return(A.x * (-2 * tm) + B.x * (2 - 4 * t) + C.x * (2 * t));
	}

	public float getDyDt(float t) {
		float tm = 1 - t;

		return(A.y * (-2 * tm) + B.y * (2 - 4 * t) + C.y * (2 * t));
	}

	public float getDzDt(float t) {
		float tm = 1 - t;

		return(A.z * (-2 * tm) + B.z * (2 - 4 * t) + C.z * (2 * t));
	}

	public float getTMax() {
		return(1.0f);
	}

	public float getTMin() {
		return(0.0f);
	}

	public float getX(float t) {
		float tm = 1 - t;

		return(A.x * tm * tm + B.x * 2 * tm * t + C.x * t * t);
	}

	public float getY(float t) {
		float tm = 1 - t;

		return(A.y * tm * tm + B.y * 2 * tm * t + C.y * t * t);
	}

	public float getZ(float t) {
		float tm = 1 - t;

		return(A.z * tm * tm + B.z * 2 * tm * t + C.z * t * t);
	}
}
