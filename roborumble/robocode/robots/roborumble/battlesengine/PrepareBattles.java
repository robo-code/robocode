package roborumble.battlesengine;

import java.net.*;
//import java.util.jar.*;
import java.util.*;
import java.util.Vector;
import java.util.Random;
import java.io.*;

/**
 * BotsDownload - a class by Albert Perez
 * Manages the download operations (participants and JAR files)
 * Controlled by properties files
 */

public class PrepareBattles {

	private String botsrepository;
	private String participantsfile;
	private String battlesfile;
	private int numbattles;
	private String sizesfile;
	private CompetitionsSelector size;
	private String runonly;
	private Properties generalratings;
	private Properties miniratings;
	private Properties microratings;
	private Properties nanoratings;
	private String priority;
	private int prioritynum;
	private int meleebots;

	
	public PrepareBattles(String propertiesfile) {
		//Read parameters
		Properties parameters = null;
		try { parameters = new Properties(); parameters.load(new FileInputStream(propertiesfile)); } 
		catch (Exception e) { System.out.println("Parameters File not found !!!"); }
		botsrepository = parameters.getProperty("BOTSREP","");
		participantsfile = parameters.getProperty("PARTICIPANTSFILE","");
		battlesfile = parameters.getProperty("INPUT","");
		numbattles = Integer.parseInt(parameters.getProperty("NUMBATTLES","100"));
		sizesfile = parameters.getProperty("CODESIZEFILE","");
		size = new CompetitionsSelector(sizesfile,botsrepository);
		runonly = parameters.getProperty("RUNONLY","GENERAL");
		prioritynum = Integer.parseInt(parameters.getProperty("BATTLESPERBOT","500"));
		meleebots = Integer.parseInt(parameters.getProperty("MELEEBOTS","10"));
		generalratings = new Properties();
		miniratings =new Properties();
		microratings = new Properties();
		nanoratings = new Properties();
		
		try { generalratings.load(new FileInputStream(parameters.getProperty("RATINGS.GENERAL",""))); } catch (Exception e) { generalratings = null; }
		try { miniratings.load(new FileInputStream(parameters.getProperty("RATINGS.MINIBOTS",""))); } catch (Exception e) { miniratings = null; }
		try { microratings.load(new FileInputStream(parameters.getProperty("RATINGS.MICROBOTS",""))); } catch (Exception e) { microratings = null; }
		try { nanoratings.load(new FileInputStream(parameters.getProperty("RATINGS.NANOBOTS",""))); } catch (Exception e) { nanoratings = null; }

		priority = parameters.getProperty("PRIORITYBATTLESFILE","");

	}

	public boolean createBattlesList() {
		Vector jars = new Vector();
		Vector names = new Vector();
		//Read participants
		try {
			FileReader fr = new FileReader(participantsfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { 
				if (record.indexOf(",") != -1) {
					String id = record.substring(record.indexOf(",")+1);
					String name = record.substring(0,record.indexOf(","));
					String jar =name.replace(' ','_')+".jar";
					boolean exists = (new File(botsrepository+jar)).exists();
					if (exists) { 
						if ( (runonly.equals("MINI") && size.CheckCompetitorsForSize(name,name,1500)) ||
					     	(runonly.equals("MICRO") && size.CheckCompetitorsForSize(name,name,750)) ||
					     	(runonly.equals("NANO") && size.CheckCompetitorsForSize(name,name,250)) ||
					     	(!runonly.equals("MINI") && !runonly.equals("MICRO") && !runonly.equals("NANO")) ) {
							jars.add(jar);
							names.add(name);
						}	
					}
				}
			}
			br.close();
		} catch (Exception e) { 
			System.out.println("Participants file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		//Open battles file
		PrintStream outtxt = null;
		try { outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)),false); } 
		catch (IOException e) { 
			System.out.println("Not able to open battles file "+battlesfile+" ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		//Create the participants file
		Random random = new Random();
		int count = 0;
		while (count<numbattles && names.size()>1) {
			int bot1 = random.nextInt(names.size());
			int bot2 = random.nextInt(names.size());
			if (bot1 != bot2) {
				//outtxt.println((String)names.get(bot1));
				//outtxt.println((String)names.get(bot2));
				outtxt.println((String)names.get(bot1)+","+(String)names.get(bot2)+","+runonly);
				count++;
			} 	
		}
		outtxt.close();
		return true;
	}

	public boolean createSmartBattlesList() {
		Vector namesall = new Vector();
		Vector namesmini = new Vector();
		Vector namesmicro = new Vector();
		Vector namesnano = new Vector();
		Vector priorityall = new Vector();
		Vector prioritymini = new Vector();
		Vector prioritymicro = new Vector();
		Vector prioritynano = new Vector();

		Vector prioritarybattles = new Vector();

		//Read participants
		try {
			FileReader fr = new FileReader(participantsfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { 
				if (record.indexOf(",") != -1) {
					String id = record.substring(record.indexOf(",")+1);
					String name = record.substring(0,record.indexOf(","));
					String jar =name.replace(' ','_')+".jar";
					boolean exists = (new File(botsrepository+jar)).exists();
					if (exists) { 
						namesall.add(name);
						if (size.CheckCompetitorsForSize(name,name,1500))  namesmini.add(name);
						if (size.CheckCompetitorsForSize(name,name,750))  namesmicro.add(name);
						if (size.CheckCompetitorsForSize(name,name,250))  namesnano.add(name);
						if (robothaspriority(name,generalratings))  priorityall.add(name);
						if (size.CheckCompetitorsForSize(name,name,1500) && robothaspriority(name,miniratings))  prioritymini.add(name);
						if (size.CheckCompetitorsForSize(name,name,750) && robothaspriority(name,microratings))  prioritymicro.add(name);
						if (size.CheckCompetitorsForSize(name,name,250) && robothaspriority(name,nanoratings))  prioritynano.add(name);
					}
				}
			}
			br.close();
		} catch (Exception e) { 
			System.out.println("Participants file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		//Read priority battles
		try {
			FileReader fr = new FileReader(priority); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { 
				String[] items = record.split(",");
				if (items.length == 3) {
					//Check that competitors exist
					String jar1 = items[0].replace(' ','_')+".jar";
					boolean exists1 = (new File(botsrepository+jar1)).exists();
					String jar2 = items[1].replace(' ','_')+".jar";
					boolean exists2 = (new File(botsrepository+jar2)).exists();
					//Add battles to prioritary battles vector
					if (exists1 && exists2) prioritarybattles.add(record);
				}
			}
			br.close();
		} catch (Exception e) { 
			System.out.println("Prioritary battles file not found ...  "); 
			//System.out.println(e);
		}
		//Delete priority battles (avoid duplication)
		File r = new File(priority);
		boolean b = r.delete();
		
		//Open battles file
		PrintStream outtxt = null;
		try { outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)),false); } 
		catch (IOException e) { 
			System.out.println("Not able to open battles file "+battlesfile+" ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		//Create the participants file
		Random random = new Random();
		int count = 0; 
		//Add prioritary battles
		while (count<numbattles && count <prioritarybattles.size()) {
			String battle = (String) prioritarybattles.get(count);
			outtxt.println(battle);
			count++;
		}
		//Add bots with less than 500 battles, or a random battle if all bots have enough battles
		while (count<numbattles && namesall.size()>1) {
			String[] bots = null;
			if (priorityall.size() > 0) bots = getbots(priorityall,namesall,random);
			else if (prioritymini.size()>0 && namesmini.size()>1) bots = getbots(prioritymini,namesmini, random);
			else if (prioritymicro.size()>0 && namesmicro.size()>1) bots = getbots(prioritymicro, namesmicro, random);
			else if (prioritynano.size()>0 && namesnano.size()>1) bots = getbots(prioritynano, namesnano, random);
			else bots = getbots(namesall, namesall, random);
			if (bots != null) {
				outtxt.println(bots[0]+","+bots[1]+","+runonly);
				count++;
			} 	
		}
		outtxt.close();
		return true;
	}

	private String[] getbots(Vector list1, Vector list2, Random rand) {
		int bot1 = rand.nextInt(list1.size()); int bot2 = rand.nextInt(list2.size());
		while (  ((String) list1.get(bot1)).equals((String) list2.get(bot2))   ) {
			bot1 = rand.nextInt(list1.size());
			bot2 = rand.nextInt(list2.size());
		}
		String[] bots = new String[2];
		bots[0] = (String) list1.get(bot1);
		bots[1] = (String) list2.get(bot2);
		return bots;
	}

	private boolean robothaspriority(String name,Properties ratings) {
		if (ratings == null) return false;
		String bot = name.replaceAll(" ","_");
		String values = ratings.getProperty(bot);
		if (values == null) return true;
		String[] value = values.split(",");
		double battles = Double.parseDouble(value[1]);
		if (battles < prioritynum) return true; else return false;
	}

	public boolean createMeleeBattlesList() {
		Vector namesall = new Vector();
		Vector namesmini = new Vector();
		Vector namesmicro = new Vector();
		Vector namesnano = new Vector();
		Vector priorityall = new Vector();
		Vector prioritymini = new Vector();
		Vector prioritymicro = new Vector();
		Vector prioritynano = new Vector();

		//Read participants
		try {
			FileReader fr = new FileReader(participantsfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { 
				if (record.indexOf(",") != -1) {
					String id = record.substring(record.indexOf(",")+1);
					String name = record.substring(0,record.indexOf(","));
					String jar =name.replace(' ','_')+".jar";
					boolean exists = (new File(botsrepository+jar)).exists();
					if (exists) { 
						namesall.add(name);
						if (size.CheckCompetitorsForSize(name,name,1500))  namesmini.add(name);
						if (size.CheckCompetitorsForSize(name,name,750))  namesmicro.add(name);
						if (size.CheckCompetitorsForSize(name,name,250))  namesnano.add(name);
						if (robothaspriority(name,generalratings))  priorityall.add(name);
						if (size.CheckCompetitorsForSize(name,name,1500) && robothaspriority(name,miniratings))  prioritymini.add(name);
						if (size.CheckCompetitorsForSize(name,name,750) && robothaspriority(name,microratings))  prioritymicro.add(name);
						if (size.CheckCompetitorsForSize(name,name,250) && robothaspriority(name,nanoratings))  prioritynano.add(name);
					}
				}
			}
			br.close();
		} catch (Exception e) { 
			System.out.println("Participants file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		
		//Open battles file
		PrintStream outtxt = null;
		try { outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(battlesfile)),false); } 
		catch (IOException e) { 
			System.out.println("Not able to open battles file "+battlesfile+" ... Aborting"); 
			System.out.println(e);
			return false; 
		}

		//Create the participants file
		Random random = new Random();
		int count = 0; 

		//Add bots with less than 500 battles, or a random battle if all bots have enough battles
		while (count<numbattles && namesall.size()>meleebots) {
			String[] bots = null;
			if (priorityall.size() > 0 && namesall.size()>=meleebots) bots = getmeleebots(priorityall,namesall,random);
			else if (prioritymini.size()>0 && namesmini.size()>=meleebots) bots = getmeleebots(prioritymini,namesmini, random);
			else if (prioritymicro.size()>0 && namesmicro.size()>=meleebots) bots = getmeleebots(prioritymicro, namesmicro, random);
			else if (prioritynano.size()>0 && namesnano.size()>=meleebots) bots = getmeleebots(prioritynano, namesnano, random);
			else if (namesall.size()>=meleebots) bots = getmeleebots(namesall, namesall, random);
			if (bots != null) {
				String battle = bots[0]; for (int i=1; i<bots.length; i++) { battle += ","+bots[i]; } battle += ","+runonly;
				outtxt.println(battle);
				count++;
			} 	
		}
		outtxt.close();
		return true;
	}

	private String[] getmeleebots(Vector list1, Vector list2, Random rand) {
		String[] bots = new String[meleebots];
		bots[0] = (String) list1.get(rand.nextInt(list1.size())); 
		int count = 1;
		while (count < meleebots) {
			bots[count] =  (String) list2.get(rand.nextInt(list2.size()));
			boolean exists = false;
			for (int i=0; i< count; i++) {
				if (bots[i].equals(bots[count])) exists = true;
			}
			if (!exists) count++; 
		}
		return bots;
	}
	
}



