/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.model;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelTexture {
	private String name;
	private String image;

	public ModelTexture() {}

	public ModelTexture(String name) {
		this.name = name;
	}

	public ModelTexture(String name, String image) {
		this.name = name;
		this.setImage(image);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return(this.name);
	}

	public String getImage() {
		return(this.image);
	}
}
