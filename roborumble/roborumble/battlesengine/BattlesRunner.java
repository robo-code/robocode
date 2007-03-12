package roborumble.battlesengine;
import robocode.*;
import robocode.control.*;
import robocode.battle.*;
import codesize.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * BattlesRunner - a class by Albert Perez
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files
 */

public class BattlesRunner
{
	private String inputfile;
	private int numrounds;
	private int fieldlen;
	private int fieldhei;
	private String outfile;
	private String user;
	private String game;
	private String matchtype; 
	private String isteams;
	
	public BattlesRunner(String propertiesfile) {
		//Read parameters
		Properties parameters = null;
		try { parameters = new Properties(); parameters.load(new FileInputStream(propertiesfile)); } 
		catch (Exception e) { System.out.println("Parameters File not found !!!"); }
		inputfile = parameters.getProperty("INPUT","");
		numrounds = Integer.parseInt(parameters.getProperty("ROUNDS","10"));
		fieldlen = Integer.parseInt(parameters.getProperty("FIELDL","800"));
		fieldhei = Integer.parseInt(parameters.getProperty("FIELDH","600"));
		outfile = parameters.getProperty("OUTPUT","");
		user = parameters.getProperty("USER","");
		matchtype = parameters.getProperty("RUNONLY","GENERAL");
		isteams = parameters.getProperty("TEAMS","NOT");
		game = propertiesfile;
		while (game.indexOf("/")!=-1) game = game.substring(game.indexOf("/")+1);
		game=game.substring(0,game.indexOf("."));
	}
	
	public boolean runBattles() {
		//Initialize objects
		Coordinator coord = new Coordinator();
		AtHomeListener listener = new AtHomeListener(coord);
		RobocodeEngineAtHome engine = new RobocodeEngineAtHome(listener);
		BattlefieldSpecification field = new BattlefieldSpecification(fieldlen,fieldhei);
		BattleSpecification battle = new BattleSpecification(numrounds, field, (new RobotSpecification[2]));
		
		//Read input file
		ArrayList robots = new ArrayList();
		
		try {
			FileReader fr = new FileReader(inputfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { robots.add(record); }
			br.close();
		} catch (Exception e) { 
			System.out.println("Battles input file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		
		//open output file
		PrintStream outtxt = null;
		try { outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile,true)),true); } 
		catch (IOException e) { 
			System.out.println("Not able to open output file ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		
		//run battle
		int index = 0; 
		while (index<robots.size()) {
			String[] param = ((String) robots.get(index)).split(",");
			//String enemies = ((String) robots.get(index))+","+((String) robots.get(index+1)); 
			String enemies = param[0]+","+param[1]; 
			//System.out.println("Fighting battle "+(index/2)+" ... "+enemies);
			System.out.println("Fighting battle "+(index)+" ... "+enemies);
			engine.runBattle(battle,enemies);			
			coord.get();
		//get results
			BattleResultsTableModel resultsTable = engine.getResults();
			String First = (String) resultsTable.getValueAt(0,0);
			First = First.substring(First.lastIndexOf(":")+2);
			int PointsFirst = Integer.parseInt((String) resultsTable.getValueAt(0,1));
			int BulletDFirst = Integer.parseInt((String) resultsTable.getValueAt(0,4));
			int SurvivalFirst = Integer.parseInt((String) resultsTable.getValueAt(0,8));
			String Second = (String) resultsTable.getValueAt(1,0);
			Second = Second.substring(Second.lastIndexOf(":")+2);
			int PointsSecond = Integer.parseInt((String) resultsTable.getValueAt(1,1));
			int BulletDSecond = Integer.parseInt((String) resultsTable.getValueAt(1,4));
			int SurvivalSecond = Integer.parseInt((String) resultsTable.getValueAt(1,8));
			
			//if it is a teams battle, version is not returned by robocode. Add it manually
			if (isteams.equals("YES")) {
				//String v1 = ((String) robots.get(index)).substring( ((String) robots.get(index)).indexOf(' ') );
				//String v2 = ((String) robots.get(index+1)).substring( ((String) robots.get(index+1)).indexOf(' ') );
				String v1 = param[0].substring( param[0].indexOf(' ') );
				String v2 = param[1].substring( param[1].indexOf(' ') );
				if ( param[0].indexOf(First) != -1) { First = First + v1; Second = Second + v2; }
				else { First = First + v2; Second = Second + v1; }
			}

			//outtxt.println(game+","+numrounds+","+fieldlen+"x"+fieldhei+","+user+","+System.currentTimeMillis()+","+matchtype);
			outtxt.println(game+","+numrounds+","+fieldlen+"x"+fieldhei+","+user+","+System.currentTimeMillis()+","+param[2]);
			outtxt.println(First+","+PointsFirst+","+BulletDFirst+","+SurvivalFirst);
			outtxt.println(Second+","+PointsSecond+","+BulletDSecond+","+SurvivalSecond);
			//index +=2;
			index++;
			System.out.println("RESULT = "+First+" wins "+PointsFirst+" to "+PointsSecond);
		}
		
		//close
		outtxt.close();
		engine.close();
		
		return true;
	}

	public boolean runMeleeBattles() {
		//Initialize objects
		Coordinator coord = new Coordinator();
		AtHomeListener listener = new AtHomeListener(coord);
		RobocodeEngineAtHome engine = new RobocodeEngineAtHome(listener);
		BattlefieldSpecification field = new BattlefieldSpecification(fieldlen,fieldhei);
		BattleSpecification battle = new BattleSpecification(numrounds, field, (new RobotSpecification[2]));
		
		//Read input file
		ArrayList robots = new ArrayList();
		
		try {
			FileReader fr = new FileReader(inputfile); 
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			while ( (record=br.readLine()) != null ) { robots.add(record); }
			br.close();
		} catch (Exception e) { 
			System.out.println("Battles input file not found ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		
		//open output file
		PrintStream outtxt = null;
		try { outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(outfile,true)),true); } 
		catch (IOException e) { 
			System.out.println("Not able to open output file ... Aborting"); 
			System.out.println(e);
			return false; 
		}
		
		//run battle
		int index = 0; 
		while (index<robots.size()) {
			String[] param = ((String) robots.get(index)).split(",");
			//String enemies = ((String) robots.get(index))+","+((String) robots.get(index+1)); 
			String enemies = "";
			for (int i=0; i<param.length-1; i++) { 
				if (i>0) enemies +=",";
				enemies += param[i];
			}
			//System.out.println("Fighting battle "+(index/2)+" ... "+enemies);
			System.out.println("Fighting battle "+(index)+" ... "+enemies);
			engine.runBattle(battle,enemies);			
			coord.get();
		//get results
			BattleResultsTableModel resultsTable = engine.getResults();
			String[] Bot = new String[param.length-1];
			int[] Points = new int[param.length-1];
			int[] BulletD = new int[param.length-1];
			int[] Survival = new int[param.length-1];
			for (int i=0; i<param.length-1; i++) {
				Bot[i] =  (String) resultsTable.getValueAt(i,0);
				Bot[i] = Bot[i].substring(Bot[i].lastIndexOf(":")+2);
				Points[i] = Integer.parseInt((String) resultsTable.getValueAt(i,1));
				BulletD[i] = Integer.parseInt((String) resultsTable.getValueAt(i,4));
				Survival[i] = Integer.parseInt((String) resultsTable.getValueAt(i,8));
			}
 
			for (int i=0; i<param.length-1; i++) {
				for (int j=0; j<param.length-1; j++) {
					if (i<j) {
						outtxt.println(game+","+numrounds+","+fieldlen+"x"+fieldhei+","+user+","+System.currentTimeMillis()+","+param[param.length-1]);
						outtxt.println(Bot[i]+","+Points[i]+","+BulletD[i]+","+Survival[i]);
						outtxt.println(Bot[j]+","+Points[j]+","+BulletD[j]+","+Survival[j]);
					}
				}
			}
			//index +=2;
			index++;
			System.out.println("RESULT = "+Bot[0]+" wins, "+Bot[1]+" is second.");
		}
		
		//close
		outtxt.close();
		engine.close();
		
		return true;
	}

		
}



