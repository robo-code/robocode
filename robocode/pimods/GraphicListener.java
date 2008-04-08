/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import pimods.model.LoadModel;
import pimods.model.Model;
import pimods.model.ModelMaterial;
import pimods.math.Vertex3f;

import pimods.Scene;

/**
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class GraphicListener implements GLEventListener {
	private Scene scene;
	private float width, height;
		
	public void display( GLAutoDrawable drawable ) {
		GL gl = drawable.getGL();
		GLU glu=new GLU();

		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl.glEnable( GL.GL_NORMALIZE );
		//gl.glEnable( GL.GL_RESCALE_NORMAL );

		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 50f, 1, 0.1f, 25.0 );

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		gl.glScalef( 600f/this.width, 600f/this.height, 1 );

		if( this.scene!=null )
			this.scene.draw( gl );
	}

	public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) {
	}

	public void init( GLAutoDrawable drawable ) {
		GL gl=drawable.getGL();

		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		gl.glClearDepth( 1.0 );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glDepthFunc( GL.GL_LEQUAL );
		gl.glFrontFace( GL.GL_CW );

		gl.glShadeModel( GL.GL_SMOOTH );

		gl.glEnable( GL.GL_CULL_FACE );
		gl.glCullFace( GL.GL_BACK );

		gl.glEnable(GL.GL_TEXTURE_2D);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable( GL.GL_LIGHTING );
		float ambient[]= { 0.6f, 0.6f, 0.6f, 1 };
		gl.glLightModelfv( GL.GL_LIGHT_MODEL_AMBIENT, ambient, 0 );
	
		gl.glEnable( GL.GL_COLOR_MATERIAL );
		gl.glColorMaterial( GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE );
		float specColor[]= {0.2f,0.2f,0.2f,1};
		gl.glMaterialfv( GL.GL_FRONT,GL.GL_SPECULAR, specColor, 0 );
		gl.glMaterialf( GL.GL_FRONT, GL.GL_SHININESS, 64 );

	}

	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		this.width = width;
		this.height = height;
	}

	public void setScene( Scene scene ) {
		this.scene = scene;
	}

	public Scene getScene() {
		return( this.scene );
	}

	public float getWidth() {
		return( this.width );
	}

	public float getHeight() {
		return( this.height );
	}


	public static Texture[][] loadTextureFromModel( GL gl, Model model ) {
		Texture[][] texture = new Texture[model.getNumberOfMaterials()][];
		for( int m=0; m<texture.length; m++ ) {
			ModelMaterial mat = model.getMaterial( m );
			texture[m] = new Texture[mat.getNumberOfTextures()];
			for( int t=0; t<texture[m].length; t++ ) {
				TextureData tex = LoadModel.getTextureFromFile( mat.getTexture( t ).getImage() );
				if( tex != null ) {
					texture[m][t] = TextureIO.newTexture( tex );
				}
			}
		}
		return( texture );
	}

}
