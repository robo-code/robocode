package roborumble.netengine;

import java.net.*;
import java.util.*;
import java.util.Vector;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;
import robocode.util.*;
import roborumble.battlesengine.*;

/**
 * BotsDownload - a class by Albert Perez
 * Manages the download operations (participants and JAR files)
 * Controlled by properties files
 */

public class UpdateRatingFiles {

	//private String resultsfile;
	//private String resultsurl;
	//private String tempdir;
	private String game;
	//private String user;
	//private String sizesfile;
	//private String botsrepository;
	private String minibots;
	private String microbots;
	private String nanobots;
	//private CompetitionsSelector size;
	private String battlesnumfile;
	private String generalratings;
	private String miniratings;
	private String microratings;
	private String nanoratings;
	
	public UpdateRatingFiles(String propertiesfile) {
		//Read parameters
		Properties parameters = null;
		try { parameters = new Properties(); parameters.load(new FileInputStream(propertiesfile)); } 
		catch (Exception e) { System.out.println("Parameters File not found !!!"); }
		//resultsfile = parameters.getProperty("OUTPUT","");
		//resultsurl = parameters.getProperty("RESULTSURL","");
		//tempdir = parameters.getProperty("TEMP","");
		//user = parameters.getProperty("USER","");
		game = propertiesfile;
		while (game.indexOf("/")!=-1) game = game.substring(game.indexOf("/")+1);
		//botsrepository = parameters.getProperty("BOTSREP","");
		game=game.substring(0,game.indexOf("."));
		//sizesfile = parameters.getProperty("CODESIZEFILE","");
		minibots = parameters.getProperty("MINIBOTS","");
		microbots = parameters.getProperty("MICROBOTS","");
		nanobots = parameters.getProperty("NANOBOTS","");

		battlesnumfile = parameters.getProperty("BATTLESNUMFILE","");

		generalratings =  parameters.getProperty("RATINGS.GENERAL","");
		miniratings =  parameters.getProperty("RATINGS.MINIBOTS","");
		microratings =  parameters.getProperty("RATINGS.MICROBOTS","");
		nanoratings =  parameters.getProperty("RATINGS.NANOBOTS","");

		//Open competitions selector
		//size = new CompetitionsSelector(sizesfile,botsrepository);
		
	}

	public boolean UpdateRatings() {

		//read all the recors to be updated
		Vector battles = new Vector();
		try {
			FileReader fr = new FileReader(battlesnumfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) {  battles.add(record);  }
			br.close();
		} catch (Exception e) { System.out.println("Can't open # battles file ... Aborting # battles update"); return false; }

		//read the ratings files
		Properties all = null; try { all = new Properties(); all.load(new FileInputStream(generalratings)); } catch (Exception e) { System.out.println("All Ratings File not found !!!"); return false; }
		Properties mini = null; try { mini = new Properties(); mini.load(new FileInputStream(miniratings)); } catch (Exception e) { mini = null; }
		Properties micro = null; try { micro = new Properties(); micro.load(new FileInputStream(microratings)); } catch (Exception e) { micro = null; }
		Properties nano = null; try { nano = new Properties(); nano.load(new FileInputStream(nanoratings)); } catch (Exception e) { nano = null; }

		//update #battles
		for (int  i=0; i<battles.size(); i++) {
			String[] battle = ((String) battles.get(i)).split(",");
			battle[1] = battle[1].replaceAll(" ","_");
			double num = Double.parseDouble(battle[2]);
			if (battle[0].equals(game)) { updateRecord(battle[1],num,all); }
			else if (battle[0].equals(minibots) && mini != null) { updateRecord(battle[1],num,mini); }
			else if (battle[0].equals(microbots) && micro != null) { updateRecord(battle[1],num,micro); }
			else if (battle[0].equals(nanobots) && nano != null) { updateRecord(battle[1],num,nano); }
		}	

		//save ratings files
		try {
			all.store(new FileOutputStream(generalratings), "General ratings updated with new battles number");
			if (mini != null) mini.store(new FileOutputStream(miniratings), "Mini ratings updated with new battles number");
			if (micro != null) micro.store(new FileOutputStream(microratings), "Micro ratings updated with new battles number");
			if (nano != null) nano.store(new FileOutputStream(nanoratings), "Nano ratings updated with new battles number");		
		} catch (Exception e) { System.out.println("Encountered problems when saving updated number of battles"); return false; }

		return true;
	}

	private void updateRecord(String bot, double battles, Properties ratings) {
		String values = ratings.getProperty(bot);
		if (values == null) return;
		
		String[] value = values.split(",");
		//double newnum = battles + Double.parseDouble(value[1]);
		//values = value[0]+","+Double.toString(newnum)+","+value[2];
		values = value[0]+","+Double.toString(battles)+","+value[2];
		ratings.setProperty(bot,values);
	}

}



