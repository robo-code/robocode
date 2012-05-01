import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.robocode.cachecleaner.CacheCleaner;
import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.RepositoryManager;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.SettingsManager;
import robocode.control.*;
import robocode.control.events.*;

/**
 * Application that demonstrates how to run two sample robots in Robocode using the
 * RobocodeEngine from the robocode.control package.
 *
 * @author Flemming N. Larsen
 */
public class BattleRunner {

    public static void main(String[] args) {

    	System.out.println("current dir :"+System.getProperty("user.dir"));
        // Create the RobocodeEngine
          // RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
        RobocodeEngine engine = new RobocodeEngine(new java.io.File("/home/himanshu/Desktop/Codefest11/VirtualCombat/forServer/workspace-11Jan-0130hrs/target/robocode-1.7.2.2-Beta-setup")); // Run from C:/Robocode


        for (int ii=0; ii<10; ii++) {
        	System.out.println("Battle No. ************************** ===== "+ ii);
        
    	SettingsManager properties = new SettingsManager();
		
		if (properties == null) {
			System.out.println("Properties is null");
			return;
		}
		
		byte[] bo = new byte[100];
		
		try {
		System.out.println("Calculating pid");
		  String[] cmd = {"bash", "-c", "echo $PPID"};
		  Process p = Runtime.getRuntime().exec(cmd);
		  p.getInputStream().read(bo);
		  System.out.println("My PID is === "+new String(bo));

		  //System.out.println(p.toString());
		//  Thread.currentThread().sleep(20000);
			} catch (Exception e2) { 
		// TODO Auto-generated catch block 
		System.out.println("Calcjhkhghjgjgffjffhgfhulating pid");
		
		e2.printStackTrace(); 
		}
		
			System.out.println("Calculatkljkjlj;lklkl;king pid");
		String s = new String(bo);
		System.out.println(s);
		Integer mypid = Integer.parseInt(s);
		System.out.println("Calcukjjlating pid"+mypid);
		
		//ob.updateProcessStatus(3,mypid);
		System.out.println("BattleRunner properties : "+properties.toString());
		RepositoryManager repoManager = new RepositoryManager(properties);
		
		if (repoManager != null)
		{
			System.out.println("Inside BattleRunner : repositoryManager : "+repoManager.toString());
			System.out.println("Inside BattleRunner: reloading repoManager");
			repoManager.reload(false);
			System.out.println("Refreshed.......");
		}
		
		
        // Add our own battle listener to the RobocodeEngine 
        engine.addBattleListener(new BattleObserver());

        // Show the Robocode battle view
        engine.setVisible(false);

        // Setup the battle specification

        int numberOfRounds = 5;
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

	
	//CacheCleaner.clean();
	String team1 = "sample.CrazyTeam6";
	String team2 = "CTFbots.FlagSeekerTeam5";
	if (ii>3)
		selectedRobots = engine.getLocalRepository("sample.MixedTeam,CTFbots.FlagSeekerTeam5");
	else
		//selectedRobots = engine.getLocalRepository("sample.CrazyTeam6Memberteam,sample.MixedTeam");
		selectedRobots = engine.getLocalRepository(team1+","+team2);

	/* Catch Exceptions if team is not found. */
	
	RobotSpecification[] team1Robots = engine.getLocalRepository(team1);
	RobotSpecification[] team2Robots = engine.getLocalRepository(team2);
	
	if (selectedRobots.length == 0) {
		System.out.println("No robots in both teams.");
		continue;
	}
	
	if (team1Robots.length == 0) {
		System.out.println("Bots not found for team "+ team1);
		continue;
	}
	if (team2Robots.length == 0) {
		System.out.println("Bots not found for team "+ team2);
		continue;
	}
	
        //BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots,"/home/himanshu/Desktop/workspace-Dec24-0500hrs-bak/workspace/target/robocode-1.7.2.2-Beta-setup/battles/1.br");
        
        
        /* To delete the extra bots in a team. Done by modified selectedbots[].*/


        System.out.println("BattleRunner: Initial no. of bots = "+selectedRobots.length);
        System.out.println("BattleRunner: Initial bots = "+selectedRobots.toString());
        
        for (int i=0; i<selectedRobots.length; i++) {
        	System.out.println("BattleRunner: Teamname = "+selectedRobots[i].getTeamId());
        	System.out.println("BattleRunner: botname = "+selectedRobots[i].getName());
        }
        
        Integer[] count_bots = new Integer[2]; // Number of teams = 2
        count_bots[0] = count_bots[1] = 0;
        
        List<RobotSpecification> newselectedRobots = new ArrayList<RobotSpecification>();
        Hashtable<String, Integer> teamNames = new Hashtable<String, Integer>();
        
        RobotSpecification specification = selectedRobots[0];  // Get the robot 0.
    	String teamFullName = HiddenAccess.getRobotTeamName(specification);
    	teamNames.put(teamFullName, 0);      // an integer is assigned to team
    	
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
        
        System.out.println("New no. of bots = "+newselectedRobots.size());
        
        RobotSpecification[] finalSelectedRobots = new RobotSpecification[newselectedRobots.size()];
        
        for (int i=0; i<newselectedRobots.size(); i++)
        	finalSelectedRobots[i] = newselectedRobots.get(i);
        
        System.out.println("BattleRunner: Initial no. of bots = "+finalSelectedRobots.length);
        System.out.println("BattleRunner: Initial bots = "+finalSelectedRobots.toString());
        
        for (int i=0; i<finalSelectedRobots.length; i++) {
        	System.out.println("BattleRunner: Teamname = "+finalSelectedRobots[i].getTeamId());
        	System.out.println("BattleRunner: botname = "+finalSelectedRobots[i].getName());
        }
        
        BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, finalSelectedRobots,"/home/himanshu/Desktop/Codefest11/VirtualCombat/forServer/workspace-11Jan-0130hrs/target/robocode-1.7.2.2-Beta-setup/battles/"+ii+".br" );

        // Run our specified battle and let it run till it is over
        engine.runBattle(battleSpec, true/* wait till the battle is over */);

}//end for loop
        // Cleanup our RobocodeEngine
        engine.close();

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