/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import java.awt.Color;

import net.sf.robocode.bv3d.model.LoadModel;
import net.sf.robocode.bv3d.model.Model;
import net.sf.robocode.bv3d.scenegraph.ModelView;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;
import net.sf.robocode.bv3d.scenegraph.TextureIndexLink;
import net.sf.robocode.bv3d.scenegraph.DisplayListIndexLink;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Tank extends TransformationNode {
	public static final Model model = LoadModel.getModelFromFile("yabusame.pobj");
	;
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();
	
	public TransformationNode head, radar;
	private ModelView mvBody, mvHead, mvRadar;
	
	public static final int PIXEL4TANK = 32;
	
	// private String name;
	private Text3D text;
	
	public Tank(String name) {
		this.name = name;
		
		mvBody = new ModelView(model, "Body", displayList, texture);
		mvHead = new ModelView(model, "Head", displayList, texture);
		mvRadar = new ModelView(model, "Radar", displayList, texture);
		
		head = new TankHead();
		radar = new TransformationNode();
		
		this.addDrawable(mvBody);
		head.addDrawable(mvHead);
		radar.addDrawable(mvRadar);
		
		head.addDrawable(radar);
		this.addDrawable(head);
		
		text = new Text3D(name);
		text.setColor(Color.BLUE);
		this.addDrawable(text);
		
		this.setTranslate(0, 0.3f, 0);
		
		// Scaling
		float mx = model.getDimension().x;
		float scaling = PIXEL4TANK / mx;

		this.setScale(scaling, scaling, scaling);
		text.setScale(10, 10, 10);
		text.setTy(20);
		
		this.setColors(Color.DARK_GRAY, new Color(102, 102, 1), Color.DARK_GRAY);
	}
	
	public Text3D getText3D() {
		return this.text;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setEnergy(float energy) {
		if (name.indexOf('.') > 0) {
			this.text.setText(name.substring(name.indexOf(".")) + "\t" + Integer.toString(((int) energy)));
		} else {
			this.text.setText(name + "\t" + Integer.toString(((int) energy)));
		}
	}
	
	public void setText(String t) {
		this.text.setText(t);
	}
	
	public void setColors(Color bodyColor, Color headColor, Color radarColor) {
		if (bodyColor != null) {
			mvBody.setColor(bodyColor);
		}
		if (headColor != null) {
			mvHead.setColor(headColor);
		}
		if (radarColor != null) {
			mvRadar.setColor(radarColor);
		}
	}
	
	public TransformationNode getTNHead() {
		return head;
	}

	public static void setTexture(int[][] t) {
		texture.setTextureIndexLink(t);
	}
	
	public static void setDisplayList(int[] grpIndex) {
		displayList.setDisplayListIndex(grpIndex);
	}
	
}
