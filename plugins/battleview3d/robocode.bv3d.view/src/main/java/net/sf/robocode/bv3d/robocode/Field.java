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

// import com.sun.opengl.util.texture.Texture;

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

public class Field extends TransformationNode {
	public static final Model modelBP = LoadModel.getModelFromFile("battleplane.pobj");
	public static final Model modelRW = LoadModel.getModelFromFile("retainingwall.pobj");
	public static final Model modelWT = LoadModel.getModelFromFile("walltower.pobj");
	private static final TextureIndexLink textureBP = new TextureIndexLink();
	private static final TextureIndexLink textureRW = new TextureIndexLink();
	private static final TextureIndexLink textureWT = new TextureIndexLink();
	private static final DisplayListIndexLink displayListBP = new DisplayListIndexLink();
	private static final DisplayListIndexLink displayListRW = new DisplayListIndexLink();
	private static final DisplayListIndexLink displayListWT = new DisplayListIndexLink();
	
	private static final int PIXEL4WALL = 8;
	private static final int PIXEL4TOWER = 10;
	private static final int padding = 20;
	private static final float wallWidth = modelRW.getDimensionX() * PIXEL4WALL;
	private static final float wallHeight = modelRW.getDimensionZ() * PIXEL4WALL;
	private static final float towerWidth = modelWT.getDimensionX() * PIXEL4TOWER;
	private static final float towerHeight = modelWT.getDimensionZ() * PIXEL4TOWER;

	private float width;
	private float height;
	private float scaling;
	
	public Field(float xL, float zL) {
		this.width = xL;
		this.height = zL;
		
		TransformationNode battlePlane = new TransformationNode();

		battlePlane.addDrawable(new ModelView(modelBP, "BattlePlane", displayListBP, textureBP));
		this.addDrawable(battlePlane);
		// this.addDrawable( new Landscape() );
		
		float mx = modelBP.getDimension().x;
		float mz = modelBP.getDimension().z;
		
		// System.out.println("mx="+mx+" - mz="+mz);
		// System.out.println("x scalato="+(mz/mx)*(xL/zL)*mx+" - z scalato="+mz);
		
		this.setUpWalls();
		
		scaling = 1 / xL;
		
		battlePlane.setScale(1 / mx * (xL + 2 * padding), 1, 1 / mz * (zL + 2 * padding));
		battlePlane.setTranslate(-padding, 0, -padding);

		this.setScale(scaling, scaling, scaling); // The field is scaled on x
		this.setTranslate(-0.5f, 0, -0.5f);
		// System.out.println(mx);

	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getScaling() {
		return scaling;
	}

	private void setUpWalls() {
		// lato in fondo 
		for (int i = 0; wallWidth * i < width + 2 * padding; i++) {
			RetainingWall wall = new RetainingWall(wallWidth * i - padding, 0, -padding);

			wall.setRotate(180, 0, 1, 0);
			this.addDrawable(wall);
		}
		// lato in testa
		for (int i = 0; wallWidth * i < width + 2 * padding; i++) {
			RetainingWall wall = new RetainingWall(wallWidth * i - padding, 0, height + padding);

			this.addDrawable(wall);
		}
		// lato sinistro 
		for (int i = 0; wallWidth * i < height + 2 * padding; i++) {
			RetainingWall wall = new RetainingWall(-padding, 0, wallWidth * i - padding);

			wall.setRotate(-90, 0, 1, 0);
			this.addDrawable(wall);
		}
		// lato destro 
		for (int i = 0; wallWidth * i < height + 2 * padding; i++) {
			RetainingWall wall = new RetainingWall(width + padding, 0, wallWidth * i - padding);

			wall.setRotate(90, 0, 1, 0);
			this.addDrawable(wall);
		}

		WallTower tower = new WallTower(-padding, 0, -padding);

		tower.setRotate(90, 0, 1, 0);
		this.addDrawable(tower);

		tower = new WallTower(width + padding, 0, -padding);
		tower.setRotate(90, 0, 1, 0);
		this.addDrawable(tower);

		tower = new WallTower(width + padding, 0, height + padding);
		tower.setRotate(-90, 0, 1, 0);
		this.addDrawable(tower);

		tower = new WallTower(-padding, 0, height + padding);
		tower.setRotate(-90, 0, 1, 0);
		this.addDrawable(tower);
	}

	// @Override
	// public void draw( GL gl){
	// gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_LINE );
	// gl.glColor3f(1f, 1f, 1f);
	// super.draw( gl);
	// gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL.GL_FILL );
	// }

	/*
	 private class Landscape implements Drawable{
	 @Override
	 public void draw(GL gl) {
	 gl.glBegin(GL.GL_TRIANGLE_STRIP);
	 gl.glColor3f( 0.4f, 0.5f, 0.4f );
	 gl.glVertex3f(-10000, -0.1f, -10000);
	 gl.glVertex3f(-10000, -0.1f, 10000);
	 gl.glVertex3f(10000, -0.1f, -10000);
	 gl.glVertex3f(10000, -0.1f, 10000);
	 gl.glEnd();
	 }

	 @Override
	 public void init(GL gl) {}
	 }
	 */

	private class RetainingWall extends TransformationNode {
		public RetainingWall(float xpos, float ypos, float zpos) {
			ModelView rWallMV = new ModelView(modelRW, "RetainingWall", displayListRW, textureRW);

			rWallMV.setColor(new Color(80, 80, 80));
			this.setScale(PIXEL4WALL, PIXEL4WALL, PIXEL4WALL);
			this.addDrawable(rWallMV);
			this.setTranslate(xpos, ypos, zpos);
		}
	}


	public class WallTower extends TransformationNode {
		public WallTower(float xpos, float ypos, float zpos) {
			ModelView wTowerMV = new ModelView(modelWT, "WallTower", displayListWT, textureWT);

			wTowerMV.setColor(new Color(80, 80, 80));
			this.setScale(PIXEL4TOWER, PIXEL4TOWER, PIXEL4TOWER);
			this.addDrawable(wTowerMV);
			this.setTranslate(xpos, ypos, zpos);
		}
	}

	public static void setTextureBP(int[][] t) {
		textureBP.setTextureIndexLink(t);
	}

	public static void setTextureRW(int[][] t) {
		textureRW.setTextureIndexLink(t);
	}

	public static void setTextureWT(int[][] t) {
		textureWT.setTextureIndexLink(t);
	}
	
	public static void setDisplayListBP(int[] grpIndex) {
		displayListBP.setDisplayListIndex(grpIndex);
	}
	
	public static void setDisplayListRW(int[] grpIndex) {
		displayListRW.setDisplayListIndex(grpIndex);
	}
	
	public static void setDisplayListWT(int[] grpIndex) {
		displayListWT.setDisplayListIndex(grpIndex);
	}
}
