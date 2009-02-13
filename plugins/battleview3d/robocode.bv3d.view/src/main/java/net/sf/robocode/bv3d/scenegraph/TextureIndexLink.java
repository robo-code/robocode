/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


// import com.sun.opengl.util.texture.Texture;

/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class TextureIndexLink {
	private int[][] textureIndexLink;

	public TextureIndexLink() {}

	public TextureIndexLink(int[][] textureIndexLink) {
		this.textureIndexLink = textureIndexLink;
	}

	public void setTextureIndexLink(int[][] textureIndexLink) {
		this.textureIndexLink = textureIndexLink;
	}

	public int[][] getTextureIndexLink() {
		return(this.textureIndexLink);
	}
}
