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
import com.sun.opengl.util.texture.Texture;

import pimods.model.LoadModel;
import pimods.model.Model;
import pimods.scenegraph.ModelView;
import pimods.scenegraph.TransformationNode;
import pimods.scenegraph.TextureIndexLink;
import pimods.scenegraph.DisplayListIndexLink;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Bullet extends TransformationNode {
	public static final Model model = LoadModel.getModelFromFile( "bullet.pobj" );
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();
	
	private static final int PIXEL4BULLET = 5;
	private static final float MAX_POWER = 4; //TODO da verificare!

	private float power=1;
	

	public Bullet( String name ) {
		this.addDrawable(new ModelView( model, "Bullet", displayList, texture ));
		
		this.name = name;
		
		// Scaling
		float mx = model.getDimension().x;
		float scaling =  PIXEL4BULLET / mx;
		this.setScale(scaling, scaling, scaling);
	}
	
	public void setPower( float p){
		this.power = p;
	}
	
	@Override
	public void draw(GL gl){
		float emissionColor[] = { 1.0f, 0.1f, 0.1f };
		float noEmissionColor[]= { 0f, 0f, 0f };

		this.setTy(16);
		gl.glColor3f(power/MAX_POWER, 0.001f, 0.001f);
		
		gl.glMaterialfv( GL.GL_FRONT, GL.GL_EMISSION, emissionColor, 0 );
		super.draw(gl);
		gl.glMaterialfv( GL.GL_FRONT, GL.GL_EMISSION, noEmissionColor, 0 );
	}


	public static void setTexture( Texture[][] t ) {
		texture.setTextureIndexLink( t );
	}

	public static void setDisplayList( int[] grpIndex ) {
		displayList.setDisplayListIndex( grpIndex );
	}
}
