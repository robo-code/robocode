/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;

import com.sun.opengl.util.texture.Texture;
import pimods.math.Vertex3f;
import pimods.scenegraph.Ageing;
import pimods.scenegraph.ParticleSystem;
import pimods.scenegraph.TransformationNode;
import pimods.scenegraph.TextureIndexLink;
import pimods.scenegraph.DisplayListIndexLink;
import pimods.model.LoadModel;
import pimods.model.Model;
import pimods.scenegraph.ModelView;



/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Explosion extends TransformationNode implements Ageing{
	public static final Model modelSparkle = LoadModel.getModelFromFile( "particle1-sparkle.pobj" );
	public static final Model modelSmoke = LoadModel.getModelFromFile( "particle1-smoke.pobj" );
	private static final TextureIndexLink textureSparkle = new TextureIndexLink();
	private static final TextureIndexLink textureSmoke = new TextureIndexLink();
	private static final DisplayListIndexLink displayListSparkle = new DisplayListIndexLink();
	private static final DisplayListIndexLink displayListSmoke = new DisplayListIndexLink();

	private ParticleSystem particleSystemSparkle;
	private ParticleSystem particleSystemSmoke;
	
	private int ttl;

	
	public Explosion(int numParticles, float position_var, float velocity,
			float velocity_var, int lifetime, int lifetime_var,
			float direction_var, Vertex3f direction ) {

		ModelView mvSparkle = new ModelView( modelSparkle, "Particle", displayListSparkle, textureSparkle );
		ModelView mvSmoke = new ModelView( modelSmoke, "Particle", displayListSmoke, textureSmoke );
		
//		this.ttl = (int)(lifetime*0.3);
		this.ttl = 4;
		
		this.particleSystemSparkle = new ParticleSystem( (int)(numParticles*0.7), position_var, velocity, velocity_var, lifetime,
				lifetime_var, direction_var, direction, this.getTy(), new Vertex3f( 0, -9.8f, 0 ), mvSparkle );

		this.particleSystemSmoke = new ParticleSystem( (int)(numParticles*0.3), position_var*3f, velocity*0.8f, velocity_var, (int)(lifetime*0.6),
				lifetime_var, direction_var, direction, this.getTy(), new Vertex3f( 0, 0.2f, 0 ), mvSmoke );
		direction.normalize();
		this.particleSystemSmoke.setTranslate(direction.getX()*lifetime*0.7f, direction.getY()*lifetime*0.7f, direction.getZ()*lifetime*0.7f);
		this.addDrawable( this.particleSystemSparkle );
//		this.addDrawable( this.particleSystemSmoke );
	}
	
	public boolean heartBeat(){
		ttl--;
		if(ttl==0){
			this.addDrawable(this.particleSystemSmoke);
		}
		boolean alive1 = this.particleSystemSparkle.heartBeat();
		boolean alive2 = true;
		if(ttl<0) alive2=this.particleSystemSmoke.heartBeat();
		return( alive1 || alive2 );
	}

	public static void setTextureSparkle( int[][] t ) {
		textureSparkle.setTextureIndexLink( t );
	}

	public static void setTextureSmoke( int[][] t ) {
		textureSmoke.setTextureIndexLink( t );
	}
	
	public static void setDisplayListSparkle( int[] grpIndex ) {
		displayListSparkle.setDisplayListIndex( grpIndex );
	}
	
	public static void setDisplayListSmoke( int[] grpIndex ) {
		displayListSmoke.setDisplayListIndex( grpIndex );
	}

}
