/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Minor optimizations
 *     - Removed dead code and unused imports
 *     - Replaced the RobocodeEngineAtHome will the RobocodeEngine, and added
 *       runBattle() to run a single battle with RobocodeEngine
 *     - The results are now read from the AtHomeListener instead of the
 *       RobocodeEngineAtHome
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Added missing close() to buffered readers
 *******************************************************************************/
package roborumble.battlesengine;


import robocode.control.*;

import java.util.*;
import java.io.*;

import static roborumble.util.PropertiesUtil.getProperties;


/**
 * The BattlesRunner is running battles.
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files.
 * 
 * @author Albert Perez (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattlesRunner {
	private String inputfile;
	private int numrounds;
	private int fieldlen;
	private int fieldhei;
	private String outfile;
	private String user;
	private String game;

	public BattlesRunner(String propertiesfile) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		inputfile = parameters.getProperty("INPUT", "");
		numrounds = Integer.parseInt(parameters.getProperty("ROUNDS", "10"));
		fieldlen = Integer.parseInt(parameters.getProperty("FIELDL", "800"));
		fieldhei = Integer.parseInt(parameters.getProperty("FIELDH", "600"));
		outfile = parameters.getProperty("OUTPUT", "");
		user = parameters.getProperty("USER", "");
		game = propertiesfile;
		while (game.indexOf("/") != -1) {
			game = game.substring(game.indexOf("/") + 1);
		}
		game = game.substring(0, game.indexOf("."));
	}

	public boolean runBattles() {
		// Initialize objects
		AtHomeListener listener = new AtHomeListener();
		RobocodeEngine engine = new RobocodeEngine(listener);
		BattlefieldSpecification field = new BattlefieldSpecification(fieldlen, fieldhei);
		BattleSpecification battle = new BattleSpecification(numrounds, field, (new RobotSpecification[2]));

		// Read input file
		ArrayList<String> robots = new ArrayList<String>();
		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(inputfile);
			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				robots.add(record);
			}
		} catch (IOException e) {
			System.out.println("Battles input file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// open output file
		PrintStream outtxt = null;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile, true)), true);
		} catch (IOException e) {
			System.out.println("Not able to open output file ... Aborting");
			System.out.println(e);
			return false;
		}

		// run battle
		int index = 0;

		while (index < robots.size()) {
			String[] param = (robots.get(index)).split(",");

			String enemies = param[0] + "," + param[1];

			System.out.println("Fighting battle " + (index) + " ... " + enemies);
			runBattle(engine, battle, enemies);
			// get results
			RobotResults[] results = listener.getResults();
			String First = results[0].getRobot().getNameAndVersion();

			int PointsFirst = results[0].getScore();
			int BulletDFirst = results[0].getBulletDamage();
			int SurvivalFirst = results[0].getFirsts();
			String Second = results[1].getRobot().getNameAndVersion();
			int PointsSecond = results[1].getScore();
			int BulletDSecond = results[1].getBulletDamage();
			int SurvivalSecond = results[1].getFirsts();

			outtxt.println(
					game + "," + numrounds + "," + fieldlen + "x" + fieldhei + "," + user + "," + System.currentTimeMillis()
					+ "," + param[2]);
			outtxt.println(First + "," + PointsFirst + "," + BulletDFirst + "," + SurvivalFirst);
			outtxt.println(Second + "," + PointsSecond + "," + BulletDSecond + "," + SurvivalSecond);
			index++;
			System.out.println("RESULT = " + First + " wins " + PointsFirst + " to " + PointsSecond);
		}

		// close
		outtxt.close();
		engine.close();

		return true;
	}

	public boolean runMeleeBattles() {
		// Initialize objects
		AtHomeListener listener = new AtHomeListener();
		RobocodeEngine engine = new RobocodeEngine(listener);
		BattlefieldSpecification field = new BattlefieldSpecification(fieldlen, fieldhei);
		BattleSpecification battle = new BattleSpecification(numrounds, field, (new RobotSpecification[2]));

		// Read input file
		ArrayList<String> robots = new ArrayList<String>();
		BufferedReader br = null;
		
		try {
			FileReader fr = new FileReader(inputfile);
			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				robots.add(record);
			}
		} catch (IOException e) {
			System.out.println("Battles input file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// open output file
		PrintStream outtxt = null;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile, true)), true);
		} catch (IOException e) {
			System.out.println("Not able to open output file ... Aborting");
			System.out.println(e);
			return false;
		}

		// run battle
		int index = 0;

		while (index < robots.size()) {
			String[] param = (robots.get(index)).split(",");

			StringBuilder enemies = new StringBuilder();

			for (int i = 0; i < param.length - 1; i++) {
				if (i > 0) {
					enemies.append(',');
				}
				enemies.append(param[i]);
			}
			System.out.println("Fighting battle " + (index) + " ... " + enemies);
			runBattle(engine, battle, enemies.toString());

			// get results
			RobotResults[] results = listener.getResults();
			String[] Bot = new String[results.length];
			int[] Points = new int[results.length];
			int[] BulletD = new int[results.length];
			int[] Survival = new int[results.length];

			for (int i = 0; i < results.length; i++) {
				Bot[i] = results[i].getRobot().getNameAndVersion();
				Points[i] = results[i].getScore();
				BulletD[i] = results[i].getBulletDamage();
				Survival[i] = results[i].getFirsts();
			}

			for (int i = 0; i < param.length - 1; i++) {
				for (int j = 0; j < param.length - 1; j++) {
					if (i < j) {
						outtxt.println(
								game + "," + numrounds + "," + fieldlen + "x" + fieldhei + "," + user + ","
								+ System.currentTimeMillis() + "," + param[param.length - 1]);
						outtxt.println(Bot[i] + "," + Points[i] + "," + BulletD[i] + "," + Survival[i]);
						outtxt.println(Bot[j] + "," + Points[j] + "," + BulletD[j] + "," + Survival[j]);
					}
				}
			}
			index++;
			System.out.println("RESULT = " + Bot[0] + " wins, " + Bot[1] + " is second.");
		}

		// close
		outtxt.close();
		engine.close();

		return true;
	}

	private void runBattle(RobocodeEngine engine, BattleSpecification battle, String selectedRobotList) {
		RobotSpecification[] repository = engine.getLocalRepository();

		HashMap<String, RobotSpecification> robotSpecMap = new HashMap<String, RobotSpecification>();

		for (RobotSpecification spec : repository) {
			robotSpecMap.put(spec.getNameAndVersion(), spec);
		}

		String[] selectedRobots = selectedRobotList.split(",");

		List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();

		RobotSpecification spec;
		
		for (String robot : selectedRobots) {
			spec = robotSpecMap.get(robot);
			if (spec != null) {
				selectedRobotSpecs.add(spec);			
			}
		}
		engine.runBattle(
				new BattleSpecification(battle.getNumRounds(), battle.getBattlefield(),
				selectedRobotSpecs.toArray(new RobotSpecification[1])));
	}
}
