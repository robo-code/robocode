/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


import java.util.Random;

import net.sf.robocode.bv3d.math.Vertex3f;
import net.sf.robocode.bv3d.scenegraph.ModelView;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ParticleSystem extends TransformationNode implements Ageing {
	private static Random random = new Random();

	private static final float ELAPSED_TIME = 1.0f / 25; // TODO variabilizzare/mettere in funzione dell'aggiornamento della scena?
	private int numParticles;
	private float position_var, velocity, velocity_var, direction_var, groundHeight;
	private int lifetime, lifetime_var;
	private Vertex3f direction;
	private Vertex3f gravity;

	private Particle particle[];
	
	public ParticleSystem(int numParticles, float position_var, float velocity, float velocity_var,
			int lifetime, int lifetime_var, float direction_var,
			Vertex3f direction, float groundHeight, Vertex3f gravity, ModelView mvParticle) {

		this.numParticles = numParticles;
		this.velocity = velocity;
		this.velocity_var = velocity_var;
		this.lifetime = lifetime;
		this.lifetime_var = lifetime_var;
		this.direction_var = direction_var;
		this.direction = direction;
		this.gravity = gravity;
		this.groundHeight = groundHeight;
		
		this.particle = new Particle[numParticles];

		for (int i = 0; i < numParticles; i++) {
			Vertex3f pos = new Vertex3f((float) random.nextGaussian() / 2 * position_var,
					(float) random.nextGaussian() / 2 * position_var, (float) random.nextGaussian() / 2 * position_var);
			
			// Particles underground are forbidden!
			if (groundHeight + pos.getY() < 0) {
				pos.setY(-pos.getY());
			}

			Vertex3f vel = (Vertex3f) direction.clone();

			// vel.normalize();
			vel.x += (float) random.nextGaussian() / 2 * direction_var;
			vel.y += (float) random.nextGaussian() / 2 * direction_var;
			vel.z += (float) random.nextGaussian() / 2 * direction_var;
			vel.normalize();
			vel.mul(velocity + (float) random.nextGaussian() / 2 * velocity_var);
			
			int ttl = lifetime + random.nextInt(lifetime_var) - lifetime / 2;

			Particle p = new Particle(vel, ttl, mvParticle);

			p.setRotate(random.nextFloat() * 180, 1, 1, 1);
			p.setPosition(pos);
			p.setScale(2f, 2f, 2f);
			this.particle[i] = p;
			this.addDrawable(p);
		}		
	}
	
	public boolean heartBeat() {
		boolean alive = false;

		for (int i = 0; i < this.particle.length; i++) {
			Vertex3f pos = particle[i].getPosition();
			Vertex3f vel = particle[i].getVelocity();
			int ttl = particle[i].getTTL();

			pos.add(vel);
			Vertex3f speedUp = ((Vertex3f) this.gravity.clone());

			speedUp.mul(ELAPSED_TIME);
			vel.add(speedUp);			
			
			if (pos.y + this.groundHeight < 0) {
				vel.y = -vel.y;
				vel.mul(0.5f);
			}

			particle[i].setPosition(pos);
			particle[i].setVelocity(vel);
			particle[i].setTTL(--ttl);
			
			if (!alive && ttl > 0) {
				alive = true;
			}
		}

		return(alive);
	}

}
