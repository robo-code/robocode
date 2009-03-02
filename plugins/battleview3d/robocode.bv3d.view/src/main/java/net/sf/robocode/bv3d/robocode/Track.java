/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import net.sf.robocode.bv3d.model.LoadModel;
import net.sf.robocode.bv3d.model.Model;
import net.sf.robocode.bv3d.scenegraph.Ageing;
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

public class Track extends TransformationNode implements Ageing {
	public static final Model model = LoadModel.getModelFromFile("track.pobj");
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();
	private static float mx = model.getDimension().x;
	private static final int PIXEL4TRACK = 30;
	
	public static final int LIFETIME = 250;
	
	private int ttl;
	private int creationTime;
	private ModelView mvTrack;

	public Track(int time, float x, float z, float angle) {
		this.mvTrack = new ModelView(model, "Track", displayList, texture);
		this.addDrawable(this.mvTrack);
		this.creationTime = time;
		this.ttl = LIFETIME;
		
		float yax = 0.5f * (LIFETIME + ((float) Math.random())) / LIFETIME;
		
		this.setTranslate(x, 0.1f + yax, z);
		this.setRotate(angle, 0, 1, 0);
		// Scaling		
		float scaling = PIXEL4TRACK / mx;

		this.setScale(scaling, scaling, scaling);
	}
	
	public int getCreationTime() {
		return creationTime;
	}
	
	public boolean heartBeat() {
		boolean alive = true;
		
		this.ttl--;
		if (this.ttl <= 0) {
			alive = false;
		}
		this.setTy(this.getTy() - 0.5f / LIFETIME);
		if (alive) {
			this.mvTrack.setColor(new Color(1, 1, 1, (float) ttl / LIFETIME));
		}
		
		return(alive);
	}
	
	/* @Override
	 public void draw(GL gl){
	 if( ttl > 0 ){
	 gl.glColor4f(1, 1, 1, (float)ttl/LIFETIME);
	 super.draw(gl);
	 gl.glColor4f(1, 1, 1, 1);
	 }
	 }
	 */
	public static void setTexture(int[][] t) {
		texture.setTextureIndexLink(t);
	}
	
	public static void setDisplayList(int[] grpIndex) {
		displayList.setDisplayListIndex(grpIndex);
	}
}
