/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
/**
 * The Robocode Control API is used for controlling the Robocode
 * application from another external application. Basically, it is possible
 * to run battles between selected robots and retrieve the results. However,
 * it is also possible to get detailed information as snapshots during the
 * battles regarding e.g. the positions and headings of the individual robots
 * and bullets at a specific time during a battle.
 * <p>
 * The main entry class is the Robocode.Control.RobocodeEngine class, which
 * must be instantiated by the application controlling Robocode. With the
 * RobocodeEngine, a battle specification must be provided in order to run a
 * battle. The battle specification specify the size of the battlefield, and
 * which rules that must be used. In addition, the participant robots must be
 * selected, which must exist in the robot directory of Robocode in
 * advantage.
 * 
 * <h2>Example</h2>
 * <p>
 * Here is a simple application that runs a battle in Robocode for 5 rounds
 * on the default battlefield of 800x600 pixels. The robots selected for the
 * battle are sample.RamFire and sample.Corners.
 * 
 * <pre>
 * import robocode.control.*;
 * import robocode.control.events.*;
 * 
 * //
 * // Application that demonstrates how to run two sample robots in Robocode using the
 * // RobocodeEngine from the robocode.control package.
 * //
 * // @author Flemming N. Larsen
 * //
 * public class BattleRunner {
 * 
 *     public static void main(String[] args) {
 * 
 *         // Disable log messages from Robocode
 *         RobocodeEngine.setLogMessagesEnabled(false);
 *
 *         // Create the RobocodeEngine
 *         //   RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
 *         RobocodeEngine engine = new RobocodeEngine(new java.io.File("C:/Robocode")); // Run from C:/Robocode
 * 
 *         // Add our own battle listener to the RobocodeEngine 
 *         engine.addBattleListener(new BattleObserver());
 * 
 *         // Show the Robocode battle view
 *         engine.setVisible(true);
 * 
 *         // Setup the battle specification
 * 
 *         int numberOfRounds = 5;
 *         BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
 *         RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.RamFire,sample.Corners");
 * 
 *         BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
 * 
 *         // Run our specified battle and let it run till it is over
 *         engine.runBattle(battleSpec, true); // waits till the battle finishes
 * 
 *         // Cleanup our RobocodeEngine
 *         engine.close();
 * 
 *         // Make sure that the Java VM is shut down properly
 *         System.exit(0);
 *     }
 * }
 * 
 * //
 * // Our private battle listener for handling the battle event we are interested in.
 * //
 * class BattleObserver extends BattleAdaptor {
 * 
 *     // Called when the battle is completed successfully with battle results
 *     public void onBattleCompleted(BattleCompletedEvent e) {
 *         System.out.println("-- Battle has completed --");
 *         
 *         // Print out the sorted results with the robot names
 *         System.out.println("Battle results:");
 *         for (robocode.BattleResults result : e.getSortedResults()) {
 *             System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
 *         }
 *     }
 * 
 *     // Called when the game sends out an information message during the battle
 *     public void onBattleMessage(BattleMessageEvent e) {
 *         System.out.println("Msg> " + e.getMessage());
 *     }
 * 
 *     // Called when the game sends out an error message during the battle
 *     public void onBattleError(BattleErrorEvent e) {
 *         System.out.println("Err> " + e.getError());
 *     }
 * }
 * </pre>
 * <h2>Notice</h2>
 * <p>
 * In order to avoid ClassNotFoundException with your application, you will need to add most of the .jar files
 * located under the /libs folder of the robocode directory to the classpath of your application.
 * That is robocode.jar, picocontainer-xxx.jar etc. You can leave out roborumble.jar and the ones for UI and
 * sound if you don't use the UI with the RobocodeEngine.</p>
 */
package robocode.control;
