/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * This class has been moved into Robocode to minimize dependences
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 * 
 */

@Deprecated
public class xmlBattleStreamMaker {

	private boolean currentRound = false;
	private xmlEl currentTurn;
	private xmlEl settings= new xmlEl("settings");
	private PrintStream out;

	public xmlBattleStreamMaker() {
		try {
			out = new PrintStream(new File("battle4pimods.xml"));
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// XXX do something?
		}
	}

	/**
	 * Remember: battle settings ( setupTank()) must be called before this method.
	 * @param sizeX Battlefield dimension on X
	 * @param sizeY Battlefield dimension on Y
	 */
	public void addBattle(int sizeX, int sizeY) {
		out.print("<battle");
//		out.print(new xmlParam("sizeX", Integer.toString(sizeX)));
//		out.print(new xmlParam("sizeY", Integer.toString(sizeY)));
		out.print(">");
		out.print(settings);
	}
	

	public void addRound(int numR) {
		if (currentRound)
			out.println("</round>");
		out.print("<round");
		out.print(new xmlParam("index", Integer.toString(numR)));
		out.print(">");
		currentRound = true;
	}

	public void addTurn(int numT) {
		if (currentTurn != null)
			out.print(currentTurn);
		currentTurn = new xmlEl("turn");
		currentTurn.params.add(new xmlParam("time", Integer.toString(numT)));
	}
	
	public void setupField( int x, int y ){
		xmlEl field = new xmlEl("field");
		field.addParam( "sizeX", Integer.toString(x) );
		field.addParam( "sizeY", Integer.toString(y) );
		settings.elements.add( field );
	}
	
	public void setupTank(String name, Color body, Color head, Color radar, Color bullet){
		xmlEl tank = new xmlEl("tank");
		tank.addParam("name", name);
		if(body!=null) tank.addParam("body_color", Long.toString(body.getRGB()));
		if(head!=null) tank.addParam("head_color", Long.toString(head.getRGB()));
		if(radar!=null) tank.addParam("radar_color", Long.toString(radar.getRGB()));
		if(bullet!=null) tank.addParam("bullet_color", Long.toString(bullet.getRGB()));
		settings.elements.add(tank);
	}

	public void addTankPosition(String name, double xpos, double ypos,
			double angle, double energy, double headAngle, double radarAngle) {
		xmlEl tank = new xmlEl("tank");
		tank.addParam("name", name);
		tank.addParam("xpos", String.format("%g", xpos));
		tank.addParam("ypos", String.format("%g", ypos));
		tank.addParam("angle", String.format("%g", angle));
		tank.addParam("energy", String.format("%g", energy));
		xmlEl head = new xmlEl("head");
		head.addParam("angle", String.format("%g", headAngle));
		xmlEl radar = new xmlEl("radar");
		radar.addParam("angle", String.format("%g", radarAngle));
		tank.elements.add(head);
		tank.elements.add(radar);
		currentTurn.elements.add(tank);
	}

	public void addBullet(String name, double xpos, double ypos, double power) {
		xmlEl bullet = new xmlEl("bullet");
		bullet.addParam("name", name);
		bullet.addParam("xpos", String.format("%g", xpos));
		bullet.addParam("ypos", String.format("%g", ypos));
		bullet.addParam("power", String.format("%g", power));
		currentTurn.elements.add(bullet);
	}
	public String getCurrentTurn(){
		return currentTurn.toString();
	}
	public String getSettings(){
		return settings.toString();
	}

	public void close() throws FileNotFoundException {
		out.print("</round>");
		out.print("</battle>");
		out.close();
	}

	private class xmlEl {
		public String name;
		public ArrayList<xmlEl> elements;
		public ArrayList<xmlParam> params;

		public xmlEl(String name) {
			this.name = name;
			params = new ArrayList<xmlParam>();
			elements = new ArrayList<xmlEl>();
		}

		public void addParam(String name, String value) {
			params.add(new xmlParam(name, value));
		}

		public String toString() {
			// TODO sistemare indentazione
			String res = "<" + this.name;
			for (xmlParam p : params) {
				res += p;
			}
			if (elements.size() == 0)
				res += " />";
			else {
				res += ">";
				for (xmlEl e : elements) {
					res += "" + e;
				}
				res += "</" + this.name + ">";
			}
			return res;
		}

	}

	private class xmlParam {
		public String name;
		public String value;

		public xmlParam(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return " " + name + "=\"" + value + "\"";
		}
	}
}
