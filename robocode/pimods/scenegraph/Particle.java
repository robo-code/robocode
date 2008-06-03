/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.scenegraph;

import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;

import pimods.math.Vertex3f;
import pimods.scenegraph.ModelView;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Particle extends TransformationNode {
	private Vertex3f velocity;
	private int ttl;

	public Particle( Vertex3f velocity, int ttl, ModelView mvParticle ) {
		this.velocity = velocity;
		this.ttl = ttl;
		this.addDrawable( mvParticle );
	}

	public void setPosition( Vertex3f p ) {
		this.setTranslate( p.x, p.y, p.z );
	}

	public void setVelocity( Vertex3f v ) {
		this.velocity = v;
	}

	public void setTTL( int ttl ) {
		this.ttl = ttl;
	}

	public Vertex3f getPosition() {
		return( new Vertex3f( this.getTx(), this.getTy(), this.getTz() ) );
	}

	public Vertex3f getVelocity() {
		return( ( Vertex3f ) this.velocity.clone() );
	}

	public int getTTL() {
		return( this.ttl );
	}
	
	@Override
	public void draw(GL gl){
		if(ttl>0) super.draw(gl);
	}


}
