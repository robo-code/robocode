/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;


import static net.sf.robocode.roborumble.util.ExcludesUtil.*;
import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;


/**
 * PrepareBattles is used for preparing battles.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Jerome Lavigne (contributor)
 */
public class PrepareBattles {

	private final String botsrepository;
	private final String participantsfile;
	private final BattlesFile battlesfile;
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
	
	private final static Random RANDOM = new Random(); 

	public PrepareBattles(String propertiesfile) {
		// Read parameters
		Properties parameters = getProperties(propertiesfile);

		botsrepository = parameters.getProperty("BOTSREP", "");
		participantsfile = parameters.getProperty("PARTICIPANTSFILE", "");
		battlesfile = new BattlesFile(parameters.getProperty("INPUT", ""));
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

		// Read and prepare exclude filters
		setExcludes(parameters);
	}

	public boolean createBattlesList() {
		List<String> names = new ArrayList<String>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String participant;

			while ((participant = br.readLine()) != null) {
				if (participant.indexOf(",") != -1) {
					String name = participant.substring(0, participant.indexOf(","));

					if (isExcluded(name)) {
						continue; // ignore excluded participant
					}
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						if ((runonly.equals("MINI") && size.checkCompetitorForSize(name, 1500))
								|| (runonly.equals("MICRO") && size.checkCompetitorForSize(name, 750))
								|| (runonly.equals("NANO") && size.checkCompetitorForSize(name, 250))
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

		if (!battlesfile.openWrite()) {
			return false;
		}
		// Create the participants file
		Random random = new Random();
		int count = 0;

		while (count < numbattles && names.size() > 1) {
			int bot1 = random.nextInt(names.size());
			int bot2 = random.nextInt(names.size());

			if (bot1 != bot2) {
				battlesfile.writeBattle(new RumbleBattle(new String[]{names.get(bot1), names.get(bot2)}, runonly));
				count++;
			}
		}
		battlesfile.closeWrite();
		return true;
	}

	public boolean createSmartBattlesList() {
		List<String> namesAll = new ArrayList<String>();
		List<String> namesMini = new ArrayList<String>();
		List<String> namesMicro = new ArrayList<String>();
		List<String> namesNano = new ArrayList<String>();
		List<String> namesNoRanking = new ArrayList<String>();
		List<String> priorityAll = new ArrayList<String>();
		List<String> priorityMini = new ArrayList<String>();
		List<String> priorityMicro = new ArrayList<String>();
		List<String> priorityNano = new ArrayList<String>();

		List<String> priorityBattles = new ArrayList<String>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String participant;

			while ((participant = br.readLine()) != null) {
				if (participant.indexOf(",") != -1) {
					String name = participant.substring(0, participant.indexOf(","));

					if (isExcluded(name)) {
						continue; // ignore excluded participant
					}
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						namesAll.add(name);
						if (size.checkCompetitorForSize(name, 1500)) {
							namesMini.add(name);
						}
						if (size.checkCompetitorForSize(name, 750)) {
							namesMicro.add(name);
						}
						if (size.checkCompetitorForSize(name, 250)) {
							namesNano.add(name);
						}
						if (robotHasPriority(name, generalratings)) {
							priorityAll.add(name);
						}
						if (size.checkCompetitorForSize(name, 1500) && robotHasPriority(name, miniratings)) {
							priorityMini.add(name);
						}
						if (size.checkCompetitorForSize(name, 750) && robotHasPriority(name, microratings)) {
							priorityMicro.add(name);
						}
						if (size.checkCompetitorForSize(name, 250) && robotHasPriority(name, nanoratings)) {
							priorityNano.add(name);
						}
						if (!isRobotInRatings(name)) {
							namesNoRanking.add(name);
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
					boolean exists1 = (new File(botsrepository + jar1)).exists() && namesAll.contains(items[0]);
					String jar2 = items[1].replace(' ', '_') + ".jar";
					boolean exists2 = (new File(botsrepository + jar2)).exists() && namesAll.contains(items[1]);

					// Add battles to priority battles list
					if (exists1 && exists2 && !priorityBattles.contains(record)) {
						priorityBattles.add(record);
					} else {
						System.out.println("Ignoring: " + record);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Priority battles file not found ...  ");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignore) {}
			}
		}

		// Delete priority battles (avoid duplication)
		File r = new File(priority);

		if (r.exists() && !r.delete()) {
			System.out.println("Cannot delete: " + priority);
		}

		if (!battlesfile.openWrite()) {
			return false;
		}
		// Create the participants file
		int count = 0;

		// Add priority battles
		while (count < numbattles && count < priorityBattles.size()) {
			String battle = priorityBattles.get(count);

			String[] items = battle.split(",");

			battlesfile.writeBattle(new RumbleBattle(new String[]{items[0], items[1]}, items[2]));

			count++;
		}
		// Add bots with less than 500 battles, or a random battle if all bots have enough battles
		if (namesAll.size() > 1) {
			while (count < numbattles) {
				String[] bots;
				if (namesNoRanking.size() > 0) {
					// Bug [3547611] - New bots not given priority 
					List<String> listWithSingleItem = new ArrayList<String>();
					String bot = namesNoRanking.get(0);
					listWithSingleItem.add(bot);
					bots = getRandomBots(listWithSingleItem, namesAll);
					namesNoRanking.remove(0);
				} else if (priorityAll.size() > 0) {
					bots = getRandomBots(priorityAll, namesAll);
				} else if (priorityMini.size() > 0 && namesMini.size() > 1) {
					bots = getRandomBots(priorityMini, namesMini);
				} else if (priorityMicro.size() > 0 && namesMicro.size() > 1) {
					bots = getRandomBots(priorityMicro, namesMicro);
				} else if (priorityNano.size() > 0 && namesNano.size() > 1) {
					bots = getRandomBots(priorityNano, namesNano);
				} else {
					bots = getRandomBots(namesAll, namesAll);
				}
				if (bots != null) {
					battlesfile.writeBattle(new RumbleBattle(new String[]{bots[0], bots[1]}, runonly));

					count++;
				}
			}
		}
		battlesfile.closeWrite();
		return true;
	}

	private String[] getRandomBots(List<String> list1, List<String> list2) {
		int bot1 = RANDOM.nextInt(list1.size());
		int bot2 = RANDOM.nextInt(list2.size());

		while ((list1.get(bot1)).equals(list2.get(bot2))) {
			bot1 = RANDOM.nextInt(list1.size());
			bot2 = RANDOM.nextInt(list2.size());
		}
		String[] bots = new String[2];

		bots[0] = list1.get(bot1);
		bots[1] = list2.get(bot2);
		return bots;
	}

	private boolean robotHasPriority(String name, Properties ratings) {
		if (name == null || ratings == null) {
			return false;
		}
		String bot = name.replaceAll(" ", "_");
		String values = ratings.getProperty(bot);

		if (values == null) {
			return false; // must be false (Bug 3474173)
		}
		String[] value = values.split(",");
		double battles = Double.parseDouble(value[1]);

		return (battles < prioritynum);
	}

	private boolean isRobotInRatings(String name) {
		if (name == null || name.trim().length() == 0) {
			return false;
		}
		String bot = name.replaceAll(" ", "_");
		
		Properties[] ratingLists = new Properties[] { generalratings, miniratings, microratings, nanoratings };
		
		for (Properties ratings : ratingLists) {
			if (ratings.getProperty(bot) != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean createMeleeBattlesList() {
		List<String> namesAll = new ArrayList<String>();
		List<String> namesMini = new ArrayList<String>();
		List<String> namesMicro = new ArrayList<String>();
		List<String> namesNano = new ArrayList<String>();
		List<String> namesNoRanking = new ArrayList<String>();
		List<String> priorityAll = new ArrayList<String>();
		List<String> priorityMini = new ArrayList<String>();
		List<String> priorityMicro = new ArrayList<String>();
		List<String> priorityNano = new ArrayList<String>();

		List<String[]> priorityPairs = new ArrayList<String[]>();

		// Read participants

		BufferedReader br = null;

		try {
			FileReader fr = new FileReader(participantsfile);

			br = new BufferedReader(fr);
			String participant;

			while ((participant = br.readLine()) != null) {
				if (participant.indexOf(",") != -1) {
					String name = participant.substring(0, participant.indexOf(","));

					if (isExcluded(name)) {
						continue; // ignore excluded participant
					}
					String jar = name.replace(' ', '_') + ".jar";
					boolean exists = (new File(botsrepository + jar)).exists();

					if (exists) {
						namesAll.add(name);
						if (size.checkCompetitorForSize(name, 1500)) {
							namesMini.add(name);
						}
						if (size.checkCompetitorForSize(name, 750)) {
							namesMicro.add(name);
						}
						if (size.checkCompetitorForSize(name, 250)) {
							namesNano.add(name);
						}
						if (robotHasPriority(name, generalratings)) {
							priorityAll.add(name);
						}
						if (size.checkCompetitorForSize(name, 1500) && robotHasPriority(name, miniratings)) {
							priorityMini.add(name);
						}
						if (size.checkCompetitorForSize(name, 750) && robotHasPriority(name, microratings)) {
							priorityMicro.add(name);
						}
						if (size.checkCompetitorForSize(name, 250) && robotHasPriority(name, nanoratings)) {
							priorityNano.add(name);
						}
						if (!isRobotInRatings(name)) {
							namesNoRanking.add(name);
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
					boolean exists1 = (new File(botsrepository + jar1)).exists() && namesAll.contains(items[0]);
					String jar2 = items[1].replace(' ', '_') + ".jar";
					boolean exists2 = (new File(botsrepository + jar2)).exists() && namesAll.contains(items[1]);

					// Add battles to priority battles vector
					if (exists1 && exists2 && !priorityPairs.contains(items)) {
						priorityPairs.add(items);
					} else {
						System.out.println("Ignoring: " + record);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Priority battles file not found ...  ");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignore) {}
			}
		}

		// Delete priority battles (avoid duplication)
		File r = new File(priority);

		if (r.exists() && !r.delete()) {
			System.out.println("Cannot delete: " + priority);
		}

		if (!battlesfile.openWrite()) {
			return false;
		}
		// Create the participants file
		int count = 0;

		// Add bots with less than 500 battles, or a random battle if all bots have enough battles
		if (namesAll.size() > meleebots) {
			while (count < numbattles) {
				String[] bots = null;
				boolean prioritized = false;
				if (namesNoRanking.size() > 0) {
					// Bug [3547611] - New bots not given priority 
					List<String> listWithSingleItem = new ArrayList<String>();
					String bot = namesNoRanking.get(0);
					listWithSingleItem.add(bot);
					bots = getRandomMeleeBots(listWithSingleItem, namesAll);
					namesNoRanking.remove(0);
				} else if (count < priorityPairs.size()) {
					String[] prioritybots = priorityPairs.get(count);
					bots = getRandomMeleeBots(prioritybots[0], prioritybots[1], namesAll);
					prioritized = true;
				} else if (priorityAll.size() > 0 && namesAll.size() >= meleebots) {
					bots = getRandomMeleeBots(priorityAll, namesAll);
					prioritized = true;
				} else if (priorityMini.size() > 0 && namesMini.size() >= meleebots) {
					bots = getRandomMeleeBots(priorityMini, namesMini);
					prioritized = true;
				} else if (priorityMicro.size() > 0 && namesMicro.size() >= meleebots) {
					bots = getRandomMeleeBots(priorityMicro, namesMicro);
					prioritized = true;
				} else if (priorityNano.size() > 0 && namesNano.size() >= meleebots) {
					bots = getRandomMeleeBots(priorityNano, namesNano);
					prioritized = true;
				} else if (namesAll.size() >= meleebots) {
					bots = getRandomMeleeBots(namesAll, namesAll);
				}
				if (bots != null) {
					battlesfile.writeBattle(new RumbleBattle(bots, runonly, prioritized));

					count++;
				}
			}
		}
		battlesfile.closeWrite();
		return true;
	}

	private String[] getRandomMeleeBots(List<String> list1, List<String> list2) {
		String[] bots = new String[meleebots];

		bots[0] = list1.get(RANDOM.nextInt(list1.size()));
		int count = 1;

		while (count < meleebots) {
			bots[count] = list2.get(RANDOM.nextInt(list2.size()));
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

	private String[] getRandomMeleeBots(String bot1, String bot2, List<String> list2) {
		String[] bots = new String[meleebots];

		bots[0] = bot1;
		bots[1] = bot2;
		int count = 2;

		while (count < meleebots) {
			bots[count] = list2.get(RANDOM.nextInt(list2.size()));
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
