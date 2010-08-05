/*******************************************************************************
 * Copyright (c) 2003, 2010 Albert P�rez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Albert P�rez
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


import net.sf.robocode.io.Logger;
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
 * @author Albert P�rez (original)
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
	private static RobotResults[] lastResults;
	private static IRobocodeEngine engine;

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
		if (engine == null) {
			engine = new RobocodeEngine();
			engine.addBattleListener(new BattleObserver());
		}
	}

	public String getVersion() {
		return (engine != null) ? engine.getVersion() : null;
	}
	
	public void runBattlesImpl(boolean melee) {
		// Initialize objects
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

			final RobotSpecification[] robotsList = engine.getLocalRepository(enemies);

			if (robotsList.length > 1) {
				final String team0 = robotsList[0].getTeamId();
				final String teamLast = robotsList[robotsList.length - 1].getTeamId();

				if (team0 == null || !team0.equals(teamLast)) {
					final BattleSpecification specification = new BattleSpecification(battle.getNumRounds(),
							battle.getBattlefield(), robotsList);

					lastResults = null;
					engine.runBattle(specification, true);
					if (lastResults != null && lastResults.length > 1) {
						dumpResults(outtxt, lastResults, param[param.length - 1], melee);
					}
				}
			} else {
				System.err.println("Skipping battle because can't load robots: " + enemies);
			}
			index++;
		}

		// close
		outtxt.close();
	}

	private String getEnemies(boolean melee, String[] param) {
		String enemies;

		if (melee) {
			StringBuilder eb = new StringBuilder();

			for (int i = 0; i < param.length - 1; i++) {
				if (i > 0) {
					eb.append(',');
				}
				eb.append(param[i]);
			}
			enemies = eb.toString();
		} else {
			enemies = param[0] + "," + param[1];
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
		final String BOT_INDEX_PATTERN = "\\[.*\\]";

		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < results.length; j++) {
				if (i < j) {
					RobotSpecification bot1 = results[i].getRobot();
					RobotSpecification bot2 = results[j].getRobot();

					String name1 = bot1.getTeamId() != null
							? bot1.getTeamId().replaceAll(BOT_INDEX_PATTERN, "")
							: bot1.getNameAndVersion();
					String name2 = bot2.getTeamId() != null
							? bot2.getTeamId().replaceAll(BOT_INDEX_PATTERN, "")
							: bot2.getNameAndVersion();
					int points1 = results[i].getScore();
					int points2 = results[j].getScore();
					int bullets1 = results[i].getBulletDamage();
					int bullets2 = results[j].getBulletDamage();
					int survival1 = results[i].getFirsts();
					int survival2 = results[j].getFirsts();

					outtxt.println(
							game + "," + numrounds + "," + fieldlen + "x" + fieldhei + "," + user + ","
							+ System.currentTimeMillis() + "," + last);
					outtxt.println(name1 + "," + points1 + "," + bullets1 + "," + survival1);
					outtxt.println(name2 + "," + points2 + "," + bullets2 + "," + survival2);
				}
			}
		}
		if (melee) {
			System.out.println(
					"RESULT = " + results[0].getRobot().getNameAndVersion() + " wins, "
					+ results[1].getRobot().getNameAndVersion() + " is second.");
		} else {
			RobotSpecification winnerBot = results[0].getRobot();
			String winnerBotName = winnerBot.getTeamId() != null
					? winnerBot.getTeamId().replaceAll(BOT_INDEX_PATTERN, "")
					: winnerBot.getNameAndVersion(); 

			System.out.println(
					"RESULT = " + winnerBotName + " wins " + results[0].getScore() + " to " + results[1].getScore());
		}
	}

	class BattleObserver extends BattleAdaptor {
		@Override
		public void onBattleError(final BattleErrorEvent event) {
			Logger.realErr.println(event.getError());
		}

		@Override
		public void onBattleCompleted(final BattleCompletedEvent event) {
			lastResults = RobotResults.convertResults(event.getSortedResults());
		}
	}
}
