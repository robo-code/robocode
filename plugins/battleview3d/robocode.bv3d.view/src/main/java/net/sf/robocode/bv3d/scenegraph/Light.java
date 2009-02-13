/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


import javax.media.opengl.GL;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Light implements Drawable {
	private int index;
	private boolean enable;
	private boolean positional;
	private float[] position;
	private float[] ambientIntensity;
	private float[] diffuseIntensity;
	private float[] specularIntensity;
	private int attenuationType;
	private float attenuationAmount;
	private float spotCutOff;

	public Light(int index) {
		this.index = index;
		this.positional = true;
		this.spotCutOff = 0.0f;
	}
	
	public void turnOn() {
		this.enable = true;
	}

	public void turnOff() {
		this.enable = false;
	}
	
	private void turnOn(GL gl) {
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0 + this.index);
	}
	
	private void turnOff(GL gl) {
		gl.glDisable(GL.GL_LIGHT0 + this.index);
	}

	public void setPosition(float x, float y, float z) {
		this.position = new float[] { x, y, z, 1.0f };
	}

	public void setDirection(float x, float y, float z) {
		this.position = new float[] { x, y, z, 0.0f };
	}

	public void setAmbientIntensity(float r, float g, float b) {
		this.ambientIntensity = new float[] { r, g, b, 1.0f };
	}

	public void setDiffuseIntensity(float r, float g, float b) {
		this.diffuseIntensity = new float[] { r, g, b, 1.0f };
	}

	public void setSpecularIntensity(float r, float g, float b) {
		this.specularIntensity = new float[] { r, g, b, 1.0f };
	}

	/*
	 GL_LINEAR_ATTENUATION | GL_QUADRATIC_ATTENUATION | GL_CONSTANT_ATTENUATION
	 */		
	public void setAttenuation(int attenuationType, float attenuationAmount) {
		this.attenuationType = attenuationType;
		this.attenuationAmount = attenuationAmount;
	}

	public void setSpotCutOff(float spotCutOff) {
		this.spotCutOff = spotCutOff;
		if (this.spotCutOff > 90) {
			this.spotCutOff = 90;
		}
		if (this.spotCutOff < 0) {
			this.spotCutOff = 0;
		}
	}

	public void makePositional() {
		this.positional = true;
	}

	public void makeDirectional() {
		this.positional = false;
	}

	public void draw(GL gl) {
		if (this.enable) {
			this.turnOn(gl);
			if (this.position != null) {
				this.position[3] = this.positional ? 1.0f : 0.0f;
				gl.glLightfv(GL.GL_LIGHT0 + this.index, GL.GL_POSITION, this.position, 0);
			}
			if (this.ambientIntensity != null) {
				gl.glLightfv(GL.GL_LIGHT0 + this.index, GL.GL_AMBIENT, this.ambientIntensity, 0);
			}
			if (this.diffuseIntensity != null) {
				gl.glLightfv(GL.GL_LIGHT0 + this.index, GL.GL_DIFFUSE, this.diffuseIntensity, 0);
			}
			if (this.specularIntensity != null) {
				gl.glLightfv(GL.GL_LIGHT0 + this.index, GL.GL_SPECULAR, this.specularIntensity, 0);
			}
			if (this.attenuationType == GL.GL_CONSTANT_ATTENUATION || this.attenuationType == GL.GL_LINEAR_ATTENUATION
					|| this.attenuationType == GL.GL_QUADRATIC_ATTENUATION) {
				gl.glLightf(GL.GL_LIGHT0 + this.index, this.attenuationType, this.attenuationAmount);
			}
			if (this.spotCutOff > 0) {
				gl.glLightf(GL.GL_LIGHT0 + this.index, GL.GL_SPOT_CUTOFF, this.spotCutOff);
			}
		} else {
			this.turnOff(gl);
		}
	}

}
