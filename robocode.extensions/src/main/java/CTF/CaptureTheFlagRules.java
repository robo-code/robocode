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


package CTF;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import robocode.DeathEvent;
import robocode.IExtensionApi;
import robocode.MessageEvent;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.RobotState;

import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.CustomRules;
import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ContestantPeer;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;
import net.sf.robocode.io.FileUtil;

/**
 * This class contains the rules for a Capture the Flag game
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagRules extends CustomRules {
	
	//TODO: refactor team rosters, flags, and bases into a single object
	List<List<String>> teams;
	List<RobotPeer> robotsInTeams;
	List<RobjectPeer> robjects;
	
	int messageDecayStart;
	int messageDecayExp;

	FlagPeer firstFlag;
	FlagPeer secondFlag;
	BasePeer firstBase;
	BasePeer secondBase;
	private CaptureTheFlagExtensionApi exApi;
	private int captures = 0;

	public boolean isGameOver(int activeRobots, List<RobotPeer> robots, 
			List<RobjectPeer> robjects) {
	
		if (activeRobots <= 1) {
			return true;
		}
		
		return captures > 0;
	}
	
	@Override
	public void finishTurn(Battle battle) {
		for (RobjectPeer robject : robjects) 
		{
		
		robject.turnUpdate();

			if (robject.getType().equals("flag"))
			{
				FlagPeer flag = (FlagPeer)robject;
				//exApi.updateFlag((Flag)robject);
				for (RobjectPeer otherRobject : robjects)
				{
					if (otherRobject.getType().equals("base") &&
							flag.getTeam() != otherRobject.getTeam() &&
							otherRobject.getBoundaryRect().contains(flag.getBoundaryRect()))
					{
						((CaptureTheFlagRobotStatistics)flag.getHolder().getStatistics()).scoreCapture();
						flag.capture();
						captures++;
					}
				}			
			}
		}
	}

	@Override
	public void robotKill(RobotPeer robot) {
		robot.getBattle().resetInactiveTurnCount(10.0);
		if (robot.isAlive()) {
			robot.addEvent(new DeathEvent());
			//TODO: this branch necessary?
			if (robot.isTeamLeader()) {
				for (RobotPeer teammate : robot.getTeamPeer()) {
					if (!(teammate.isDead() || teammate == robot)) {
						teammate.updateEnergy(-30);

						BulletPeer sBullet = new BulletPeer(robot, 
								robot.getBattle().getBattleRules(), -1);

						sBullet.setState(BulletState.HIT_VICTIM);
						sBullet.setX(teammate.getX());
						sBullet.setY(teammate.getY());
						sBullet.setVictim(teammate);
						sBullet.setPower(4);
						robot.getBattle().addBullet(sBullet);
					}
				}
			}

			// 'fake' bullet for explosion on self
			final ExplosionPeer fake = new ExplosionPeer(robot, robot.getBattle().getBattleRules());

			robot.getBattle().addBullet(fake);
		}
		robot.updateEnergy(-robot.getEnergy());
		((CaptureTheFlagRobotStatistics)robot.getStatistics()).scoreRobotDeath(0);
		robot.setState(RobotState.DEAD);
	}	
	
	@Override
	//Respawn!
	public void robotIsDead(RobotPeer robot)
	{
		if (robot.isDead())
		{
			robot.updateEnergy(1);
			if (robot.getEnergy() >= 100)
			{
				robot.setX(20);
				robot.setY(20);
				robot.setState(RobotState.ACTIVE);
				robot.updateBoundingBox();
				robot.setIsExecFinishedAndDisabled(false);
			}
		}
	}
	
	@Override
	public int computeActiveRobots(List<RobotPeer> robots)
	{
		return robots.size();
	}

	@Override
	public void startRound(Battle battle) {

		this.robjects = battle.getRobjects();
		
		captures = 0;
		
		
		for (RobjectPeer robject : robjects)
		{
			if (robject.getType().equals("flag"))
			{
				updateFlag((FlagPeer)robject);
			}
			if (robject.getType().equals("base"))
			{
				BasePeer base = (BasePeer)robject;
				if (base.getTeam() == 1)
				{
					firstBase = base;
				}
				else
				{
					secondBase = base;
				}
			}
		}
	}
	
	private void updateFlag(FlagPeer flag)
	{
		if (flag.getTeam() == 1)
		{
			firstFlag = flag;
		}
		else
		{
			secondFlag = flag;
		}
	}
	
	public FlagPeer getFlag(String name)
	{
		for (List<String> team : teams)
		{
			if (team.contains(name))
			{
				if (teams.indexOf(team) == 0)
				{
					return firstFlag;
				}
				else if (teams.indexOf(team) == 1)
				{
					return secondFlag; 
				}
			}
		}
		return null;
	}
	
	public BasePeer getBase(String name)
	{
		for (List<String> team : teams)
		{
			if (team.contains(name))
			{
				if (teams.indexOf(team) == 0)
				{
					return firstBase;
				}
				else if (teams.indexOf(team) == 1)
				{
					return firstBase; 
				}
			}
		}
		return null;
	}
	
	@Override
	public IContestantStatistics getEmptyStatistics(){
		return new CaptureTheFlagRobotStatistics();
	}
	
	@Override
	public void startBattle(Battle battle)
	{
		List<RobotPeer> robots = battle.getRobotsAtRandom();
		List<ContestantPeer> contestants = battle.getContestants();
		
		//Assign teams
		teams = new ArrayList<List<String>>();
		teams.add(new ArrayList<String>());
		teams.add(new ArrayList<String>());
		
		robotsInTeams = new ArrayList<RobotPeer>();
		List<CaptureTheFlagTeamPeer> teamPeers = 
			new ArrayList<CaptureTheFlagTeamPeer>();
		teamPeers.add(new CaptureTheFlagTeamPeer("Team 1", null, 0));
		teamPeers.add(new CaptureTheFlagTeamPeer("Team 2", null, 1));
		
		for (int robotIndex = 0; robotIndex < robots.size(); robotIndex++)
		{
			if (robotIndex % 2 == 0)
			{
				teams.get(0).add(robots.get(robotIndex).getName());
				robotsInTeams.add(robots.get(robotIndex));
				robots.get(robotIndex).setTeamPeer(teamPeers.get(0));
				teamPeers.get(0).add(robots.get(robotIndex));
			}
			else
			{
				teams.get(1).add(robots.get(robotIndex).getName());
				robotsInTeams.add(robots.get(robotIndex));
				robots.get(robotIndex).setTeamPeer(teamPeers.get(1));
				teamPeers.get(1).add(robots.get(robotIndex));
			}
		}
		
		//remove non-team contestants, add team contestants
		contestants.clear();
		contestants.add(teamPeers.get(0));
		contestants.add(teamPeers.get(1));
		
		battle.setContestants(contestants);
		
		
	}
	
	@Override
	public IExtensionApi getExtensionApi()
	{
		if (exApi == null)
		{
			exApi = new CaptureTheFlagExtensionApi(this);
		}
		return exApi;
	}
	
	public List<List<String>> getTeams()
	{
		return teams;
	}

	/**
	 * 
	 * @param message
	 * @param sender
	 * @param receiver null indicates a team broadcast
	 */
	public void broadcastMessage(Serializable message, String senderName, String receiver) {
		List<String> team = null;
		RobotPeer sender = null;
		Random random = new Random();
		for (List<String> teamList : teams)
		{
			if (teamList.contains(senderName))
			{
				team = teamList; 
			}
		}
		for (RobotPeer robot : robotsInTeams)
		{
			if (robot.getName().equals(senderName))
			{
				sender = robot;
			}
		}
		if (team == null || sender == null) return;
		if (receiver == null)
		{
			for(RobotPeer robot : robotsInTeams)
			{
				if (team.contains(robot.getName()) && !robot.getName().equals(senderName))
				{
					attemptBroadcast(message, robot, sender, random);
				}
			}
		}
		else
		{
			for(RobotPeer robot : robotsInTeams)
			{
				if (team.contains(robot.getName()) && robot.getName().equals(receiver))
				{
					attemptBroadcast(message, robot, sender, random);
				}
			}
		}
	}
	
	private void attemptBroadcast(Serializable message, RobotPeer robot, RobotPeer sender, Random random)
	{
		boolean fail = false;
		Line2D transmissionLine = new Line2D.Double(robot.getX(), robot.getY(), sender.getX(), sender.getY());
		
		for (RobjectPeer robject : robjects)
		{
			if (transmissionLine.intersects(robject.getBoundaryRect()))
			{
				fail = true;
			}
		}
		
		double rand = random.nextInt(1000) / 1000.0;
		double distance = new Point2D.Double(robot.getX(), robot.getY()).distance(
				new Point2D.Double(sender.getX(), sender.getY()));
		if (rand > messageProbability(distance))
		{
			fail = true;
		}
		
		if (!fail)
		{
			robot.addEvent(new MessageEvent(sender.getName(), message));
		}
	}
	
	private double messageProbability(double distance)
	{
		return 1 / (Math.pow(distance / messageDecayStart, messageDecayExp));
	}

	public BasePeer getOwnBase(String ownName) {
		for (List<String> team : teams)
		{
			if (team.contains(ownName))
			{
				if (teams.indexOf(team) == 0)
				{
					return firstBase;
				}
				else if (teams.indexOf(team) == 1)
				{
					return secondBase;
				}
			}
		}
		return null;
	}

	public FlagPeer getOwnFlag(String ownName) {
		for (List<String> team : teams)
		{
			if (team.contains(ownName))
			{
				if (teams.indexOf(team) == 0)
				{
					return firstFlag;
				}
				else if (teams.indexOf(team) == 1)
				{
					return secondFlag;
				}
			}
		}
		return null;
	}

	public FlagPeer getEnemyFlag(String ownName) {
		for (List<String> team : teams)
		{
			if (team.contains(ownName))
			{
				if (teams.indexOf(team) == 1)
				{
					return firstFlag;
				}
				else if (teams.indexOf(team) == 0)
				{
					return secondFlag;
				}
			}
		}
		return null;
	}

	public BasePeer getEnemyBase(String ownName) {
		for (List<String> team : teams)
		{
			if (team.contains(ownName))
			{
				if (teams.indexOf(team) == 1)
				{
					return firstBase;
				}
				else if (teams.indexOf(team) == 0)
				{
					return secondBase;
				}
			}
		}
		return null;
	}

	public List<RobjectPeer> setupObjects(int battlefieldWidth, int battlefieldHeight) {
		List<RobjectPeer> robjects = new ArrayList<RobjectPeer>();
		
		try 
		{
			File file = new File(FileUtil.getMapsDir(), "CurrentMap.xml");
			
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
					if (type.equals("Box"))
					{
						int x = Integer.parseInt(getTagValue("X", element));
						int y = Integer.parseInt(getTagValue("Y", element));
						int width = Integer.parseInt(getTagValue("Width", element));
						int height = Integer.parseInt(getTagValue("Height", element));
						robjects.add(new BoxPeer(x, y, width, height));				
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
		return robjects;
	}

		private static String getTagValue(String tag, Element element)
		 {
			  NodeList nodeList= element.getElementsByTagName(tag).item(0).getChildNodes();
			  Node nValue = (Node) nodeList.item(0); 
		 
			  return nValue.getNodeValue();
		 
		 }

		@Override
		public double[][] computeInitialPositions(String initialPositions, int battlefieldWidth, int battlefieldHeight) {
			double[][] initialRobotPositions = new double[0][0];
			File file = new File(FileUtil.getMapsDir(), "CurrentMap.xml");
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				
				doc = docBuilder.parse(file);
				doc.getDocumentElement().normalize();
				NodeList nodeList = doc.getElementsByTagName("StartingPoint");
				initialRobotPositions = new double[nodeList.getLength()][3];
	  
				for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) 
				{
					Node node = nodeList.item(nodeIndex);
		 
					if (node.getNodeType() == Node.ELEMENT_NODE) 
					{
			 
						Element element = (Element) node;
			 
						double x = Double.parseDouble(getTagValue("X", element));
						double y = Double.parseDouble(getTagValue("Y", element));
						double heading = Double.parseDouble(getTagValue("Heading", element));
						
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
