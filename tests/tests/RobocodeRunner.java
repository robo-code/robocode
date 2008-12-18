
import java.io.File;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.BattleStartedEvent;

/**
 * Application that runs two sample robots in Robocode.
 *
 * @author Flemming N. Larsen
 */
public class RobocodeRunner {

    public static void main(String[] args) {

        // Battle listener used for receiving battle events
    	BattleObserver battleListener = new BattleObserver();

        // Create the RobocodeEngine
//      RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
        RobocodeEngine engine = new RobocodeEngine(new File("C:/Robocode")); // Run from C:/Robocode

        // Add battle listener to our RobocodeEngine
        engine.addBattleListener(battleListener);

        // Show the battles
        engine.setVisible(true);

        // Setup the battle specification

        int numberOfRounds = 5;
        BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
        RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.RamFire, sample.Corners");

        BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);

        // Run our specified battle and let it run till it's over
        engine.runBattle(battleSpec, true /* wait till the battle is over */);

        // Cleanup our RobocodeEngine
        engine.close();

        // Make sure that the Java VM is shut down properly
        System.exit(0);
    }
}

/**
 * BattleListener for handling the battle event we are interested in.
 */
class BattleObserver extends BattleAdaptor {

    public void onBattleStarted(BattleStartedEvent e) {
        System.out.println("-- Battle was started --");
	}

	public void onBattleFinished(BattleFinishedEvent e) {
		if (e.isAborted()) {
			System.out.println("-- Battle was aborted --");
		} else {
			System.out.println("-- Battle was finished succesfully --");
		}
	}

	public void onBattleCompleted(BattleCompletedEvent e) {
        System.out.println("-- Battle has completed --");

        // Print out the sorted results with the robot names
        System.out.println("\n-- Battle results --");
        for (BattleResults result : e.getSortedResults()) {
            System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
        }
	}

	public void onBattleMessage(BattleMessageEvent e) {
        System.out.println("Msg> " + e.getMessage());
	}

	public void onBattleError(BattleErrorEvent e) {
        System.out.println("Err> " + e.getError());
	}
}
