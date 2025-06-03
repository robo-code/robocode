/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
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

import static robocode.util.Utils.assertEquals;
import static robocode.util.Utils.assertNotNull;


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
 * @author Flemming N. Larsen (contributor)
 */
public abstract class RobotTestBed<R extends IBasicRobot> extends BattleAdaptor {
    /**
     * The Robocode game engine instance used for this test.
     */
    protected static volatile IRobocodeEngine engine;
    private static final Object engineLock = new Object();
    /**
     * The battlefield specification, which is the default.
     */
    protected final BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();
    /**
     * The number of errors generated during this battle so far.
     */
    protected int errors = 0;
    protected StringBuilder errorText = new StringBuilder();
    protected Throwable lastError = null;
    protected EngineErrorsListener engineErrorsListener = new EngineErrorsListener();
    protected TestErrorListener testErrorListener = new TestErrorListener();

    /**
     * The number of messages generated during this battle so far.
     */
    protected int messages = 0;
    protected static volatile String robotsPath;
    private static final Object ROBOTS_PATH_LOCK = new Object();

    /**
     * True to specify that the position during each turn should be printed out.
     */
    private static volatile boolean isDumpingPositions = false;
    /**
     * True to specify that each turn should be printed out.
     */
    private static volatile boolean isDumpingTurns = false;
    /**
     * True to specify that Robot output should be printed out.
     */
    private static volatile boolean isDumpingOutput = true;
    /**
     * True to specify that error messages should be printed out.
     */
    private static volatile boolean isDumpingErrors = true;
    /**
     * True to specify that Robot messages should be printed out.
     */
    private static volatile boolean isDumpingMessages = true;

    private static final Object FLAGS_LOCK = new Object();

    /**
     * Thread-safe method to modify the position dumping flag.
     * Use this instead of directly modifying the isDumpingPositions field.
     *
     * @param value The new value for the flag
     */
    public static void setPositionDumping(boolean value) {
        synchronized (FLAGS_LOCK) {
            isDumpingPositions = value;
        }
    }

    /**
     * Thread-safe method to modify the turn dumping flag.
     * Use this instead of directly modifying the isDumpingTurns field.
     *
     * @param value The new value for the flag
     */
    public static void setTurnDumping(boolean value) {
        synchronized (FLAGS_LOCK) {
            isDumpingTurns = value;
        }
    }

    /**
     * Thread-safe method to modify the output dumping flag.
     * Use this instead of directly modifying the isDumpingOutput field.
     *
     * @param value The new value for the flag
     */
    public static void setOutputDumping(boolean value) {
        synchronized (FLAGS_LOCK) {
            isDumpingOutput = value;
        }
    }

    /**
     * Thread-safe method to modify the error dumping flag.
     * Use this instead of directly modifying the isDumpingErrors field.
     *
     * @param value The new value for the flag
     */
    public static void setErrorDumping(boolean value) {
        synchronized (FLAGS_LOCK) {
            isDumpingErrors = value;
        }
    }

    /**
     * Thread-safe method to modify the message dumping flag.
     * Use this instead of directly modifying the isDumpingMessages field.
     *
     * @param value The new value for the flag
     */
    public static void setMessageDumping(boolean value) {
        synchronized (FLAGS_LOCK) {
            isDumpingMessages = value;
        }
    }

    /**
     * Gets whether robot positions should be printed during each turn.
     *
     * @return true if position dumping is enabled
     */
    public static boolean isDumpingPositions() {
        return isDumpingPositions;
    }

    /**
     * Sets whether robot positions should be printed during each turn.
     *
     * @param dumpingPositions true to enable position dumping
     */
    public static void setDumpingPositions(boolean dumpingPositions) {
        synchronized (FLAGS_LOCK) {
            isDumpingPositions = dumpingPositions;
        }
    }

    /**
     * Gets whether each turn should be printed out.
     *
     * @return true if turn dumping is enabled
     */
    public static boolean isDumpingTurns() {
        return isDumpingTurns;
    }

    /**
     * Sets whether each turn should be printed out.
     *
     * @param dumpingTurns true to enable turn dumping
     */
    public static void setDumpingTurns(boolean dumpingTurns) {
        synchronized (FLAGS_LOCK) {
            isDumpingTurns = dumpingTurns;
        }
    }

    /**
     * Gets whether robot output should be printed out.
     *
     * @return true if output dumping is enabled
     */
    public static boolean isDumpingOutput() {
        return isDumpingOutput;
    }

    /**
     * Sets whether robot output should be printed out.
     *
     * @param dumpingOutput true to enable output dumping
     */
    public static void setDumpingOutput(boolean dumpingOutput) {
        synchronized (FLAGS_LOCK) {
            isDumpingOutput = dumpingOutput;
        }
    }

    /**
     * Gets whether error messages should be printed out.
     *
     * @return true if error dumping is enabled
     */
    public static boolean isDumpingErrors() {
        return isDumpingErrors;
    }

    /**
     * Sets whether error messages should be printed out.
     *
     * @param dumpingErrors true to enable error dumping
     */
    public static void setDumpingErrors(boolean dumpingErrors) {
        synchronized (FLAGS_LOCK) {
            isDumpingErrors = dumpingErrors;
        }
    }

    /**
     * Gets whether robot messages should be printed out.
     *
     * @return true if message dumping is enabled
     */
    public static boolean isDumpingMessages() {
        return isDumpingMessages;
    }

    /**
     * Sets whether robot messages should be printed out.
     *
     * @param dumpingMessages true to enable message dumping
     */
    public static void setDumpingMessages(boolean dumpingMessages) {
        synchronized (FLAGS_LOCK) {
            isDumpingMessages = dumpingMessages;
        }
    }


    /**
     * Instance of tested robot
     */
    protected R robotObject;

    public RobotTestBed() {
        synchronized (engineLock) {
            if (engine == null) {
                beforeInit();
                engine = new RobocodeEngine();
                afterInit();
            }
        }

        errors = 0;
        messages = 0;
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
        if (robotList == null || robotList.isEmpty()) {
            return 0;
        }
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

    public boolean isEnableScreenshots() {
        return false;
    }

    protected void beforeInit() {
        System.setProperty("EXPERIMENTAL", "true");
        System.setProperty("TESTING", "true");
        System.setProperty("robocode.options.battle.desiredTPS", "10000");

        // Ensure security is enabled without SecurityManager (removed in Java 24)
        System.setProperty("NOSECURITY", "false");

        // Enable custom security adapter for Java 24+
        System.setProperty("robocode.security.adapter", "true");

        synchronized (ROBOTS_PATH_LOCK) {
            if (robotsPath == null) {
                try {
                    // Use configurable path via system property if available
                    String configPath = System.getProperty("robocode.robot.test.path");
                    File robotsPathFile = (configPath != null) ?
                            new File(configPath).getCanonicalFile().getAbsoluteFile() :
                            new File("../.sandbox/test-robots").getCanonicalFile().getAbsoluteFile();

                    if (!robotsPathFile.exists()) {
                        robotsPathFile.mkdirs();
                    }

                    robotsPath = robotsPathFile.getPath();
                } catch (IOException e) {
                    Logger.logError("Error initializing robots path", e);
                    throw new RuntimeException("Error initializing robot test bed", e);
                }
            }
        }

        System.setProperty("ROBOTPATH", robotsPath);
        if (isEnableScreenshots()) {
            System.setProperty("PAINTING", "true");
        }
    }

    /**
     * Called after the engine is initialized to perform additional setup.
     * Use this method to configure engine properties after initialization.
     */
    protected void afterInit() {
        try {
            if (isEnableScreenshots() && engine != null) {
                engine.setVisible(true);
            }
        } catch (Exception e) {
            Logger.logError("Error in afterInit", e);
            cleanup(); // Clean up resources if initialization fails
            throw new RuntimeException("Error configuring Robocode engine", e);
        }
    }

    /**
     * The setup method run before each test, which sets up the listener on the engine for testing.
     * Don't override this method; instead, override runSetup to add behavior before the test
     * battle starts.
     */
    protected void before() {
        engine.addBattleListener(engineErrorsListener);
        engine.addBattleListener(testErrorListener);
        if (isDeterministic()) {
            RandomFactory.resetDeterministic(0);
        }
        errors = 0;
        errorText = new StringBuilder();
        messages = 0;
        lastError = null;
    }

    protected void after() {
        engine.removeBattleListener(engineErrorsListener);
        engine.removeBattleListener(testErrorListener);
    }

    /**
     * Releases any resources used by this test bed.
     * <p>
     * This method should be called when the test framework is being shut down to ensure
     * proper cleanup of resources. It will close the Robocode engine and release all
     * associated resources.
     * <p>
     * It is recommended to call this method in a finally block or shutdown hook to
     * ensure resources are properly released even if tests fail.
     */
    public static void cleanup() {
        synchronized (engineLock) {
            if (engine != null) {
                try {
                    engine.close();
                } catch (Exception e) {
                    Logger.logError("Error closing Robocode engine", e);
                } finally {
                    engine = null;
                }
            }
        }
    }

    protected void run(String robotName, String enemyName) {
        runSetup();
        runBattle(robotName + "," + enemyName, getNumRounds(), getInitialPositions());
        runTeardown();
        final int expectedErrors = getExpectedErrors();

        if (lastError != null) {
            Class<?> errorClass = lastError.getClass();
            if (RuntimeException.class.isAssignableFrom(errorClass)) {
                throw (RuntimeException) lastError;
            } else if (Error.class.isAssignableFrom(errorClass)) {
                throw (Error) lastError;
            } else {
                throw new Error(lastError);
            }
        }
        if (errors != expectedErrors) {
            throw new AssertionError(
                    "Number of errors " + errors + " is different than expected " + expectedErrors + "\n" + errorText
                            + "======================================================================");
        }
    }

    protected void run(String enemyName) {
        run(getRobotName(), enemyName);
    }

    protected void run() {
        run(getRobotName(), getEnemyName());
    }

    protected int getExpectedErrors() {
        return 0;
    }

    protected void runSetup() {
    }

    protected void runTeardown() {
    }

    protected void runBattle(String robotList, int numRounds, String initialPositions) {
        if (robotList == null || robotList.isEmpty()) {
            throw new IllegalArgumentException("Robot list cannot be null or empty");
        }

        final RobotSpecification[] robotSpecifications = engine.getLocalRepository(robotList);

        if (getExpectedRobotCount(robotList) > 0) {
            assertNotNull("Robot were not loaded", robotSpecifications);
            assertEquals("Robot were not loaded", getExpectedRobotCount(robotList), robotSpecifications.length);
            engine.runBattle(new BattleSpecification(numRounds, battleFieldSpec, robotSpecifications), initialPositions,
                    true, isEnableRecording());
        }
    }

    class EngineErrorsListener extends BattleAdaptor {
        public void onBattleMessage(BattleMessageEvent event) {
            if (isDumpingMessages) {
                Logger.realOut.println(event.getMessage());
            }
            RobotTestBed.this.messages++;
        }

        @Override
        public void onRoundStarted(RoundStartedEvent event) {
            List<IBasicRobot> robotObjects = event.getRobotObjects();
            if (robotObjects != null && !robotObjects.isEmpty()) {
                IBasicRobot robot = robotObjects.get(0);
                try {
                    @SuppressWarnings("unchecked")
                    R typedRobot = (R) robot;
                    RobotTestBed.this.robotObject = typedRobot;
                } catch (ClassCastException e) {
                    Logger.logError("Error casting robot to expected type: " + robot.getClass().getName(), e);
                }
            }
        }

        @Override
        public void onBattleError(BattleErrorEvent event) {
            String error = event.getError();
            if (isDumpingErrors) {
                Logger.realErr.println(error);
            }
            errorText.append("----------err #");
            errorText.append(errors);
            errorText.append("--------------------------------------------------\n");
            errorText.append(error);
            errorText.append("\n");
            errors++;
        }

        @Override
        public void onTurnEnded(TurnEndedEvent event) {
            if (event == null || event.getTurnSnapshot() == null) {
                return;
            }

            if (isDumpingTurns) {
                Logger.realOut.println("turn " + event.getTurnSnapshot().getTurn());
            }

            IRobotSnapshot[] robots = event.getTurnSnapshot().getRobots();
            if (robots == null) {
                return;
            }

            for (IRobotSnapshot robot : robots) {
                if (robot == null) {
                    continue;
                }

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
                    String outputSnapshot = robot.getOutputStreamSnapshot();
                    if (outputSnapshot != null) {
                        Logger.realOut.print(outputSnapshot);
                    }
                }
            }
        }
    }

    class TestErrorListener implements IBattleListener {
        protected void handleError(Throwable ex) {
            if (ex == null) {
                return;
            }

            synchronized (RobotTestBed.this) {
                if (RobotTestBed.this.lastError == null) {
                    RobotTestBed.this.lastError = ex;
                    try {
                        if (isEnableScreenshots() && engine != null) {
                            engine.takeScreenshot();
                        }
                        if (engine != null) {
                            engine.abortCurrentBattle(false);
                        }
                    } catch (Exception e) {
                        Logger.logError("Error while handling another error", e);
                    }
                }
            }
        }

        @Override
        public void onBattleStarted(BattleStartedEvent event) {
            try {
                RobotTestBed.this.onBattleStarted(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattleFinished(BattleFinishedEvent event) {
            try {
                RobotTestBed.this.onBattleFinished(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattleCompleted(BattleCompletedEvent event) {
            try {
                RobotTestBed.this.onBattleCompleted(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattlePaused(BattlePausedEvent event) {
            try {
                RobotTestBed.this.onBattlePaused(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattleResumed(BattleResumedEvent event) {
            try {
                RobotTestBed.this.onBattleResumed(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onRoundStarted(RoundStartedEvent event) {
            try {
                RobotTestBed.this.onRoundStarted(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onRoundEnded(RoundEndedEvent event) {
            try {
                RobotTestBed.this.onRoundEnded(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onTurnStarted(TurnStartedEvent event) {
            try {
                RobotTestBed.this.onTurnStarted(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onTurnEnded(TurnEndedEvent event) {
            try {
                synchronized (RobotTestBed.this) {
                    if (RobotTestBed.this.lastError == null) {
                        RobotTestBed.this.onTurnEnded(event);
                    }
                }
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattleMessage(BattleMessageEvent event) {
            try {
                RobotTestBed.this.onBattleMessage(event);
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }

        @Override
        public void onBattleError(BattleErrorEvent event) {
            try {
                RobotTestBed.this.onBattleError(event);
                Throwable errorInstance = event.getErrorInstance();
                if (errorInstance != null) {
                    handleError(errorInstance);
                }
            } catch (Error | Exception ex) {
                handleError(ex);
            }
        }
    }
}