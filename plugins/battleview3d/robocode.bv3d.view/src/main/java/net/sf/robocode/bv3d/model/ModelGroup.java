/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.model;


import java.awt.Color;
import java.util.ArrayList;
import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelGroup {
	private String name;
	private Vertex3f center;
	private Color color;
	private ModelObject object[];
	
	public ModelGroup() {}
	
	public ModelGroup(String n) {
		this.name = n;
	}
	
	public ModelGroup(String n, ArrayList o) {
		this.name = n;
		this.object = new ModelObject[o.size()];
		this.setObjects(o);
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setObjects(ArrayList o) {
		this.object = new ModelObject[o.size()];
		for (int i = 0; i < this.object.length; i++) {
			this.object[i] = (ModelObject) o.get(i);
		}

		this.center = new Vertex3f();
		for (int i = 0; i < this.object.length; i++) {
			this.center.add(this.object[i].getCenter());
		}

		this.center.mul(1.0f / this.object.length);
	}
	
	public String getName() {
		return(this.name);
	}
	
	public int getNumberOfObjects() {
		return(this.object.length);
	}
	
	public ModelObject getObject(int i) {
		return(this.object[i]);
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

}

