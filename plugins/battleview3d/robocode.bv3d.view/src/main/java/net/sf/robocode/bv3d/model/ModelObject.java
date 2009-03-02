/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.model;


import java.util.ArrayList;
import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelObject {
	private String name;
	private Vertex3f center;
	private int materialIndex;
	private ModelFace face[];
	
	public ModelObject() {}
	
	public ModelObject(String n) {
		this.name = n;
	}
	
	public ModelObject(String n, ArrayList f) {
		this.name = n;
		this.setFaces(f);
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setCenter(Vertex3f v) {
		this.center = v;
	}

	public void setMaterialIndex(int n) {
		this.materialIndex = n;
	}
	
	public void setFaces(ArrayList f) {
		this.face = new ModelFace[f.size()];
		for (int i = 0; i < this.face.length; i++) {
			this.face[i] = (ModelFace) f.get(i);
		}
	}
	
	public String getName() {
		return(this.name);
	}

	public Vertex3f getCenter() {
		return(this.center);
	}

	public float getCenterX() {
		return(this.center.x);
	}

	public float getCenterY() {
		return(this.center.y);
	}

	public float getCenterZ() {
		return(this.center.z);
	}

	public int getMaterialIndex() {
		return(this.materialIndex);
	}
	
	public int getNumberOfFaces() {
		return(this.face.length);
	}
	
	public ModelFace getFace(int i) {
		return(this.face[i]);
	}
	
}

