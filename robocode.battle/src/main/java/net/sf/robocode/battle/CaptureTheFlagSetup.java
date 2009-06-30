/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
  *******************************************************************************/

package net.sf.robocode.battle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.io.FileUtil;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.*;


/**
 * This class sets up the objects for Capture the Flag
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagSetup extends BattlefieldSetup{
	
	public List<RobjectPeer> setupObjects(int battlefieldWidth, int battlefieldHeight) {
		List<RobjectPeer> robjects = new ArrayList<RobjectPeer>();
		
		try 
		{
			File file = new File(FileUtil.getMapsDir(), "Map2.xml");
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			doc = docBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("Object");
  
			for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) 
			{
 
				Node node = nodeList.item(nodeIndex);
	 
				if (node.getNodeType() == Node.ELEMENT_NODE) 
				{
		 
					Element element = (Element) node;
		 
					String type = getTagValue("Type", element);
					if (type.equals("Obstacle"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int width = Integer.parseInt(getTagValue("Width", element));
						int height = Integer.parseInt(getTagValue("Height", element));
						robjects.add(new BoxPeer(x, y, width, height));				
					}
					else if (type.equals("Trench"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int width = Integer.parseInt(getTagValue("Width", element));
						int height = Integer.parseInt(getTagValue("Height", element));
						robjects.add(new TrenchPeer(x, y, width, height));		
					}
					else if (type.equals("Flag"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int teamNumber = Integer.parseInt(getTagValue("Team", element));
						robjects.add(new FlagPeer(x, y, teamNumber));
					}
					else if (type.equals("Base"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int width = Integer.parseInt(getTagValue("Width", element));
						int height = Integer.parseInt(getTagValue("Height", element));
						int teamNumber = Integer.parseInt(getTagValue("Team", element));
						robjects.add(new BasePeer(x, y, width, height, teamNumber));	
					}
					else if (type.equals("Rubble"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int width = Integer.parseInt(getTagValue("Width", element));
						int height = Integer.parseInt(getTagValue("Height", element));
						robjects.add(new RubblePeer(x, y, width, height));	
					}
					else if (type.equals("EnergyPack"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						robjects.add(new EnergyPackPeer(x, y));
					}
				}
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkBoundaries(robjects, battlefieldWidth, battlefieldHeight);
	}

		private static String getTagValue(String tag, Element element)
		 {
			  NodeList nodeList= element.getElementsByTagName(tag).item(0).getChildNodes();
			  Node nValue = (Node) nodeList.item(0); 
		 
			  return nValue.getNodeValue();
		 
		 }

		@Override
		public double[][] computeInitialPositions(String initialPositions, int battlefieldWidth, int battlefieldHeight) {
			double[][] initialRobotPositions = new double[2][3];
			File file = new File(FileUtil.getMapsDir(), "Map2.xml");
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				
				doc = docBuilder.parse(file);
				doc.getDocumentElement().normalize();
				NodeList nodeList = doc.getElementsByTagName("StartingPoint");
	  
				for (int nodeIndex = 0; nodeIndex < 2; nodeIndex++) 
				{
					Node node = nodeList.item(nodeIndex);
		 
					if (node.getNodeType() == Node.ELEMENT_NODE) 
					{
			 
						Element element = (Element) node;
			 
						double x = Double.parseDouble(getTagValue("X", element));
						double y = Double.parseDouble(getTagValue("Y", element));
						double heading = Double.parseDouble(getTagValue("Heading", element));
						
						//Make sure coordinates are at least 20 units away from boundaries,
						//otherwise the robots will shift to new locations their first turns
						
						if (x < 20) x = 20;
						if (x > battlefieldWidth - 20) x = battlefieldWidth - 20;
						if (y < 20) y = 20;
						if (y > battlefieldHeight - 20) y = battlefieldHeight - 20;
						if (heading < 0) heading = 0;
						while (heading > Math.PI * 2) heading -= Math.PI * 2;
						
						//adjust to '0 degrees is East' TODO: remove for official version
						heading = -1 * heading + (Math.PI / 2);
						
						initialRobotPositions[nodeIndex][0] = x;
						initialRobotPositions[nodeIndex][1] = y;
						initialRobotPositions[nodeIndex][2] = heading;
					}
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return initialRobotPositions;
		}

}
