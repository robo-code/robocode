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

public class Vertex3f {
	public float x, y, z;

	public Vertex3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vertex3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Object clone() {
		Vertex3f clone = new Vertex3f(x, y, z);

		return(clone);
	}
	
	public String toString() {
		return("x: " + x + ", y: " + y + ", z: " + z);
	}
	
	public void set(Vertex3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public void add(Vertex3f v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}

	public void sub(Vertex3f v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	
	public void mul(float s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
	}

	public void normalize() {
		this.mul(1.0f / this.getLength());
	}

	public float getLength() {
		return((float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
	}

	public Vertex3f getVectorProduct(Vertex3f other) {
		float vpx = this.y * other.z - other.y * this.z;
		float vpy = this.z * other.x - other.z * this.x;
		float vpz = this.x * other.y - other.x * this.y;

		return(new Vertex3f(vpx, vpy, vpz));
	}

	public Vertex3f getNormalCandidate() {
		float xa = 1, ya = 0, za = 0;
		float x = this.getX(), y = this.getY(), z = this.getZ();
		float projection = x * xa + y * ya + z * za;

		if (projection > 0.9f) {
			xa = 0;
			ya = 1;
			za = 0;
			projection = x * xa + y * ya + z * za;
		}
		xa -= x * projection;
		ya -= y * projection;
		za -= z * projection;

		return(new Vertex3f(xa, ya, za));
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}

	public float getX() {
		return(x);
	}

	public float getY() {
		return(y);
	}

	public float getZ() {
		return(z);
	}

}
