/*******************************************************************************
 * Copyright (c) 2003, 2008 Albert Pérez and RoboRumble contributors
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
 *     Joachim Hofer
 *     - Fixing problem with wrong results in RoboRumble due to wrong ordering
 *******************************************************************************/
package net.sf.robocode.roborumble.battlesengine;


import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;
import robocode.control.*;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;

import java.io.*;
import java.util.*;


/**
 * The BattlesRunner is running battles.
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files.
 *
 * @author Albert Perez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Joachim Hofer (contributor)
 */
public class BattlesRunner {
	private final String inputfile;
	private final int numrounds;
	private final int fieldlen;
	private final int fieldhei;
	private final String outfile;
	private final String user;
	private String game;
	private final Map<String, RobotSpecification> robotSpecMap = new HashMap<String, RobotSpecification>(500);
	private RobotResults[] lastResults;
	private BattleObserver battleObserver;
	public static String version; 

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

		initialize();
	}

	private void initialize() {
		IRobocodeEngine engine = new RobocodeEngine();
		RobotSpecification[] repository = engine.getLocalRepository();

		for (RobotSpecification spec : repository) {
			robotSpecMap.put(spec.getNameAndVersion(), spec);
		}

		battleObserver = new BattleObserver();
	}

	public void runBattlesImpl(boolean melee) {
		// Initialize objects
		IRobocodeEngine engine = new RobocodeEngine();

		engine.addBattleListener(battleObserver);
		BattlefieldSpecification field = new BattlefieldSpecification(fieldlen, fieldhei);
		BattleSpecification battle = new BattleSpecification(numrounds, field, (new RobotSpecification[2]));

		// Read input file
		ArrayList<String> robots = new ArrayList<String>();
		BufferedReader br = null;

		if (readRobots(robots, br)) {
			return;
		}

		// open output file
		PrintStream outtxt = getRedirectedOutput();

		if (outtxt == null) {
			return;
		}

		// run battle
		int index = 0;

		while (index < robots.size()) {
			String[] param = (robots.get(index)).split(",");

			String enemies = getEnemies(melee, param);

			System.out.println("Fighting battle " + (index) + " ... " + enemies);

			String[] selectedRobots = enemies.split(",");
			List<RobotSpecification> selectedRobotSpecs = new ArrayList<RobotSpecification>();

			for (String robot : selectedRobots) {
				RobotSpecification spec = robotSpecMap.get(robot);

				if (spec != null) {
					selectedRobotSpecs.add(spec);
				}
			}
			final RobotSpecification[] robotsList = selectedRobotSpecs.toArray(new RobotSpecification[1]);

			if (robotsList.length >= 2) {
				final BattleSpecification specification = new BattleSpecification(battle.getNumRounds(),
						battle.getBattlefield(), robotsList);

				lastResults = null;
				engine.runBattle(specification, true);
				if (lastResults != null) {
					dumpResults(outtxt, lastResults, param[param.length - 1], melee);
				}
			} else {
				System.err.println("Skipping battle because can't load robots");
			}
			index++;
		}

		engine.removeBattleListener(battleObserver);
		version = engine.getVersion();

		// close
		outtxt.close();
		engine.close();
	}

	private String getEnemies(boolean melee, String[] param) {
		String enemies;

		if (melee) {
			enemies = param[0] + "," + param[1];
		} else {
			StringBuilder eb = new StringBuilder();

			for (int i = 0; i < param.length - 1; i++) {
				if (i > 0) {
					eb.append(',');
				}
				eb.append(param[i]);
			}
			enemies = eb.toString();
		}
		return enemies;
	}

	private PrintStream getRedirectedOutput() {
		try {
			return new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile, true)), true);
		} catch (IOException e) {
			System.out.println("Not able to open output file ... Aborting");
			System.out.println(e);
			return null;
		}
	}

	private boolean readRobots(ArrayList<String> robots, BufferedReader br) {
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
			return true;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {// just ignore
				}
			}
		}
		return false;
	}

	private void dumpResults(PrintStream outtxt, RobotResults[] results, String last, boolean melee) {
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < results.length; j++) {
				if (i < j) {
					final String botOne = results[i].getRobot().getNameAndVersion();
					final String botTwo = results[j].getRobot().getNameAndVersion();
					final int pointsOne = results[i].getScore();
					final int pointsTwo = results[j].getScore();
					final int bulletsOne = results[i].getBulletDamage();
					final int bulletsTwo = results[j].getBulletDamage();
					final int survivalOne = results[i].getSurvival();
					final int survivalTwo = results[j].getSurvival();

					outtxt.println(
							game + "," + numrounds + "," + fieldlen + "x" + fieldhei + "," + user + ","
							+ System.currentTimeMillis() + "," + last);
					outtxt.println(botOne + "," + pointsOne + "," + bulletsOne + "," + survivalOne);
					outtxt.println(botTwo + "," + pointsTwo + "," + bulletsTwo + "," + survivalTwo);
				}
			}
		}
		if (melee) {
			System.out.println(
					"RESULT = " + results[0].getRobot().getNameAndVersion() + " wins, "
					+ results[1].getRobot().getNameAndVersion() + " is second.");
		} else {
			System.out.println(
					"RESULT = " + results[0].getRobot().getNameAndVersion() + " wins " + results[0].getScore() + " to "
					+ results[1].getScore());
		}
	}

	class BattleObserver extends BattleAdaptor {
		// @Override
		// public void onBattleMessage(final BattleMessageEvent event) {
		// SecurePrintStream.realOut.println(event.getMessage());
		// }

		@Override
		public void onBattleError(final BattleErrorEvent event) {
			System.err.println(event.getError());
		}

		@Override
		public void onBattleCompleted(final BattleCompletedEvent event) {
			lastResults = RobotResults.convertResults(event.getSortedResults());
		}
	}
}
