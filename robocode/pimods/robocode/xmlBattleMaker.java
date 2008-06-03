/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.robocode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

@Deprecated
public class xmlBattleMaker { 
	
	private xmlEl xml;
	private xmlEl currentRound;
	private xmlEl currentTurn;

	public xmlBattleMaker(){
		System.out.println("XML Maker");
	}
	public void addBattle( int sizeX, int sizeY){
		xml = new xmlEl( "battle");
		xml.params.add( new xmlParam("sizeX", Integer.toString(sizeX)));
		xml.params.add( new xmlParam("sizeY", Integer.toString(sizeY)));
	}
	public void addRound( int numR){
		currentRound = new xmlEl("round");
		currentRound.params.add( new xmlParam("index", Integer.toString(numR)));
		xml.elements.add( currentRound);
	}
	public void addTurn( int numT){
		currentTurn = new xmlEl("turn");
		currentTurn.params.add( new xmlParam("time", Integer.toString(numT)));
		currentRound.elements.add( currentTurn);
	}
	public void addTankPosition(
			String name,
			double xpos,
			double ypos,
			double angle,
			double energy,
			double headAngle,
			double radarAngle)
	{
		xmlEl tank = new xmlEl("tank");
		tank.addParam( "name", name);
		tank.addParam( "xpos", Double.toString(xpos));
		tank.addParam( "ypos", Double.toString(ypos));
		tank.addParam( "angle", Double.toString(angle));
		tank.addParam( "energy", Double.toString(energy));
		
		xmlEl head = new xmlEl("head");
		head.addParam("angle", Double.toString(headAngle));
		xmlEl radar = new xmlEl("radar");
		radar.addParam("angle", Double.toString(radarAngle));
		tank.elements.add(head);
		tank.elements.add(radar);
		currentTurn.elements.add( tank);
	}
	
	public void addBullet(String name, double xpos, double ypos, double power){
		xmlEl bullet = new xmlEl("bullet");
		bullet.addParam( "name", name);
		bullet.addParam( "xpos", Double.toString(xpos));
		bullet.addParam( "ypos", Double.toString(ypos));
		bullet.addParam( "power", Double.toString(power));
		currentTurn.elements.add( bullet);
	}
	
	public void close() throws FileNotFoundException{
		PrintStream s = new PrintStream( new File( "battle4pimods.xml" ));
		s.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		s.println( xml);
		s.close();
	}
	
	public class xmlEl{
		public String name;
		public ArrayList<xmlEl> elements;
		public ArrayList<xmlParam> params;
		
		public xmlEl( String name){
			this.name = name;
			params = new ArrayList<xmlParam>();
			elements = new ArrayList<xmlEl>();
		}
		
		public void addParam(String name, String value){
			params.add(new xmlParam(name, value));
		}

		public String toString(){
			String res = "<"+this.name;
			for(xmlParam p : params){
				res+=" "+p;
			}
			if( elements.size()==0)
				res+=" />";
			else{
				res+=">\n";
				for(xmlEl e : elements){
					res+=e+"\n";
				}
				res+="</"+this.name+">\n";
			}
			return res;
		}
		
	}
	public class xmlParam{
		public String name;
		public String value;
		
		public xmlParam( String name, String value){
			this.name=name;
			this.value=value;
		}

		public String toString( ){
			return name+"=\""+value+"\"";
		}
	}
}
