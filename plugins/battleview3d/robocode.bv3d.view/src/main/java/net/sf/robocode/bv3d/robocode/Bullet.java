/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import javax.media.opengl.GL;

import net.sf.robocode.bv3d.model.LoadModel;
import net.sf.robocode.bv3d.model.Model;
import net.sf.robocode.bv3d.scenegraph.ModelView;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;
import net.sf.robocode.bv3d.scenegraph.TextureIndexLink;
import net.sf.robocode.bv3d.scenegraph.DisplayListIndexLink;
import java.awt.Color;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Bullet extends TransformationNode {
	public static final Model model = LoadModel.getModelFromFile("bullet.pobj");
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();
	
	private static final int PIXEL4BULLET = 5;
	private static final float MAX_POWER = 4; // TODO da verificare!

	private float power = 1;
	private ModelView mvBullet;
	
	public Bullet(String name) {
		this.mvBullet = new ModelView(model, "Bullet", displayList, texture);
		this.addDrawable(this.mvBullet);

		this.name = name;

		// Scaling
		float mx = model.getDimension().x;
		float scaling = PIXEL4BULLET / mx;

		this.setScale(scaling, scaling, scaling);
	}
	
	public void setPower(float p) {
		this.power = p;
		this.mvBullet.setColor(new Color(this.power / MAX_POWER, 0.001f, 0.001f));
	}
	
	@Override
	public void draw(GL gl) {
		float emissionColor[] = { 1.0f, 0.1f, 0.1f };
		float noEmissionColor[] = { 0f, 0f, 0f };

		this.setTy(16);
		// gl.glColor3f(power/MAX_POWER, 0.001f, 0.001f);
		
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emissionColor, 0);
		super.draw(gl);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, noEmissionColor, 0);
	}

	public static void setTexture(int[][] t) {
		texture.setTextureIndexLink(t);
	}

	public static void setDisplayList(int[] grpIndex) {
		displayList.setDisplayListIndex(grpIndex);
	}
}
