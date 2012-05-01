/**
TO COMPILE: 
javac -classpath '/var/www/html/tankyudh_cfitbhu/virtual-combat/libs/*:/var/www/html/tankyudh_cfitbhu/virtual-combat/' BattleRunner.java

TO RUN:
java -classpath './:/var/www/html/tankyudh_cfitbhu/virtual-combat/libs/*:/var/www/html/tankyudh_cfitbhu/virtual-combat/*' BattleRunner
*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.robocode.cachecleaner.CacheCleaner;
import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.RepositoryManager;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.SettingsManager;
import robocode.control.*;
import robocode.control.events.*;
import java.sql.*;
import java.lang.*;
import java.util.*;

//import Connect;
/**
 * Application that demonstrates how to run two sample robots in Robocode using the
 * RobocodeEngine from the robocode.control package.
 *
 * @author Flemming N. Larsen
 */

/*
 * Battle Status :
 *  0 Unprocessed
 *  1 Processing
 *  2 Processed
 *  3 Battle Error: No bots in both teams
 *   
 * 
 */
public class BattleRunner {

	private static Connect ob;
	static int match_ID;
	public static int time_span_minutes = 3;
	public static String mainpath = "/var/www/html/tankyudh_cfitbhu/virtual-combat/";
        static String team1;
	public static int error = 0;
	public static Connect getObj(){
		return ob;
	}

    public static int getPid() throws IOException,InterruptedException {  
	      
	    Vector<String> commands=new Vector<String>();  
	    commands.add("/bin/bash");  
	    commands.add("-c");  
	    commands.add("echo $PPID");  
	    ProcessBuilder pb=new ProcessBuilder(commands);  
	      
	    Process pr=pb.start();  
	    pr.waitFor();  
	    if (pr.exitValue()==0) {  
	      BufferedReader outReader=new BufferedReader(new InputStreamReader(pr.getInputStream()));  
	      return Integer.parseInt(outReader.readLine().trim());  
	    } else {  
	      System.out.println("Error while getting PID");  
	      return -1;  
	    }  
    }
    
    public static void main(String[] args) {

    	Connection conn=null;	

    	try{
		String userName = "root";
		String password = "3pq,x-w6";
		String url = "jdbc:mysql://localhost:3306/robodata";
		Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		conn = DriverManager.getConnection (url, userName, password);
		System.out.println("BattleRunner##Connected to database successfully");
		ob=new Connect(conn);

		long startTime = System.currentTimeMillis();

		int progPid = getPid();
		ob.insertProcess(0,progPid);
		System.out.println("BattleRunner##Made an entry in processes table");
        // Create the RobocodeEngine
        // RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
        RobocodeEngine engine = new RobocodeEngine(new java.io.File(mainpath)); // Run from C:/Robocode


        /*for (int ii=0; ii<10; ii++) {
        	System.out.println("Battle No. ************************** ===== "+ ii);
        
    	SettingsManager properties = new SettingsManager();
		
		if (properties == null) {
			System.out.println("Properties is null");
			return;
		}
		
		System.out.println("BattleRunner properties : "+properties.toString());
		RepositoryManager repoManager = new RepositoryManager(properties);
		
		if (repoManager != null)
		{
			System.out.println("Inside BattleRunner : repositoryManager : "+repoManager.toString());
			System.out.println("Inside BattleRunner: reloading repoManager");
			repoManager.reload(false);
			System.out.println("Refreshed.......");
		}*/
		
        // Add our own battle listener to the RobocodeEngine
		
        engine.addBattleListener(new BattleObserver());

        // Show the Robocode battle view
        engine.setVisible(false);

        // Setup the battle specification

        int numberOfRounds = 3;
        BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
        System.getProperty("user.dir");

		
        //RobotSpecification[] selectedRobots = engine.getLocalRepository("CTFbots.FlagSeeker5Robots,sample.CrazyTeam6Memberteam");
        //RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.MixedTeam,sample.SampleBots4DifferentMemberTeam");
	
	RobotSpecification[] selectedRobots;
	/*IRepositoryManager repositoryManager = Container.getComponent(IRepositoryManager.class);

	if (repositoryManager != null)
	{
		repositoryManager.refresh(true);
		System.out.println("Refreshed.......");
	}
	*/
/*	try{
		String userName = "root";
		String password = "public123";
		String url = "jdbc:mysql://localhost:3306/robodata";
		Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		conn = DriverManager.getConnection (url, userName, password);
		System.out.println("Connected to database successfully");
		ob=new Connect(conn);

		long startTime = System.currentTimeMillis();
*/		
		for(int infinite_loop =  0;; infinite_loop++)
		{
			//CacheCleaner.clean();
			 
			for(InputTable e:ob.getUnprocessedBattles(progPid))
			{
		    		System.out.println("BattleRunner## Found some pending battles");
			    	team1 = e.team1;
			    	String team2 = e.team2;
			    	match_ID=e.matchID;
			    	selectedRobots = engine.getLocalRepository(team1+","+team2);

			    /* Catch Exceptions if team is not found. */

			    	RobotSpecification[] team1Robots = engine.getLocalRepository(team1);
			    	RobotSpecification[] team2Robots = engine.getLocalRepository(team2);

			    	if (selectedRobots.length == 0) {
			    		System.out.println("No robots in both teams.");
			    		//change status to 3
					error = 3;
			    		ob.updateStatus(3, e.matchID);
			    		continue;
		
			    	}
	
			    	if (team1Robots.length == 0) {
			    		System.out.println("Bots not found for team "+ team1);
			    		ob.updateStatus(progPid, e.matchID);
			    		//Set team2 as winner
			    		continue;
			    	}
			    	if (team2Robots.length == 0) {
			    		System.out.println("Bots not found for team "+ team2);
					ob.updateStatus(progPid, e.matchID);
			    		//Set team1 as winner
			    		continue;
			    	}

			    	//BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots,"/home/himanshu/Desktop/workspace-Dec24-0500hrs-bak/workspace/target/robocode-1.7.2.2-Beta-setup/battles/1.br");
	
	
			    	/* To delete the extra bots in a team. Done by modified selectedbots[].*/


				/*System.out.println("BattleRunner: Initial no. of bots = "+selectedRobots.length);
				System.out.println("BattleRunner: Initial bots = "+selectedRobots.toString());
		
				for (int i=0; i<selectedRobots.length; i++) {
					System.out.println("BattleRunner: Teamname = "+selectedRobots[i].getTeamId());
					System.out.println("BattleRunner: botname = "+selectedRobots[i].getName());
				}*/
				System.out.println("BattleRunner## Checking whether number of robots are more than 4");
				Integer[] count_bots = new Integer[2]; // Number of teams = 2
				count_bots[0] = count_bots[1] = 0;
				//			    	System.out.println("Here3");
				List<RobotSpecification> newselectedRobots = new ArrayList<RobotSpecification>();
				Hashtable<String, Integer> teamNames = new Hashtable<String, Integer>();
			    	//System.out.println("Here4");
				RobotSpecification specification = selectedRobots[0];  // Get the robot 0.
			    	String teamFullName = HiddenAccess.getRobotTeamName(specification);
				if(teamFullName == null)
				{
					error = 3;
					ob.updateStatus(3, e.matchID);
					continue;
				}
							    	System.out.println("Here5 TeamName ="+teamNames+" TeamFullName = "+teamFullName);
			    	teamNames.put(teamFullName, 0);      // an integer is assigned to team
			    	//System.out.println("Here1.. blah blah");
				for (int i=0; i<selectedRobots.length; i++) {
					specification = selectedRobots[i];
					teamFullName = HiddenAccess.getRobotTeamName(specification);
			
					if (teamFullName != null) {
						if (!teamNames.containsKey(teamFullName))
							teamNames.put(teamFullName, 1);
					
						int num = count_bots[teamNames.get(teamFullName)];
						if (num < 4) {
							newselectedRobots.add(specification);
							count_bots[teamNames.get(teamFullName)]++;
						}
					}
				}
			    	//System.out.println("Here2");
				//System.out.println("New no. of bots = "+newselectedRobots.size());
		
				RobotSpecification[] finalSelectedRobots = new RobotSpecification[newselectedRobots.size()];
		
				for (int i=0; i<newselectedRobots.size(); i++)
					finalSelectedRobots[i] = newselectedRobots.get(i);
				//System.out.println("Here3");
				/*System.out.println("BattleRunner: Initial no. of bots = "+finalSelectedRobots.length);
				System.out.println("BattleRunner: Initial bots = "+finalSelectedRobots.toString());
		
				for (int i=0; i<finalSelectedRobots.length; i++) {
					System.out.println("BattleRunner: Teamname = "+finalSelectedRobots[i].getTeamId());
					System.out.println("BattleRunner: botname = "+finalSelectedRobots[i].getName());
				}*/
				System.out.println("BattleRunner## Setting up the battle specification");
				BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, finalSelectedRobots,(mainpath+"/battles/"+match_ID+".br"));

				// Run our specified battle and let it run till it is over
				try {
					System.out.println("BattleRunner## Going to run a battle");
					engine.runBattle(battleSpec, true/* wait till the battle is over */);
				} catch(Exception e3){
					e3.printStackTrace();
				}
			}

			long currentTime = System.currentTimeMillis();
			
			if( (currentTime - startTime)/(60*1000) > time_span_minutes)
			{
				System.out.println("I've handled enough battles, i'm too tired. So this instance is going to exit.");
				
				int progPid2 = getPid();
				//ob.insertProcess(progPID);
				ob.updateProcess(3,progPid2);

				try{
			    		if(ob!=null) ob.endConnection();
			    		if(conn!=null) conn.close();
			    	}
			    	catch(SQLException e){
			    		e.printStackTrace();
			    	}
			    	
				
				engine.close();
				System.exit(0);
			
			}
			else
			{
				if(infinite_loop%5 == 0)
				{
					System.gc();
				}
			}
			
			
		  }

	}//end try
	catch(Exception e){
		System.out.println("Catched the exception " + e.getMessage());
	}
	finally{
	    	try{
	    		if(ob!=null) ob.endConnection();
	    		if(conn!=null) conn.close();
	    	}
	    	catch(SQLException e){
	    		e.printStackTrace();
	    	}
    		
        }
        // Cleanup our RobocodeEngine
      // engine.close();

        // Make sure that the Java VM is shut down properly
        System.exit(0);
    }
}


/**
 * Our private battle listener for handling the battle event we are interested in.
 */
class BattleObserver extends BattleAdaptor {

    // Called when the battle is completed successfully with battle results
    public void onBattleCompleted(BattleCompletedEvent e) {
        System.out.println("-- Battle has completed --");
        
        // Print out the sorted results with the robot names
        System.out.println("Battle results:");
        for (robocode.BattleResults result : e.getSortedResults()) {
            System.out.println("  " + result.getTeamName() + ": " + result.getCombinedScore());
        }
        BattleRunner.getObj().saveResult(e,BattleRunner.match_ID,BattleRunner.team1);
    }

    // Called when the game sends out an information message during the battle
    public void onBattleMessage(BattleMessageEvent e) {
        System.out.println("Msg> " + e.getMessage());
    }

    // Called when the game sends out an error message during the battle
    public void onBattleError(BattleErrorEvent e) {
        System.out.println("Err> " + e.getError());
    }
}
