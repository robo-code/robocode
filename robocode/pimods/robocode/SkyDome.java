/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;

import javax.media.opengl.GL;

import pimods.model.LoadModel;
import pimods.model.Model;
import pimods.scenegraph.ModelView;
import pimods.scenegraph.TextureIndexLink;
import pimods.scenegraph.DisplayListIndexLink;
import pimods.scenegraph.TransformationNode;

import com.sun.opengl.util.texture.Texture;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class SkyDome extends TransformationNode {
	public static final Model model = LoadModel.getModelFromFile( "skydome.pobj" );
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();

	public SkyDome() {
		this.addDrawable( new ModelView( model, "SkyDome", displayList, texture ) );
		this.setTranslate( 0, -0.1f, 0 );
		this.setScale( 15, 15, 15 );
	}

	@Override
	public void draw(GL gl){
		//gl.glColor3f( 1, 1, 1 );
		//gl.glMaterialf(GL.GL_FRONT_AND_BACK,GL.GL_SHININESS, 0 );
		gl.glDisable( GL.GL_NORMALIZE );
		super.draw(gl);
		gl.glEnable( GL.GL_NORMALIZE );
		//gl.glMaterialf(GL.GL_FRONT_AND_BACK,GL.GL_SHININESS, 128);
	}


	public static void setTexture( int[][] t ) {
		texture.setTextureIndexLink( t );
	}
	
	public static void setDisplayList( int[] grpIndex ) {
		displayList.setDisplayListIndex( grpIndex );
	}
}
