/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
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
 * The main entry class is the {@link robocode.control.RobocodeEngine} class, which
 * must be instantiated by the application controlling Robocode. With the
 * RobocodeEngine, a battle specification must be provided in order to run a
 * battle. The battle specification specify the size of the battlefield, and
 * which rules that must be used. In addition, the participant robots must be
 * selected, which must exist in the robot directory of Robocode in
 * advantage.
 *
 * <h2>Running many battles</h2>
 * <p>
 * Creating the RobocodeEngine takes time, as Robocode must start up and load the robots.
 * Running a battle afterwards is fast. So when many battles must be run, e.g. for a tournament,
 * for testing many robots, or for a genetic algorithm, keep the same RobocodeEngine and call
 * {@link robocode.control.RobocodeEngine#runBattle(BattleSpecification, boolean) runBattle()} for each
 * battle. Close the engine when all battles are over. This is much faster than starting Robocode
 * from the command line for each battle.
 * <p>
 * The battles can run without showing the battle view, by calling
 * {@link robocode.control.RobocodeEngine#setVisible(boolean) setVisible(false)}, which is also the default.
 *
 * <h2>Example</h2>
 * <p>
 * Here is a simple application that runs two battles in Robocode for 5 rounds each
 * on the default battlefield of 800x600 pixels, without showing the battle view.
 * The first battle is between sample.RamFire and sample.Corners, and the second
 * battle is between sample.Walls and sample.SpinBot.
 *
 * <pre>
 * import robocode.control.*;
 * import robocode.control.events.*;
 *
 * //
 * // Application that demonstrates how to run battles between sample robots in Robocode using the
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
 *         RobocodeEngine engine = new RobocodeEngine(); // Run from current working directory
 *         //   RobocodeEngine engine = new RobocodeEngine(new java.io.File("C:/Robocode")); // Run from C:/Robocode
 *
 *         // Add our own battle listener to the RobocodeEngine
 *         engine.addBattleListener(new BattleObserver());
 *
 *         // Do not show the Robocode battle view. Use true to watch the battles.
 *         engine.setVisible(false);
 *
 *         // Setup the battle specifications
 *
 *         int numberOfRounds = 5;
 *         BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
 *         String[] battles = { "sample.RamFire,sample.Corners", "sample.Walls,sample.SpinBot" };
 *
 *         // Run all battles with the same RobocodeEngine
 *         for (String robots : battles) {
 *             RobotSpecification[] selectedRobots = engine.getLocalRepository(robots);
 *             BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);
 *
 *             // Run our specified battle and let it run till it is over
 *             engine.runBattle(battleSpec, true); // waits till the battle finishes
 *         }
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
 *         System.out.println("Msg&gt; " + e.getMessage());
 *     }
 *
 *     // Called when the game sends out an error message during the battle
 *     public void onBattleError(BattleErrorEvent e) {
 *         System.out.println("Err&gt; " + e.getError());
 *     }
 * }
 * </pre>
 *
 * <h2>Compiling and running the example</h2>
 * <p>
 * Save the example as BattleRunner.java in the Robocode directory, e.g. C:\Robocode, and compile
 * and run it from there:
 *
 * <pre>
 * javac -cp "libs/*" BattleRunner.java
 * java -cp "libs/*:." -XX:+IgnoreUnrecognizedVMOptions
 *     --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED
 *     --add-opens=java.base/java.lang.reflect=ALL-UNNAMED
 *     --add-opens=java.desktop/javax.swing.text=ALL-UNNAMED
 *     --add-opens=java.desktop/sun.awt=ALL-UNNAMED
 *     BattleRunner
 * </pre>
 * <p>
 * Enter the java command on one line. On Windows, use {@code "libs/*;."} as the classpath.
 * The {@code --add-opens} options are the ones used by robocode.sh and robocode.bat, and
 * {@code -XX:+IgnoreUnrecognizedVMOptions} lets Java 8 ignore them.
 * <p>
 * With Java 18 to 23, also add {@code -Djava.security.manager=allow}, so Robocode can use its security manager
 * for the robots. Without it, the robot classes are only checked when they are loaded, like with Java 24 and newer.
 * Do not add it with Java 24 and newer, as Java will then fail to start.
 *
 * <h2>Notice</h2>
 * <p>
 * In order to avoid ClassNotFoundException with your application, you will need to add most of the .jar files
 * located under the /libs folder of the robocode directory to the classpath of your application.
 * That is robocode.jar, picocontainer-xxx.jar etc. You can leave out roborumble.jar and the ones for UI and
 * sound if you don't use the UI with the RobocodeEngine.</p>
 */
package robocode.control;
