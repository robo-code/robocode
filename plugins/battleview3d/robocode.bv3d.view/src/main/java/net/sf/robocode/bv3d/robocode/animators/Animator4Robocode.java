/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode.animators;


import java.util.ArrayList;
import java.awt.*;

import net.sf.robocode.bv3d.Animator;
import net.sf.robocode.bv3d.MVCManager;
import net.sf.robocode.bv3d.Scene;
import net.sf.robocode.bv3d.robocode.*;
import net.sf.robocode.bv3d.scenegraph.Drawable;
import net.sf.robocode.bv3d.scenegraph.Light;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;
import net.sf.robocode.battle.snapshot.TurnSnapshot;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Animator4Robocode extends Animator implements RobocodeOptionable {
	
	/** Field is the battle-field and is the father of Tanks, Bullets, Tracks and Explosions in TrasformationNode tree 
	 * @see Scene#root */
	protected Field field;
	protected ArrayList<Tank> tanks;
	protected ArrayList<Bullet> bullets;
	protected ArrayList<Track> tracks;
	protected ArrayList<Explosion> explosions;
	protected int time;
	
	protected boolean tankTrack, explosion, bulletWake;
	
	/**
	 * Initialize all class fields. 
	 * @param manager the MVCManager
	 */
	public Animator4Robocode(MVCManager manager) {
		super(manager);
		
		tanks = new ArrayList<Tank>();
		bullets = new ArrayList<Bullet>();
		tracks = new ArrayList<Track>();
		explosions = new ArrayList<Explosion>();
		
		time = 0;
		
		tankTrack = true;
		explosion = true;
		bulletWake = true;
	}
	
	/**
	 * Here Battlefield and Lights are created and added to Scene.
	 * @param xField field width dimension
	 * @param yField field heigth dimension
	 * @see Field
	 * @see Light
	 */
	public void setupScene(float xField, float yField) {
		if (field != null) {
			clearScene();
			tanks = new ArrayList<Tank>();
			bullets = new ArrayList<Bullet>();
			tracks = new ArrayList<Track>();
			explosions = new ArrayList<Explosion>();
		}
		
		// First, set up Lights: some Lights are independent from the Field
		// and so, are added directly to the Scene
		Light light0 = new Light(0);

		light0.setPosition(-0.4f, 0.99f, 0.7f);
		light0.setDiffuseIntensity(1, 1, 1);
		light0.setSpecularIntensity(1, 1, 1);
		light0.turnOn();

		Light light1 = new Light(1);

		light1.setPosition(0.4f, 0.99f, -0.7f);
		light1.setDiffuseIntensity(1, 1, 1);
		light1.setSpecularIntensity(1, 1, 1);
		light1.turnOn();

		addDrawableToScene(light0);
		addDrawableToScene(light1);
		
		// Second, the SkyDome
		addDrawableToScene(new SkyDome());
		
		// Third, the Field, with the parameters read from XML
		this.field = new Field(xField, yField);
		addDrawableToScene(field);
		
		displayMessage("New Battle is now displaying");
	}
	
	/**
	 * Cleen-up the scene 
	 */
	public void newRound() {
		this.setupScene(this.field.getWidth(), this.field.getHeight());
	}
	
	/**
	 * Update the transormation node with the snapshot
	 * @param turn
	 */
	public void processTurn(TurnSnapshot turn) {
		this.time = turn.getTurn();
		
		// Tank management
		for (IRobotSnapshot robot : turn.getRobots()) {
			if (robot.getState().isAlive()) {
				boolean isNew = true;

				for (Tank tank : tanks) {
					if (tank.getName().equals(robot.getVeryShortName())) {
						this.updateTank(tank, robot);
						isNew = false;
						break;
					}
				}
				if (isNew) {
					Tank tank = new Tank(robot.getVeryShortName());

					tank.setColors(new Color(robot.getBodyColor(), true), new Color(robot.getGunColor(), true),
							new Color(robot.getRadarColor(), true));
					this.addPrimary(tank);
					this.updateTank(tank, robot);
				}
			} else {
				int i = 0;

				while (i < tanks.size()) {
					Tank t = tanks.get(i);

					if (t.getName().equals(robot.getVeryShortName())) {
						removeDrawableFromScene(t);
						break;
					} else {
						i++;
					}
				}
			}
		}
		
		// Bullet management
		boolean isActive = false;

		for (int i = 0; i < bullets.size();) {
			Bullet bulletTN = bullets.get(i);

			for (IBulletSnapshot bullet : turn.getBullets()) {
				if (bulletTN.getName().equals("bullet" + bullet.getBulletId())) {
					isActive = true;
					break;
				}
			}
			if (!isActive) {
				this.removeDrawableFromScene(bulletTN);
			} else {
				i++;
			}
		}		
		for (IBulletSnapshot bullet : turn.getBullets()) {
			if (bullet.getState().getValue() < 2) {
				boolean isNew = true;

				for (Bullet bulletTN : bullets) {
					if (bulletTN.getName().equals("bullet" + bullet.getBulletId())) {
						isNew = false;
						this.updateBullet(bulletTN, bullet);
						break;
					}
				}
				if (isNew) {
					Bullet b = new Bullet("bullet" + bullet.getBulletId());

					b.setPower((float) bullet.getPower());
					this.addPrimary(b);
					this.updateBullet(b, bullet);
				}
			} else {
				for (int i = 0; i < bullets.size();) {
					Bullet b = bullets.get(i);

					if (b.getName().equals("bullet" + bullet.getBulletId())) {
						removeDrawableFromScene(b);
						break;
					} else {
						i++;
					}
				}
			}
		}
		
		// Tank Track management
		int i = 0;

		while (tankTrack && i < tracks.size()) {
			Track track = tracks.get(i);

			track.heartBeat();
			if (this.time - track.getCreationTime() > Track.LIFETIME) {
				removeDrawableFromScene(track);
			} else {
				i++;
			}
		}
		// Explosions management
		int j = 0;

		while (j < explosions.size()) {
			Explosion exp = explosions.get(j);
			boolean alive = exp.heartBeat();

			if (alive) {
				j++;
			} else {
				removeDrawableFromScene(exp);
			}
		}
	}
	
	private void updateTank(Tank t, IRobotSnapshot s) {
		// Reading information from snapshot
		float xpos = (float) s.getX();
		float ypos = (float) s.getY();

		ypos = field.getHeight() - ypos;
		float angle = (float) s.getBodyHeading();
		float energy = (float) s.getEnergy();
		float head = (float) s.getGunHeading();
		float radar = (float) s.getRadarHeading();
		
		// Normalization
		angle = -(float) (angle * 180 / Math.PI) - 180;
		head = -(float) (head * 180 / Math.PI) - angle - 180;
		radar = -(float) (radar * 180 / Math.PI) - angle - head - 180;
		
		// Scenegraph update
		t.setRotate(angle, 0, 1, 0);
		t.setTranslate(xpos, 0, ypos);
		t.head.setRotate(head, 0, 1, 0);
		t.radar.setRotate(radar, 0, 1, 0);
		t.setEnergy(energy);
		
		if (this.tankTrack) {
			addPrimary(new Track(this.time, xpos, ypos, angle));
		}
	}
	
	private void updateBullet(Bullet b, IBulletSnapshot s) {
		float xpos = (float) s.getX();
		float ypos = (float) s.getY();

		ypos = field.getHeight() - ypos;
		
		if (this.bulletWake) { 
			this.addPrimary(new BulletWake(b.getTx(), b.getTz(), xpos, ypos));
		}
		
		b.setTranslate(xpos, 0.07f, ypos);
	}
	
	/**
	 * Primaries are children of Field, like tanks and bullets.
	 * @param tn Tank, Bullet, Track or Explosion to add
	 */
	private void addPrimary(TransformationNode tn) {
		field.addDrawable(tn);
		if (tn instanceof Tank) { 
			tanks.add((Tank) tn);
			this.addFollowed(((Tank) tn).getTNHead());
			this.addFrontal(((Tank) tn).getText3D());
		} else if (tn instanceof Bullet) {
			bullets.add((Bullet) tn);
		} else if (tn instanceof Track) {
			tracks.add((Track) tn);
		} else if (tn instanceof Explosion) {
			explosions.add((Explosion) tn);
		}
	}
	
	/**
	 * Remove from Scene and from class field.
	 * If {@link Drawable} is a {@link Tank}, add new {@link BigExplosion}.
	 * If {@link Drawable} is a {@link Bullet}, add new {@link LittleExplosion}.
	 * @param d the element to remove
	 */
	@Override
	public void removeDrawableFromScene(Drawable d) {
		if (d instanceof Tank) {
			tanks.remove(d);
			this.removeFollowed(((Tank) d).getTNHead());
			this.removeFrontal(((Tank) d).getText3D());
			// add the explosion!
			if (explosion) {
				Explosion exp = new BigExplosion(((Tank) d).getTx(), ((Tank) d).getTz());

				this.addPrimary(exp);
			}
		} else if (d instanceof Bullet) {
			bullets.remove(d);
			if (explosion) {
				this.addPrimary(new LittleExplosion(((Bullet) d).getTx(), ((Bullet) d).getTz()));
			}
		} else if (d instanceof Track) {
			tracks.remove((Track) d);
		} else if (d instanceof Explosion) {
			explosions.remove(d);
		}
		super.removeDrawableFromScene(d);
	}

	public boolean isTankTrackEnable() {
		return this.tankTrack;
	}

	public void setTankTrackEnable(boolean tt) {
		this.tankTrack = tt;
		if (!tankTrack) {
			while (!tracks.isEmpty()) {
				removeDrawableFromScene(tracks.get(0));
			}
		}
	}

	public boolean isBulletWakeEnable() {
		return this.bulletWake;
	}

	public boolean isExplosionEnable() {
		return this.explosion;
	}

	public void setBulletWakeEnable(boolean bw) {
		this.bulletWake = bw;
		int i = 0;

		while (!bulletWake && i < explosions.size()) {
			Explosion e = explosions.get(i);

			if (e instanceof BulletWake) {
				removeDrawableFromScene(e);
			} else {
				i++;
			}
		}
	}

	public void setExplosionEnable(boolean exp) {
		this.explosion = exp;
		int i = 0;

		while (!explosion && i < explosions.size()) {
			Explosion e = explosions.get(i);

			if (e instanceof LittleExplosion || e instanceof BigExplosion) {
				removeDrawableFromScene(e);
			} else {
				i++;
			}
		}
	}

	@Override
	protected void setup() {}

	@Override
	protected void updateScene() {}

}
