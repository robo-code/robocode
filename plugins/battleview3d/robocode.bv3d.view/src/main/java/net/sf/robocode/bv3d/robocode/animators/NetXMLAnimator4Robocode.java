/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode.animators;


import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.robocode.bv3d.MVCManager;
import net.sf.robocode.bv3d.NetXMLAnimator;
import net.sf.robocode.bv3d.Scene;
import net.sf.robocode.bv3d.robocode.BigExplosion;
import net.sf.robocode.bv3d.robocode.Bullet;
import net.sf.robocode.bv3d.robocode.BulletWake;
import net.sf.robocode.bv3d.robocode.Explosion;
import net.sf.robocode.bv3d.robocode.Field;
import net.sf.robocode.bv3d.robocode.LittleExplosion;
import net.sf.robocode.bv3d.robocode.RobocodeOptionable;
import net.sf.robocode.bv3d.robocode.SkyDome;
import net.sf.robocode.bv3d.robocode.Tank;
import net.sf.robocode.bv3d.robocode.Track;
import net.sf.robocode.bv3d.scenegraph.Drawable;
import net.sf.robocode.bv3d.scenegraph.Light;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

// TODO this needs to be revisited, probably rewritten from scratch or deleted
// TODO we no longer use XML
public class NetXMLAnimator4Robocode extends NetXMLAnimator implements RobocodeOptionable {
	
	private static final String TANK = "tank";
	private static final String BULLET = "bullet";
	
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
	 * Initialize all class fields. FPS are set to 200 for take the same speed of the client. 
	 * @param manager the MVCManager
	 * @param portNumber Port number for the connection
	 */
	public NetXMLAnimator4Robocode(MVCManager manager, int portNumber) {
		super(manager, portNumber);
		this.setFPS(200); // this is for take the same speed of the client
		
		tanks = new ArrayList<Tank>();
		bullets = new ArrayList<Bullet>();
		tracks = new ArrayList<Track>();
		explosions = new ArrayList<Explosion>();
		
		time = 0;
		
		tankTrack = false;
		explosion = false;
		bulletWake = false;
	}
	
	public NetXMLAnimator4Robocode(MVCManager manager) {
		this(manager, 4444);
	}
	
	/**
	 * Redirect the processing to {@link NetXMLAnimator4Robocode#setupScene(Node)} or
	 * {@link NetXMLAnimator4Robocode#processTurn(Node)}.
	 */
	@Override
	protected void processXMLNode(Node el) {
		if (el.getNodeName().equals("settings")) {
			setupScene(el);
		} else if (el.getNodeName().equals("turn")) {
			// this.turn = el;
			processTurn(el);
		}
	}
	
	/**
	 * Here Battlefield and Lights are created and added to Scene.
	 * Tanks settings are read and passed to {@link NetXMLAnimator4Robocode#newTNode(Node)}
	 * @param settings The XML-formed line with the settings
	 * @see Field
	 * @see Light
	 */
	protected void setupScene(Node settings) {
		if (field != null) {
			clearScene();
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
		NodeList nodelist = settings.getChildNodes();
		float width = Float.parseFloat(nodelist.item(0).getAttributes().getNamedItem("sizeX").getNodeValue());
		float heigth = Float.parseFloat(nodelist.item(0).getAttributes().getNamedItem("sizeY").getNodeValue());

		this.field = new Field(width, heigth);
		addDrawableToScene(field);
		
		// Last, tanks settings passed at newTNode(Node)
		for (int i = 1; i < nodelist.getLength(); i++) {
			addPrimary(newTNode(nodelist.item(i)));
		}
		
		displayMessage("New Battle is now displaying");
	}
	
	private void processTurn(Node turn) {
		this.time = Integer.parseInt(turn.getAttributes().getNamedItem("time").getNodeValue());
		
		// Second, process all the xml nodes
		ArrayList<Node> nodes = new ArrayList<Node>();
		NodeList nodelist = turn.getChildNodes();

		for (int i = 0; i < nodelist.getLength(); i++) {
			nodes.add(nodelist.item(i));
		}
		processNodesThisTurn(nodes);
				
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
	
	private void processNodesThisTurn(ArrayList<Node> nodesThisTurn) {
		// Find Nodes to delete
		for (TransformationNode tn : this.getTanksAndBullets()) {
			boolean stillActive = false;

			for (Node n : nodesThisTurn) {
				if (tn.getName().equals(n.getAttributes().getNamedItem("name").getNodeValue())) {
					stillActive = true;
					break;
				}
			}
			if (!stillActive) {
				this.removeDrawableFromScene(tn);
			}
		}
		
		// Update existing Nodes and create new Nodes
		for (Node n : nodesThisTurn) {
			String nName = n.getAttributes().getNamedItem("name").getNodeValue();
			boolean isNew = true;

			for (TransformationNode tn : this.getTanksAndBullets()) {
				if (tn.getName().equals(nName)) {
					isNew = false;
					updateNode(tn, n);
				}
			}
			if (isNew) {
				addPrimary(newTNode(n));
			}
		}
	}
	
	/**
	 * Primaries are children of Field, like tanks and bullets.
	 * @param tn Tank, Bullet, Track or Explosion to add
	 */
	protected void addPrimary(TransformationNode tn) {
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
	
	protected ArrayList<TransformationNode> getTanksAndBullets() {
		ArrayList<TransformationNode> al = new ArrayList<TransformationNode>();

		al.addAll(tanks);
		al.addAll(bullets);
		return al;
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
			tanks.remove((Tank) d);
			this.removeFollowed(((Tank) d).getTNHead());
			this.removeFrontal(((Tank) d).getText3D());
			// add the explosion!
			if (explosion) {
				Explosion exp = new BigExplosion(((Tank) d).getTx(), ((Tank) d).getTz());

				this.addPrimary(exp);
			}
		} else if (d instanceof Bullet) {
			bullets.remove((Bullet) d);
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
	
	private TransformationNode newTNode(Node n) {
		TransformationNode tn = null;

		if (n.getNodeName().equals(TANK)) {
			Tank tank = new Tank(n.getAttributes().getNamedItem("name").getNodeValue());
			Color body = null, head = null, radar = null;

			if (n.getAttributes().getNamedItem("body_color") != null) {
				body = new Color(Integer.parseInt(n.getAttributes().getNamedItem("body_color").getNodeValue()));
			}
			if (n.getAttributes().getNamedItem("head_color") != null) {
				head = new Color(Integer.parseInt(n.getAttributes().getNamedItem("head_color").getNodeValue()));
			}
			if (n.getAttributes().getNamedItem("radar_color") != null) {
				head = new Color(Integer.parseInt(n.getAttributes().getNamedItem("radar_color").getNodeValue()));
			}
			tank.setColors(body, head, radar);
			tn = tank;
		} else if (n.getNodeName().equals(BULLET)) {
			tn = new Bullet(n.getAttributes().getNamedItem("name").getNodeValue());
			((Bullet) (tn)).setPower(Float.parseFloat(n.getAttributes().getNamedItem("power").getNodeValue()));
			updateNode(tn, n);
		}
		// updateNode(tn, n);
		return tn;
	}
	
	/**
	 * Read the XML {@link Node} and update the respective {@link TransformationNode}.
	 * Here there are also the creations of tank {@link Track} and {@link BulletWake}.
	 * @param tn {@link Bullet} or {@link Tank} to update
	 * @param n the XML Node from which take data
	 */
	private void updateNode(TransformationNode tn, Node n) {
		if (n.getNodeName().equals(TANK)) {
			Tank tank = (Tank) tn;
			// Reading information from XML
			float xpos = Float.parseFloat(n.getAttributes().getNamedItem("xpos").getNodeValue());
			float ypos = Float.parseFloat(n.getAttributes().getNamedItem("ypos").getNodeValue());

			ypos = field.getHeight() - ypos;
			float angle = Float.parseFloat(n.getAttributes().getNamedItem("angle").getNodeValue());
			float energy = Float.parseFloat(n.getAttributes().getNamedItem("energy").getNodeValue());
			Node xhead = n.getFirstChild();
			float head = Float.parseFloat(xhead.getAttributes().getNamedItem("angle").getNodeValue());
			Node xradar = n.getLastChild();
			float radar = Float.parseFloat(xradar.getAttributes().getNamedItem("angle").getNodeValue());
			
			// Normalization
			angle = -(float) (angle * 180 / Math.PI) - 180;
			head = -(float) (head * 180 / Math.PI) - angle - 180;
			radar = -(float) (radar * 180 / Math.PI) - angle - head - 180;
			
			// Scenegraph update
			tank.setRotate(angle, 0, 1, 0);
			tank.setTranslate(xpos, 0, ypos);
			tank.head.setRotate(head, 0, 1, 0);
			tank.radar.setRotate(radar, 0, 1, 0);
			tank.setEnergy(energy);
			
			if (this.tankTrack/* && time%4==0 */) {
				addPrimary(new Track(this.time, xpos, ypos, angle));
			}
		} else if (n.getNodeName().equals(BULLET)) {
			Bullet bullet = (Bullet) tn;
			float xpos = Float.parseFloat(n.getAttributes().getNamedItem("xpos").getNodeValue());
			float ypos = Float.parseFloat(n.getAttributes().getNamedItem("ypos").getNodeValue());

			ypos = field.getHeight() - ypos;
			
			if (this.bulletWake) { 
				this.addPrimary(new BulletWake(bullet.getTx(), bullet.getTz(), xpos, ypos));
			}
			
			bullet.setTranslate(xpos, 0.07f, ypos);
		}
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

}
