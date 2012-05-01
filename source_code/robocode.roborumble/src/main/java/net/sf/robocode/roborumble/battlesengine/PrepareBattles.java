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
 *     - Removed dead code
 *     - Minor cleanup and optimizations
 *     - Properties are now read using PropertiesUtil.getProperties()
 *     - Renamed CheckCompetitorsForSize() into checkCompetitorsForSize()
 *     - Catch of entire Exception has been reduced to catch of IOException when
 *       only this exception is ever thrown
 *     - Added missing close() to buffered readers
 *     Jerome Lavigne
 *     - Added "smart battles" (priority robot pairs) for melee 
 *******************************************************************************/
package net.sf.robocode.roborumble.battlesengine;


import net.sf.robocode.io.Logger;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.io.*;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;


/**
 * PrepareBattles is used for preparing battles.
 * Controlled by properties files.
 *
 * @author Albert Perez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Jerome Lavigne (contributor)
 */
public class PrepareBattles {

	private final String botsrepository;
	private final String participantsfile;
	private final String battlesfile;
	private final int numbattles;
	private final CompetitionsSelector size;
	private final String runonly;
	private final Properties generalratings;
	private final Properties miniratings;
	private final Properties microratings;
	private final Properties nanoratings;
	private final String priority;
	private final int prioritynum;
	private final int meleebots;

	public PrepareBattles(String propertiesfile) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		botsrepository = parameters.getProperty("BOTSREP", "");
		participantsfile = parameters.getProperty("PARTICIPANTSFILE", "");
		battlesfile = parameters.getProperty("INPUT", "");
		numbattles = Integer.parseInt(parameters.getProperty("NUMBATTLES", "100"));
		String sizesfile = parameters.getProperty("CODESIZEFILE", "");

		size = new CompetitionsSelector(sizesfile, botsrepository);
		runonly = parameters.getProperty("RUNONLY", "GENERAL");
		prioritynum = Integer.parseInt(parameters.getProperty("BATTLESPERBOT", "500"));
		meleebots = Integer.parseInt(parameters.getProperty("MELEEBOTS", "10"));
		generalratings = getProperties(parameters.getProperty("RATINGS.GENERAL", ""));
		miniratings = getProperties(parameters.getProperty("RATINGS.MINIBOTS", ""));
		microratings = getProperties(parameters.getProperty("RATINGS.MICROBOTS", ""));
		nanoratings = getProperties(parameters.getProperty("RATINGS.NANOBOTS", ""));
		priority = parameters.getProperty("PRIORITYBATTLESFILE", "");
	}

	public boolean createBattlesList() {
		Vector<String> names = new Vector<String>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(","));
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						if ((runonly.equals("MINI") && size.checkCompetitorsForSize(name, name, 1500))
								|| (runonly.equals("MICRO") && size.checkCompetitorsForSize(name, name, 750))
								|| (runonly.equals("NANO") && size.checkCompetitorsForSize(name, name, 250))
								|| (!runonly.equals("MINI") && !runonly.equals("MICRO") && !runonly.equals("NANO"))) {
							names.add(name);
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}
		// Open battles file
		PrintStream outtxt;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)), false);
		} catch (IOException e) {
			System.out.println("Not able to open battles file " + battlesfile + " ... Aborting");
			System.out.println(e);
			return false;
		}
		// Create the participants file
		Random random = new Random();
		int count = 0;

		while (count < numbattles && names.size() > 1) {
			int bot1 = random.nextInt(names.size());
			int bot2 = random.nextInt(names.size());

			if (bot1 != bot2) {
				outtxt.println(names.get(bot1) + "," + names.get(bot2) + "," + runonly);
				count++;
			}
		}
		outtxt.close();
		return true;
	}

	public boolean createSmartBattlesList() {
		Vector<String> namesall = new Vector<String>();
		Vector<String> namesmini = new Vector<String>();
		Vector<String> namesmicro = new Vector<String>();
		Vector<String> namesnano = new Vector<String>();
		Vector<String> priorityall = new Vector<String>();
		Vector<String> prioritymini = new Vector<String>();
		Vector<String> prioritymicro = new Vector<String>();
		Vector<String> prioritynano = new Vector<String>();

		Vector<String> prioritarybattles = new Vector<String>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(","));
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						namesall.add(name);
						if (size.checkCompetitorsForSize(name, name, 1500)) {
							namesmini.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 750)) {
							namesmicro.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 250)) {
							namesnano.add(name);
						}
						if (robothaspriority(name, generalratings)) {
							priorityall.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 1500) && robothaspriority(name, miniratings)) {
							prioritymini.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 750) && robothaspriority(name, microratings)) {
							prioritymicro.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 250) && robothaspriority(name, nanoratings)) {
							prioritynano.add(name);
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}

		// Read priority battles

		br = null;

		try {
			FileReader fr = new FileReader(priority);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				String[] items = record.split(",");

				if (items.length == 3) {
					// Check that competitors exist
					String jar1 = items[0].replace(' ', '_') + ".jar";
					boolean exists1 = (new File(botsrepository + jar1)).exists();
					String jar2 = items[1].replace(' ', '_') + ".jar";
					boolean exists2 = (new File(botsrepository + jar2)).exists();

					// Add battles to prioritary battles vector
					if (exists1 && exists2) {
						prioritarybattles.add(record);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Prioritary battles file not found ...  ");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}

		// Delete priority battles (avoid duplication)
		File r = new File(priority);

		if (r.exists() && !r.delete()) {
			Logger.logError("Can't delete" + r);
		}

		// Open battles file
		PrintStream outtxt;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)), false);
		} catch (IOException e) {
			System.out.println("Not able to open battles file " + battlesfile + " ... Aborting");
			System.out.println(e);
			return false;
		}
		// Create the participants file
		Random random = new Random();
		int count = 0;

		// Add prioritary battles
		while (count < numbattles && count < prioritarybattles.size()) {
			String battle = prioritarybattles.get(count);

			outtxt.println(battle);
			count++;
		}
		// Add bots with less than 500 battles, or a random battle if all bots have enough battles
		while (count < numbattles && namesall.size() > 1) {
			String[] bots;

			if (priorityall.size() > 0) {
				bots = getbots(priorityall, namesall, random);
			} else if (prioritymini.size() > 0 && namesmini.size() > 1) {
				bots = getbots(prioritymini, namesmini, random);
			} else if (prioritymicro.size() > 0 && namesmicro.size() > 1) {
				bots = getbots(prioritymicro, namesmicro, random);
			} else if (prioritynano.size() > 0 && namesnano.size() > 1) {
				bots = getbots(prioritynano, namesnano, random);
			} else {
				bots = getbots(namesall, namesall, random);
			}
			if (bots != null) {
				outtxt.println(bots[0] + "," + bots[1] + "," + runonly);
				count++;
			}
		}
		outtxt.close();
		return true;
	}

	private String[] getbots(Vector<String> list1, Vector<String> list2, Random rand) {
		int bot1 = rand.nextInt(list1.size());
		int bot2 = rand.nextInt(list2.size());

		while ((list1.get(bot1)).equals(list2.get(bot2))) {
			bot1 = rand.nextInt(list1.size());
			bot2 = rand.nextInt(list2.size());
		}
		String[] bots = new String[2];

		bots[0] = list1.get(bot1);
		bots[1] = list2.get(bot2);
		return bots;
	}

	private boolean robothaspriority(String name, Properties ratings) {
		if (ratings == null) {
			return false;
		}
		String bot = name.replaceAll(" ", "_");
		String values = ratings.getProperty(bot);

		if (values == null) {
			return true;
		}
		String[] value = values.split(",");
		double battles = Double.parseDouble(value[1]);

		return (battles < prioritynum);
	}

	public boolean createMeleeBattlesList() {
		Vector<String> namesall = new Vector<String>();
		Vector<String> namesmini = new Vector<String>();
		Vector<String> namesmicro = new Vector<String>();
		Vector<String> namesnano = new Vector<String>();
		Vector<String> priorityall = new Vector<String>();
		Vector<String> prioritymini = new Vector<String>();
		Vector<String> prioritymicro = new Vector<String>();
		Vector<String> prioritynano = new Vector<String>();
		Vector<String[]> prioritypairs = new Vector<String[]>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				if (record.indexOf(",") != -1) {
					String name = record.substring(0, record.indexOf(","));
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						namesall.add(name);
						if (size.checkCompetitorsForSize(name, name, 1500)) {
							namesmini.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 750)) {
							namesmicro.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 250)) {
							namesnano.add(name);
						}
						if (robothaspriority(name, generalratings)) {
							priorityall.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 1500) && robothaspriority(name, miniratings)) {
							prioritymini.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 750) && robothaspriority(name, microratings)) {
							prioritymicro.add(name);
						}
						if (size.checkCompetitorsForSize(name, name, 250) && robothaspriority(name, nanoratings)) {
							prioritynano.add(name);
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Participants file not found ... Aborting");
			System.out.println(e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {}
			}
		}

		// Read priority battles

		br = null;

		try {
			FileReader fr = new FileReader(priority);

			br = new BufferedReader(fr);
			String record;

			while ((record = br.readLine()) != null) {
				String[] items = record.split(",");

				if (items.length == 3) {
					// Check that competitors exist
					String jar1 = items[0].replace(' ', '_') + ".jar";
					boolean exists1 = (new File(botsrepository + jar1)).exists();
					String jar2 = items[1].replace(' ', '_') + ".jar";
					boolean exists2 = (new File(botsrepository + jar2)).exists();

					// Add battles to prioritary battles vector
					if (exists1 && exists2) {
						prioritypairs.add(items);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Priority battles file not found ...  ");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		// Delete priority battles (avoid duplication)
		File r = new File(priority);

		r.delete();

		// Open battles file
		PrintStream outtxt;

		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)), false);
		} catch (IOException e) {
			System.out.println("Not able to open battles file " + battlesfile + " ... Aborting");
			System.out.println(e);
			return false;
		}

		// Create the participants file
		Random random = new Random();
		int count = 0;

		// Add bots with less than 500 battles, or a random battle if all bots have enough battles
		while (count < numbattles && namesall.size() > meleebots) {
			String[] bots = null;

			if (count < prioritypairs.size()) {
				String[] prioritybots = prioritypairs.get(count);

				bots = getMeleeBots(prioritybots[0], prioritybots[1], namesall, random);
			} else if (priorityall.size() > 0 && namesall.size() >= meleebots) {
				bots = getmeleebots(priorityall, namesall, random);
			} else if (prioritymini.size() > 0 && namesmini.size() >= meleebots) {
				bots = getmeleebots(prioritymini, namesmini, random);
			} else if (prioritymicro.size() > 0 && namesmicro.size() >= meleebots) {
				bots = getmeleebots(prioritymicro, namesmicro, random);
			} else if (prioritynano.size() > 0 && namesnano.size() >= meleebots) {
				bots = getmeleebots(prioritynano, namesnano, random);
			} else if (namesall.size() >= meleebots) {
				bots = getmeleebots(namesall, namesall, random);
			}
			if (bots != null) {
				StringBuilder battle = new StringBuilder(bots[0]);

				for (int i = 1; i < bots.length; i++) {
					battle.append(',').append(bots[i]);
				}
				battle.append(',').append(runonly);

				outtxt.println(battle);
				count++;
			}
		}
		outtxt.close();
		return true;
	}

	private String[] getmeleebots(Vector<String> list1, Vector<String> list2, Random rand) {
		String[] bots = new String[meleebots];

		bots[0] = list1.get(rand.nextInt(list1.size()));
		int count = 1;

		while (count < meleebots) {
			bots[count] = list2.get(rand.nextInt(list2.size()));
			boolean exists = false;

			for (int i = 0; i < count; i++) {
				if (bots[i].equals(bots[count])) {
					exists = true;
				}
			}
			if (!exists) {
				count++;
			}
		}
		return bots;
	}

	private String[] getMeleeBots(String bot1, String bot2, Vector<String> list2, Random rand) {
		String[] bots = new String[meleebots];

		bots[0] = bot1;
		bots[1] = bot2;
		int count = 2;

		while (count < meleebots) {
			bots[count] = list2.get(rand.nextInt(list2.size()));
			boolean exists = false;

			for (int i = 0; i < count; i++) {
				if (bots[i].equals(bots[count])) {
					exists = true;
				}
			}
			if (!exists) {
				count++;
			}
		}
		return bots;
	}
}
