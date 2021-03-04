/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;


import net.sf.robocode.io.Logger;

import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.robotinterfaces.IBasicRobot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static robocode.util.Utils.isNear;


/**
 * RobotTestBed provides a superclass that can be extended in order to implement JUnit tests
 * for Robocode robots.
 * <p>
 * The user must set the system property robocode.home to the location of the robocode installation,
 * otherwise we cannot set up the Robocode engine.  If robocode.home is not a system property,
 * we throw a RuntimeException.
 *
 * @author Philip Johnson (original)
 * @author Pavel Savara (contributor)
 */
public abstract class RobotTestBed<R extends IBasicRobot> extends BattleAdaptor {
    /**
     * The Robocode game engine instance used for this test.
     */
    protected static IRobocodeEngine engine;
    /**
     * The battlefield specification, which is the default.
     */
    protected final BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();
    /**
     * The number of errors generated during this battle so far.
     */
    protected static int errors = 0;
    protected static StringBuilder errorText = new StringBuilder();
    /**
     * The number of messages generated during this battle so far.
     */
    protected static int messages = 0;
    protected static String robotsPath;

    /**
     * True to specify that the position during each turn should be printed out.
     */
    public static boolean isDumpingPositions = false;
    /**
     * True to specify that each turn should be printed out.
     */
    public static boolean isDumpingTurns = false;
    /**
     * True to specify that Robot output should be printed out.
     */
    public static boolean isDumpingOutput = true;
    /**
     * True to specify that error messages should be printed out.
     */
    public static boolean isDumpingErrors = true;
    /**
     * True to specify that Robot messages should be printed out.
     */
    public static boolean isDumpingMessages = true;


    /**
     * Instance of tested robot
     */
    protected R robotObject;

    protected void beforeInit() {
        System.setProperty("EXPERIMENTAL", "true");
        System.setProperty("TESTING", "true");

        try {
            File robotsPathFile = new File("../.sandbox/robots").getCanonicalFile().getAbsoluteFile();
            robotsPath = robotsPathFile.getPath();
        } catch (IOException e) {
            e.printStackTrace(Logger.realErr);
            throw new Error(e);
        }
        System.setProperty("ROBOTPATH", robotsPath);
    }

    public RobotTestBed() {
        if (engine == null) {
            beforeInit();
            engine = new RobocodeEngine(new BattleAdaptor() {
                public void onBattleMessage(BattleMessageEvent event) {
                    if (isDumpingMessages) {
                        Logger.realOut.println(event.getMessage());
                    }
                    messages++;
                }

                public void onBattleError(BattleErrorEvent event) {
                    if (isDumpingErrors) {
                        Logger.realErr.println(event.getError());
                    }
                    errorText.append("----------err #");
                    errorText.append(errors);
                    errorText.append("--------------------------------------------------\n");
                    errorText.append(event.getError());
                    errorText.append("\n");
                    errors++;
                }
            });
        }

        errors = 0;
        messages = 0;
    }

    @Override
    public void onRoundStarted(RoundStartedEvent event) {
        List<IBasicRobot> robotObjects = event.getRobotObjects();
        if (robotObjects != null && !robotObjects.isEmpty()) {
            this.robotObject = (R) robotObjects.get(0);
        }
        super.onRoundStarted(event);
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        if (isDumpingTurns) {
            Logger.realOut.println("turn " + event.getTurnSnapshot().getTurn());
        }
        for (IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
            if (isDumpingPositions) {
                Logger.realOut.print(robot.getVeryShortName());
                Logger.realOut.print(" X:");
                Logger.realOut.print(robot.getX());
                Logger.realOut.print(" Y:");
                Logger.realOut.print(robot.getY());
                Logger.realOut.print(" V:");
                Logger.realOut.print(robot.getVelocity());
                Logger.realOut.println();
            }
            if (isDumpingOutput) {
                Logger.realOut.print(robot.getOutputStreamSnapshot());
            }
        }
    }

    /**
     * Must return a fully qualified robot name to be in this battle.
     * <p>
     * You must override this event to specify the robot to battle in this test case.
     *
     * @return robot name.
     */
    public abstract String getRobotName();

    /**
     * Must return a fully qualified enemy robot name to be in this battle.
     * <p>
     * You must override this event to specify the robot to battle in this test case.
     *
     * @return robot name.
     */
    public String getEnemyName() {
        return "sample.Target";
    }

    /**
     * Provides the number of rounds in this battle.  Defaults to 1.
     * Override this to change the number of rounds.
     *
     * @return The number of rounds.
     */
    public int getNumRounds() {
        return 1;
    }

    /**
     * Returns a comma or space separated list like: x1,y1,heading1, x2,y2,heading2, which are the
     * coordinates and heading of robot #1 and #2. So "0,0,180, 50,80,270" means that robot #1
     * has position (0,0) and heading 180, and robot #2 has position (50,80) and heading 270.
     * <p>
     * Override this method to explicitly specify the initial positions.
     * <p>
     * Defaults to null, which means that the initial positions are determined randomly.  Since
     * battles are deterministic by default, the initial positions are randomly chosen but will
     * always be the same each time you run the test case.
     *
     * @return The list of initial positions.
     */
    public String getInitialPositions() {
        return null;
    }

    /**
     * Provides the number of robots in this battle.
     *
     * @param robotList The list of robots.
     * @return The number of robots in this battle.
     */
    public int getExpectedRobotCount(String robotList) {
        return robotList.split("[\\s,;]+").length;
    }

    /**
     * Defaults to true, indicating that the battle is deterministic and robots will always start
     * in the same position each time.
     * <p>
     * Override to support random initialization.
     *
     * @return True if the battle will be deterministic.
     */
    public boolean isDeterministic() {
        return true;
    }

    public boolean isEnableRecording() {
        return false;
    }

    /**
     * The setup method run before each test, which sets up the listener on the engine for testing.
     * Don't override this method; instead, override runSetup to add behavior before the test
     * battle starts.
     */
    protected void before() {
        engine.addBattleListener(this);
        if (isDeterministic()) {
            RandomFactory.resetDeterministic(0);
        }
        errors = 0;
        errorText = new StringBuilder();
        messages = 0;
    }

    protected void after() {
        engine.removeBattleListener(this);
    }

    protected void run() {
        runSetup();
		runBattle(getRobotName() + "," + getEnemyName(), getNumRounds(), getInitialPositions());
        runTeardown();
        final int expectedErrors = getExpectedErrors();

        if (errors != expectedErrors) {
            throw new AssertionError(
                    "Number of errors " + errors + " is different than expected " + expectedErrors + "\n" + errorText
                            + "======================================================================");
        }
    }

    protected int getExpectedErrors() {
        return 0;
    }

    protected void runSetup() {
    }

    protected void runTeardown() {
    }

    protected void runBattle(String robotList, int numRounds, String initialPositions) {
        final RobotSpecification[] robotSpecifications = engine.getLocalRepository(robotList);

        if (getExpectedRobotCount(robotList) > 0) {
            assertNotNull("Robot were not loaded", robotSpecifications);
            assertEquals("Robot were not loaded", getExpectedRobotCount(robotList), robotSpecifications.length);
            engine.runBattle(new BattleSpecification(numRounds, battleFieldSpec, robotSpecifications), initialPositions,
                    true, isEnableRecording());
        }
    }

    public static void assertNotNull(String message, Object value) {
        if (value == null) {
            throw new AssertionError(message);
        }
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            throw new AssertionError(message);
        }
        if (!expected.equals(actual)) {
            throw new AssertionError(message);
        }
    }

    public static void assertNear(String message, double expected, double actual) {
        if (!isNear(expected, actual)) {
            throw new AssertionError(message);
        }
    }
}