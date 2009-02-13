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


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelMaterial {
	private String name;
	private ModelTexture texture[];

	public ModelMaterial() {}

	public ModelMaterial(String name) {
		this.name = name;
	}

	public ModelMaterial(String name, ArrayList t) {
		this.name = name;
		this.setTextures(t);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTextures(ArrayList t) {
		this.texture = new ModelTexture[t.size()];
		for (int i = 0; i < this.texture.length; i++) {
			this.texture[i] = (ModelTexture) t.get(i);
		}
	}

	public String getName() {
		return(this.name);
	}

	public int getNumberOfTextures() {
		return(this.texture.length);
	}

	public ModelTexture getTexture(int i) {
		return(this.texture[i]);
	}
}
