/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Replaced the ContestantPeerVector, BulletPeerVector, and RobotPeerVector
 *       with plain Vector
 *     - Integration of new render classes placed under the robocode.gfx package
 *     - BattleView is not given via the constructor anymore, but is retrieved
 *       from the RobocodeManager. In addition, the battleView is now allowed to
 *       be null, e.g. if no GUI is available
 *     - Ported to Java 5.0
 *     - Bugfixed sounds that were cut off after first battle
 *     - Changed initialize() to use loadClass() instead of loadRobotClass() if
 *       security is turned off
 *     - Changed the way the TPS is loaded and updated
 *     - Added updateTitle() in order to manage and update the title on the
 *       RobocodeFrame
 *     - Added replay feature
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that has been (re)moved from the robocode.util.Utils class
 *     - Changed so robots die faster graphically when the battles are over
 *     - Changed cleanup to only remove the robot in the robot peers, as the
 *       robot peers themselves are used for replay recording
 *     - Added support for playing background music when the battle is ongoing
 *     - Removed unnecessary catches of NullPointerExceptions
 *     - Added support for setting the initial robot positions on the battlefield
 *     - Removed the showResultsDialog field which is replaced by the
 *       getOptionsCommonShowResults() from the properties
 *     - Simplified the code in the run() method when battle is stopped
 *     - Changed so that stop() makes the current round stop immediately
 *     - Added handling keyboard events thru a KeyboardEventDispather
 *     - Added mouseMoved(), mouseClicked(), mouseReleased(), mouseEntered(),
 *       mouseExited(), mouseDragged(), mouseWheelMoved()
 *     - Changed to take the new JuniorRobot class into account
 *     - When cleaning up robots their static fields are now being cleaned up
 *     - Bugfix: Changed the runRound() so that the robot are painted after
 *       they have made their turn
 *     - The thread handling for unsafe robot loading has been put in an
 *       independent UnsafeLoadRobotsThread class. In addition, the battle
 *       thread is not sharing it's run() method anymore with the
 *       UnsafeLoadRobotsThread, which has now got its own run() method
 *     - The 'running' and 'aborted' flags are now synchronizet towards
 *       'battleMonitor' instead of 'this' object
 *     - Added waitTillRunning() method so another thread can be blocked until
 *       the battle has started running
 *     - Replaced synchronizetList on lists for deathEvent, robots, bullets,
 *       and contestants with a CopyOnWriteArrayList in order to prevent
 *       ConcurrentModificationExceptions when accessing these list via
 *       Iterators using public methods to this class
 *     - The moveBullets() was simplified and moved inside the runRound() method
 *     - The flushOldEvents() method was moved into the runRound() method
 *     - Major bugfix: Two robots running with exactly the same code was getting
 *       different scores. Robots listed before other robots always got a better
 *       score in the end. Hence, the getRobotsAtRandom() method has been added
 *       in order to gain fair play, and this method should be used where robots
 *       are checked and awakened in turn
 *     - Simplified the repainting of the battle
 *     - Bugfix: In wakeupRobots(), only wakeup a robot that is running and alive
 *     - A StatusEvent is now send to all alive robot each turn
 *     Luis Crespo
 *     - Added sound features using the playSounds() method
 *     - Added debug step feature
 *     - Added isRunning()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronizet List and HashMap
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *     Nathaniel Troutman
 *     - Bugfix: In order to prevent memory leaks, the cleanup() method has now
 *       been extended to cleanup all robots, but also all classes that this
 *       class refers to in order to avoid circular references. In addition,
 *       cleanup has been added to the KeyEventHandler
 *     Julian Kent
 *     - Fix: Method for using only nano second precision when using
 *       RobotPeer.wait(0, nanoSeconds) in order to prevent the millisecond
 *       granularity issue, which is typically were coarse compared to the one
 *       with nano seconds 
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode.battle;


import robocode.*;
import robocode.battle.record.*;
import robocode.battlefield.BattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.control.RobotResults;
import robocode.dialog.RobocodeFrame;
import robocode.dialog.RobotButton;
import static robocode.io.Logger.log;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.manager.RobocodeProperties.PropertyListener;
import robocode.peer.*;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.robot.RobotClassManager;
import robocode.peer.robot.RobotStatistics;
import robocode.peer.views.BattleRobotView;
import robocode.peer.views.IBattleBulletView;
import robocode.peer.views.IBattleRobotView;
import robocode.peer.views.IDisplayRobotView;
import robocode.repository.RobotFileSpecification;
import robocode.robotinterfaces.IBasicRobot;
import robocode.security.RobocodeClassLoader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The <code>Battle</code> class is used for controlling a battle.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Titus Chen (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Julian Kent (contributor)
 * @author Pavel Savara (contributor)
 */
public class Battle extends BattleData implements Runnable {

	// Maximum turns to display the battle when battle ended
	private final static int TURNS_DISPLAYED_AFTER_ENDING = 35;

	// Objects we use
	private BattleView battleView;
	private BattleField battleField;
	private BattleManager battleManager;
	private RobocodeManager manager;

	// Battle items
	private Thread battleThread;
	private Object battleMonitor = new Object();
	private volatile boolean running;
	private volatile boolean aborted;

	// Option related items
	private double gunCoolingRate = .1;

	// Inactivity related items
	private int inactiveTurnCount;
	private double inactivityEnergy;
	private long inactivityTime;

	// TPS and FPS related items
	private int desiredTPS = 30;
	private long startTimeThisSec = 0;

	// Turn skip related items
	private int maxSkippedTurns = 30;
	private int maxSkippedTurnsWithIO = 240;
	private String nonDeterministicRobots;
	private boolean deterministic = true;

	// Current round items
	private int numRounds;
	private int roundNum;
	private int turnsThisSec;
	private int framesThisSec;
	private int currentTime;
	private int endTimer;
	private int activeRobots;

	// Results related items
	private boolean exitOnComplete;

	// Results for RobocodeEngine controller
	private BattleSpecification battleSpecification;

	// Robot loading related items
	private Thread unsafeLoadRobotsThread;
	private Object unsafeLoaderMonitor = new Object();
	private boolean unsafeLoaderThreadRunning;
	private boolean robotsLoaded;

	// Replay related items
	private boolean replay;
	private boolean isRecordingEnabled;
	private static BattleRecord battleRecord;
	private RoundRecord currentRoundRecord;
	private TurnRecord currentTurnRecord;

	// Initial robot start positions (if any)
	private double[][] initialRobotPositions;

	// Property listener
	private PropertyListener propertyListener;

	// Key event dispatcher
	private KeyEventHandler keyHandler;

	// Dummy component used to preventing robots in accessing the real source component
	private static Component safeEventComponent;

    // Death events
    private List<IBattleRobotView> deathEvents = new CopyOnWriteArrayList<IBattleRobotView>();

	/**
	 * Battle constructor
	 */
	public Battle(BattleField battleField, RobocodeManager manager) {
		super();

		if (manager.isGUIEnabled()) {
			battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();
			battleView.setBattle(this);
		}
		this.battleField = battleField;
		this.manager = manager;

		battleManager = manager.getBattleManager();

		if (manager.isGUIEnabled()) {
			keyHandler = new KeyEventHandler(this, getDisplayRobots());
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyHandler);
		}
	}

	@Override
	public void finalize() {
		if (keyHandler != null) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyHandler);
		}
	}

	public void setReplay(boolean replay) {
		this.replay = replay;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
	public void run() {
		initialize();

		deterministic = true;
		nonDeterministicRobots = null;

		boolean soundInitialized = false;

		if (manager.isSoundEnabled()) {
			soundInitialized = true;
		}

		roundNum = 0;

		if (manager.isGUIEnabled()) {
			RobocodeFrame frame = manager.getWindowManager().getRobocodeFrame();

			frame.setEnableStopButton(true);
			frame.setEnableRestartButton(true);
			frame.setEnableReplayButton(false);
		}
		isRecordingEnabled = manager.getProperties().getOptionsCommonEnableReplayRecording();

		if (!replay) {
			battleRecord = isRecordingEnabled ? new BattleRecord(battleField, getBattleRobots()) : null;
		}
			
		while (!isAborted() && roundNum < numRounds) {
			updateTitle();
			try {
				setupRound();

				if (replay) {
					// Only run replay for a round if it has been recorded
					if (battleRecord != null && battleRecord.rounds.size() > roundNum) {
						replayRound();
					}
				} else {
					runRound();
				}

				cleanupRound();
			} catch (Exception e) {
				e.printStackTrace();
				log("Exception running a battle: " + e);
			}

			roundNum++;
		}

		if (!replay) {
			for (IBattleRobotView robotView : getBattleRobots()) {
				robotView.getOut().close();
				robotView.getRobotThreadManager().cleanup();
			}
			unsafeLoadRobotsThread.interrupt();

			if (manager.getListener() != null) {
				if (isAborted()) {
					manager.getListener().battleAborted(battleSpecification);
				} else {
					battleManager.sendResultsToListener(this, manager.getListener());
				}
			}
			if (!isAborted() && manager.isGUIEnabled() && manager.getProperties().getOptionsCommonShowResults()) {
				manager.getWindowManager().showResultsDialog();
			}

			battleManager.printResultsData(this);

			if (exitOnComplete) {
				System.exit(0);
			}
		} else {
			// Replay

			if (!isAborted()) {
				if (manager.getProperties().getOptionsCommonShowResults()) {
					RobotResults[] results = battleRecord.rounds.get(battleRecord.rounds.size() - 1).results;

					for (int i = 0; i < getBattleRobots().size(); i++) {
						IBattleRobotView robotView = getBattleRobots().get(i);

						RobotStatistics stats = new RobotStatistics(robotView, results[i]);

						robotView.b_setStatistics(stats);
					}

					manager.getWindowManager().showResultsDialog();
				}
			}
		}

		if (battleView != null) {
			battleView.repaint();
		}

		// The results dialog needs the battle object to be complete, so we
		// won't clean it up just yet, instead the ResultsDialog is responsible
		// for cleaning up the battle when its done with it.
		if (!manager.isGUIEnabled()) {
			cleanup();
		}

		if (soundInitialized) {
			manager.getSoundManager().stopBackgroundMusic();
			manager.getSoundManager().playEndOfBattleMusic();
		}

		if (manager.isGUIEnabled()) {
			RobocodeFrame frame = manager.getWindowManager().getRobocodeFrame();

			frame.setEnableStopButton(false);
			frame.setEnableReplayButton(battleRecord != null);
		}

		// Notify that the battle is over
		//TODO ZAMO synchronizet (battleMonitor)
        {
			running = false;
			battleMonitor.notifyAll();
		}

		// Must be done here, as this method depends on the running state
		updateTitle();
	}

	public void waitTillRunning() {
		//TODO ZAMO synchronizet (battleMonitor)
        {
			while (!running) {
				try {
					battleMonitor.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public void addRobot(RobotClassManager robotClassManager) {
		RobotPeer robotPeer = new RobotPeer(robotClassManager,
				battleManager.getManager().getProperties().getRobotFilesystemQuota());
		TeamPeer teamManager = robotClassManager.getTeamManager();

		if (teamManager != null) {
			teamManager.add(robotPeer);
			addContestant(teamManager);
		} else {
			addContestant(robotPeer);
		}
		robotPeer.setBattle(this);
		robotPeer.getOut();

		int count = 0;

		for (IBattleRobotView otherRobotView : getBattleRobots()) {
			if (otherRobotView.getFullClassNameWithVersion().equals(
					robotPeer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion())) {
				if (count == 0) {
					if (!otherRobotView.isDuplicate()) {
						otherRobotView.setDuplicate(0);
					}
				}
				count++;
			}
		}
		if (count > 0) {
			robotPeer.getBattleView().setDuplicate(count);
		}
        addRobotPeer(robotPeer);
    }

	public void cleanup() {
		for (RobotPeer robotPeer : getRobotPeers()) {
            robotPeer.cleanup();
        }

        cleanupData();

        if (keyHandler != null) {
			keyHandler.cleanup();
			keyHandler = null;
		}

		if (manager != null) {
			RobocodeProperties props = manager.getProperties();

			props.removePropertyListener(propertyListener);
			props = null;
			propertyListener = null;
		}

		battleField = null;
		battleManager = null;
		battleSpecification = null;

		// Request garbage collecting
		for (int i = 4; i >= 0; i--) { // Make sure it is run
			System.gc();
		}
	}

	private void cleanupRound() {
		if (!replay) {
			log("Round " + (roundNum + 1) + " cleaning up.");

			for (IBattleRobotView robotView : getBattleRobots()) {
				robotView.getRobotThreadManager().waitForStop();
				robotView.getRobotStatistics().generateTotals();
			}
		}
	}

	public void generateDeathEvents(RobotPeer r) {
		deathEvents.add(r.getBattleView());
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Thread getBattleThread() {
		return battleThread;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	public long getInactivityTime() {
		return inactivityTime;
	}

	public String getNonDeterministicRobots() {
		return nonDeterministicRobots;
	}

	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Returns a list of all robots in random order. This method is used to gain fair play in Robocode,
	 * so that a robot placed before another robot in the list will not gain any benefit when the game
	 * checks if a robot has won, is dead, etc.
	 * This method was introduced as two equal robots like sample.RamFire got different scores even
	 * though the code was exactly the same.
	 *
	 * @return a list of robots
	 */
	private List<IBattleRobotView> getRobotsAtRandom() {
		int count = getBattleRobots().size();

		List<IBattleRobotView> list = new ArrayList<IBattleRobotView>(count);

		if (count > 0) {
			list.add(getBattleRobots().get(0));

			for (int i = 1; i < count; i++) {
				list.add((int) (Math.random() * i + 0.5), getBattleRobots().get(i));
			}
		}

		return list;
	}

	public void setOptions() {
		setDesiredTPS(manager.getProperties().getOptionsBattleDesiredTPS());
		if (battleView != null) {
			battleView.setDisplayOptions();
		}
	}

	public void setDesiredTPS(int desiredTPS) {
		this.desiredTPS = desiredTPS;
	}

	public void initialize() {
        // Notify that the battle is now running
        //TODO ZAMO synchronizet (battleMonitor)
        {
            running = true;
            battleMonitor.notifyAll();
        }

        if (battleView != null) {
            battleView.repaint();
        }

        if (manager.isSoundEnabled()) {
            manager.getSoundManager().playBackgroundMusic();
        }

		setOptions();

		RobocodeProperties props = manager.getProperties();

		desiredTPS = props.getOptionsBattleDesiredTPS();

		propertyListener = props.new PropertyListener() {
			@Override
			public void desiredTpsChanged(int tps) {
				desiredTPS = tps;
			}
		};

		props.addPropertyListener(propertyListener);

		// Starting loader thread
		ThreadGroup unsafeThreadGroup = new ThreadGroup("Robot Loader Group");

		unsafeThreadGroup.setDaemon(true);
		unsafeThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
		unsafeLoadRobotsThread = new UnsafeLoadRobotsThread();
		manager.getThreadManager().setRobotLoaderThread(unsafeLoadRobotsThread);
		unsafeLoadRobotsThread.start();

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().clearRobotButtons();
		}

		for (IBattleRobotView robotView : getBattleRobots()) {
			robotView.b_preInitialize();
			if (manager.isGUIEnabled()) {
				manager.getWindowManager().getRobocodeFrame().addRobotButton(
						new RobotButton(manager.getRobotDialogManager(), robotView.getDisplayView()));
			}
		}
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().validate();
		}

		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
        //TODO ZAMO synchronizet (getBattleRobots())
        {
			for (IBattleRobotView robotView : getBattleRobots()) {
				try {
					Class<?> c;

					RobotClassManager classManager = robotView.getRobotClassManager();
					String className = classManager.getFullClassName();

					RobocodeClassLoader classLoader = classManager.getRobotClassLoader();

					if (RobotClassManager.isSecutityOn()) {
						c = classLoader.loadRobotClass(className, true);
					} else {
						c = classLoader.loadClass(className);
					}

					classManager.setRobotClass(c);

					robotView.getRobotFileSystemManager().initializeQuota();

					RobotFileSpecification robotFileSpecification = classManager.getRobotSpecification();

					robotView.getPeer().setInfo(robotFileSpecification);
					initializeRobotPosition(robotView);

					if (battleView != null && !replay) {
						battleView.update();
					}
				} catch (Throwable e) {
					robotView.getOut().println("SYSTEM: Could not load " + robotView.getName() + " : " + e);
					e.printStackTrace(robotView.getOut());
				}
			}
		}
	}

	public boolean isDeterministic() {
		return deterministic;
	}

	public boolean isExitOnComplete() {
		return exitOnComplete;
	}

    //TODO ZAMO synchronizet  
    public boolean isRobotsLoaded() {
		return robotsLoaded;
	}

	public void printSystemThreads() {
		Thread systemThreads[] = new Thread[256];

		battleThread.getThreadGroup().enumerate(systemThreads, false);

		log("Threads: ------------------------");
		for (Thread thread : systemThreads) {
			if (thread != null) {
				log(thread.getName());
			}
		}
	}

	public void resetInactiveTurnCount(double energyLoss) {
		if (energyLoss < 0) {
			return;
		}
		inactivityEnergy += energyLoss;
		while (inactivityEnergy >= 10) {
			inactivityEnergy -= 10;
			inactiveTurnCount = 0;
		}
	}

    private boolean roundOver;

    private long robotStartTime;
    private long turnStartTime;
    private long frameStartTime;

    private int currentRobotMillis;
    private int totalRobotMillisThisSec;
    private int totalFrameMillisThisSec;
    private int totalTurnMillisThisSec;
    private float estFrameTimeThisSec;
    private float estimatedFPS;
    private int estimatedTurnMillisThisSec;
    private int delay;
    private boolean resetThisSec;

    public void runRound() {
		log("Let the games begin!");

		roundOver = false;
		endTimer = 0;
		currentTime = 0;
		inactiveTurnCount = 0;
		turnsThisSec = 0;
		framesThisSec = 0;
		currentRobotMillis = 0;
		totalRobotMillisThisSec = 0;
		totalFrameMillisThisSec = 0;
		estimatedFPS = 0;
		delay = 0;

		resetThisSec = true;

		if (isRecordingEnabled) {
			currentRoundRecord = new RoundRecord();
		}

		battleManager.startNewRound();

        boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();
        
        if (!minimizedMode) {
			battleView.update();
		}

		while (!roundOver) {
            runTurn();
		}

        recordRound();

        clearBullets();
	}


    public void replayRound() {
		log("Replay started");

		roundOver = false;

		endTimer = 0;
		currentTime = 0;
		turnsThisSec = 0;
		framesThisSec = 0;
		totalFrameMillisThisSec = 0;
		estimatedFPS = 0;
		delay = 0;
		resetThisSec = true;

		battleManager.startNewRound();

        boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

        if (!minimizedMode) {
			battleView.update();
		}

		while (!(roundOver || isAborted())) {
            replayTurn();
		}

		clearBullets();
	}

    private void replayTurn() {

        if (shouldPause() && !battleManager.shouldStep()) {
            resetThisSec = true;
            return;
        }

        // Next turn is starting
        turnStartTime = System.currentTimeMillis();

        resetSec();

        roundOver = replayRecord();

        currentTime++;
        turnsThisSec++;

        // Set flag indication if we are running in "minimized mode"
        boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

        // Store the start time before the frame update
        frameStartTime = System.currentTimeMillis();

        // Paint current battle frame
        displayTurn(minimizedMode);

        measureTime(minimizedMode);

        // Update title when second has passed
        if (resetThisSec) {
            updateTitle();
        }
    }

    private void runTurn() {
        if (shouldPause() && !battleManager.shouldStep()) {
            resetThisSec = true;
            return;
        }

        // Next turn is starting
        turnStartTime = System.currentTimeMillis();

        resetSec();

        lockRobots();
        try{
            cleanRobotEvents();

            currentTime++;
            turnsThisSec++;

            updateBullets();

            moveRobots();

            handleDeathEvents();

            performScans();

            deathEvents.clear();

            roundOver = checkBattleOver();

            inactiveTurnCount++;

            computeActiveRobots();

            recordTurn();

            addStatusEvent();

            // Store the robot start time
            robotStartTime = System.currentTimeMillis();
        }
        finally {
            unlockRobots();
        }

        // Robot time!
        wakeupRobots();

        // Calculate the time spend on the robots
        currentRobotMillis = (int) (System.currentTimeMillis() - robotStartTime);

        // Set flag indication if we are running in "minimized mode"
        boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

        // Paint current battle frame
        displayTurn(minimizedMode);

        //statistics
        measureTime(minimizedMode);

        // Update title when second has passed
        if (resetThisSec) {
            updateTitle();
        }
    }

    private void lockRobots(){
        for (IBattleRobotView robotView : getBattleRobots()) {
            robotView.lockWrite();
        }
    }

    private void unlockRobots(){
        for (IBattleRobotView robotView : getBattleRobots()) {
            robotView.unlockWrite();
        }
    }

    private void resetSec() {
        if (resetThisSec) {
            resetThisSec = false;

            startTimeThisSec = turnStartTime;

            turnsThisSec = 0;
            framesThisSec = 0;

            totalRobotMillisThisSec = 0;
            totalFrameMillisThisSec = 0;
        }
    }

    private void addStatusEvent() {
        // Add status events for the current turn to all robots that are alive
        for (IBattleRobotView robotView : getBattleRobots()) {
            if (!robotView.isDead()) {
                robotView.getBattleEventManager().add(new StatusEvent(robotView));
            }
        }
    }

    private void cleanRobotEvents() {
        // New turn: flush any old events
        for (IBattleRobotView robotView : getBattleRobots()) {
            robotView.getBattleEventManager().clearOld(currentTime - 1);
        }
    }

    private void moveRobots() {
        boolean zap = (inactiveTurnCount > inactivityTime);

        // Move all bots
        for (IBattleRobotView robotView : getRobotsAtRandom()) {
            if (!robotView.isDead()) {
                robotView.b_update(getBattleRobots());
            }
            if ((zap || isAborted()) && !robotView.isDead()) {
                if (isAborted()) {
                    robotView.b_zap(5);
                } else {
                    robotView.b_zap(.1);
                }
            }
        }
    }

    private void updateBullets() {
        //gather bullets
        for (IBattleRobotView robotView : getBattleRobots()) {

            BulletPeer bullet = robotView.b_getCurrentBullet();
            if (bullet != null) {
                robotView.b_adjustGunHeat(Rules.getGunHeat(bullet.getPower()));

                addBullet(bullet);
                robotView.b_setCurrentBullet(null);
            }
        }

        // Update bullets
        for (IBattleBulletView b : getBattleBullets()) {
            b.update(getBattleRobots(), getBattleBullets());
        }

        //remove dead bullets
        for (IBattleBulletView b : getBattleBullets()) {
            if (b.getState() == BulletPeer.STATE_INACTIVE){
                removeBullet((BulletPeer)b);
            }
        }
    }

    private void measureTime(boolean minimizedMode) {
        // Calculate the total time spend on robots this second
        totalRobotMillisThisSec += currentRobotMillis;

        // Calculate the total time used for the frame update
        totalFrameMillisThisSec += (int) (System.currentTimeMillis() - turnStartTime) - currentRobotMillis;

        // Calculate the total turn time this second
        totalTurnMillisThisSec = max(1, totalRobotMillisThisSec + totalFrameMillisThisSec);

        // Estimate the time remaining this second to spend on frame updates
        estFrameTimeThisSec = max(0, 1000 - desiredTPS * totalRobotMillisThisSec / turnsThisSec);

        // Estimate the possible FPS based on the estimated frame time
        estimatedFPS = max(1, framesThisSec * estFrameTimeThisSec / totalFrameMillisThisSec);

        // Estimate the time that will be used on the total turn this second
        estimatedTurnMillisThisSec = desiredTPS * totalTurnMillisThisSec / turnsThisSec;

        // Calculate delay needed for keeping the desired TPS (Turns Per Second)
        if (endTimer >= TURNS_DISPLAYED_AFTER_ENDING || minimizedMode) {
            delay = 0;
        } else {
            delay = (estimatedTurnMillisThisSec >= 1000) ? 0 : (1000 - estimatedTurnMillisThisSec) / desiredTPS;
        }

        // Set flag for if the second has passed
        resetThisSec = (System.currentTimeMillis() - startTimeThisSec) >= 1000;

        // Check if we must limit the TPS
        if (!(resetThisSec || minimizedMode)) {
            resetThisSec = ((desiredTPS - turnsThisSec) == 0);
        }

        // Delay to match desired TPS
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // Set the thread status back to being interrupted
                Thread.currentThread().interrupt();
            }
        }
    }

    private void recordRound() {
        if (isRecordingEnabled) {
            List<RobotPeer> orderedRobots = new ArrayList<RobotPeer>(getRobotPeers());

            Collections.sort(orderedRobots);

            RobotResults results[] = new RobotResults[getBattleRobots().size()];

            int rank;

            for (int i = 0; i < getBattleRobots().size(); i++) {
                IBattleRobotView r = orderedRobots.get(i).getBattleView();

                for (rank = 0; rank < getBattleRobots().size(); rank++) {
                    if (getBattleRobots().get(rank) == r) {
                        break;
                    }
                }
                results[rank] = r.getRobotStatistics().getResults(i + 1, isRunning());
            }

            currentRoundRecord.results = results;
            battleRecord.rounds.add(currentRoundRecord);
        }
    }

    private void displayTurn(boolean minimizedMode) {
        if (!(isAborted() || endTimer >= TURNS_DISPLAYED_AFTER_ENDING || minimizedMode)) {
            // Update the battle view if the frame has not been painted yet this second
            // or if it's time to paint the next frame
            if ((estimatedFPS * turnsThisSec / desiredTPS) >= framesThisSec) {
                battleView.update();
                framesThisSec++;
            }

            playSounds();
        }
    }

    private boolean replayRecord() {
        IBattleRobotView robot;
        BulletPeer bullet;
        RoundRecord roundRecord = battleRecord.rounds.get(roundNum);
        TurnRecord turnRecord = roundRecord.turns.get(currentTime);

        clearBullets();
        for (IBattleRobotView robotView : getBattleRobots()) {
            robotView.b_setState(RobotPeerStatus.STATE_DEAD);
        }
        for (RobotRecord rr : turnRecord.robotStates) {
            robot = getBattleRobots().get(rr.index);
            robot.b_setRecord(rr);
        }
        for (BulletRecord br : turnRecord.bulletStates) {
            robot = getBattleRobots().get(br.owner);
            if (br.state == BulletPeer.STATE_EXPLODED) {
                bullet = new ExplosionPeer(robot, this, br);
            } else {
                bullet = new BulletPeer(robot, this, br);
            }
            addBullet(bullet);
        }
        return currentTime >= roundRecord.turns.size();
    }

    private void recordTurn() {
        if (isRecordingEnabled && endTimer < TURNS_DISPLAYED_AFTER_ENDING) {
            currentTurnRecord = new TurnRecord();
            currentRoundRecord.turns.add(currentTurnRecord);

            currentTurnRecord.robotStates = new ArrayList<RobotRecord>();

            IBattleRobotView rp;
            for (int i = 0; i < getBattleRobots().size(); i++) {
                rp = getBattleRobots().get(i);
                if (!rp.isDead()) {
                    RobotRecord rr = new RobotRecord(i, rp);

                    currentTurnRecord.robotStates.add(rr);
                }
            }

            currentTurnRecord.bulletStates = new ArrayList<BulletRecord>();
            for (IBattleBulletView bp : getBattleBullets()) {
                IBattleRobotView owner = bp.getOwner();

                for (int i = 0; i < getBattleRobots().size(); i++) {
                    if (getBattleRobots().get(i) == owner) {
                        BulletRecord br = new BulletRecord(i, bp);

                        currentTurnRecord.bulletStates.add(br);
                        break;
                    }
                }
            }
        }
    }

	private boolean shouldPause() {
		if (battleManager.isPaused() && !isAborted()) {
			updateTitle();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
			return true;
		}
		return false;
	}

	private void computeActiveRobots() {
		int ar = 0;

		// Compute active robots
		for (IBattleRobotView robotView : getBattleRobots()) {
			if (!robotView.isDead()) {
				ar++;
			}
		}
		setActiveRobots(ar);
	}

	private void wakeupRobots() {
		// Wake up all robot threads
		//TODO ZAMO synchronizet (getBattleRobots())
        {
			for (IBattleRobotView r : getRobotsAtRandom()) {
				if (!r.isRunning()) {
					continue;
				}
				// This call blocks until the
				// robot's thread actually wakes up.
				r.b_wakeup();

				if (r.isAlive()) {
                    //TODO ZAMO                     synchronizet (r)
                    {
						// It's quite possible for simple robots to
						// complete their processing before we get here,
						// so we test if the robot is already asleep.

						if (!r.isSleeping()) {
							try {
								long waitTime = manager.getCpuManager().getCpuConstant();

								int millisWait = (int) (waitTime / 1000000);

								for (int i = millisWait; i > 0 && !r.isSleeping(); i--) {
									r.wait(0, 999999);
								}
								if (!r.isSleeping()) {
									r.wait(0, (int) (waitTime % 1000000));
								}
							} catch (InterruptedException e) {
								// ?
								log("Wait for " + r + " interrupted.");
							}
						}
					}
					if (r.isSleeping() || !r.isRunning()) {
						r.b_setSkippedTurns(0);
					} else {
						r.b_setSkippedTurns(r.getSkippedTurns() + 1);

						r.getBattleEventManager().add(new SkippedTurnEvent());

						// Actually, Robocode is never deterministic due to Robots
						// using calls to Math.random()... but the point is,
						// at least one robot skipped a turn.
						deterministic = false;
						if (nonDeterministicRobots == null) {
							nonDeterministicRobots = r.getName();
						} else if (nonDeterministicRobots.indexOf(r.getName()) == -1) {
							nonDeterministicRobots += "," + r.getName();
						}
						if ((!r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurns))
								|| (r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurnsWithIO))) {
							r.getOut().println(
									"SYSTEM: " + r.getName()
									+ " has not performed any actions in a reasonable amount of time.");
							r.getOut().println("SYSTEM: No score will be generated.");
							r.getRobotStatistics().setInactive();
							r.getRobotThreadManager().forceStop();
						}
					}
				}
			}
		}
	}

	private void handleDeathEvents() {
		if (deathEvents.size() > 0) {
			for (IBattleRobotView robotView : getBattleRobots()) {
				if (!robotView.isDead()) {
					for (IBattleRobotView de : deathEvents) {
						robotView.getBattleEventManager().add(new RobotDeathEvent(de.getName()));
						if (robotView.getTeamPeer() == null || robotView.getTeamPeer() != de.getTeamPeer()) {
							robotView.getRobotStatistics().scoreSurvival();
						}
					}
				}
			}
		}
		// Compute scores for dead robots
		for (IBattleRobotView de : deathEvents) {
			if (de.getTeamPeer() == null) {
				de.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(de));
			} else {
				boolean teammatesalive = false;

				for (IBattleRobotView tm : getBattleRobots()) {
					if (tm.getTeamPeer() == de.getTeamPeer() && (!tm.isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					de.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(de));
				}
			}
		}
	}

	private void performScans() {
		// Perform scans, handle messages
		for (IBattleRobotView robotView : getRobotsAtRandom()) {
			if (!robotView.isDead()) {
				if (robotView.getScan()) {
					// Enter scan
					System.err.flush();

					robotView.b_scan(getBattleRobots());
					// Exit scan
					robotView.setScan(false);
				}

				if (robotView.getBattleMessageManager() != null) {
					List<MessageEvent> messageEvents = robotView.getBattleMessageManager().getMessageEvents();

					for (MessageEvent me : messageEvents) {
						robotView.getBattleEventManager().add(me);
					}
					messageEvents.clear();
				}
			}
		}
	}

	private boolean checkBattleOver() {
		boolean battleOver = false;

		// Check game over
		if (isAborted() || oneTeamRemaining()) {
			if (endTimer == 0) {
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				for (IBattleRobotView robotView : getRobotsAtRandom()) {
					if (!robotView.isDead()) {
						if (!robotView.isWinner()) {
							robotView.getRobotStatistics().scoreLastSurvivor();
							robotView.b_setWinner(true);
                            robotView.getOut().println("SYSTEM: " + robotView.getName() + " wins the round.");
                            robotView.getBattleEventManager().add(new WinEvent());
							if (robotView.getTeamPeer() != null) {
								if (robotView.isTeamLeader()) {
									leaderFirsts = true;
								} else {
									winningTeam = robotView.getTeamPeer();
								}
							}
						}
					}
				}
				if (!leaderFirsts && winningTeam != null) {
					(winningTeam.getTeamLeader()).getBattleView().getRobotStatistics().scoreFirsts();
				}
			}

			if (endTimer > 4 * 30) {
				for (IBattleRobotView robotView : getBattleRobots()) {
					if (!robotView.isDead()) {
						robotView.b_setHalt(true);
					}
				}
			}

			endTimer++;
			if (endTimer > 5 * 30) {
				battleOver = true;
			}
		}
		return battleOver;
	}

	public int getActiveContestantCount(IBattleRobotView peer) {
		int count = 0;

		for (IContestantPeer c : getContestants()) {
			if (c instanceof RobotPeer && !((RobotPeer) c).getBattleView().isDead()) {
				count++;
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				for (RobotPeer r : (TeamPeer) c) {
					if (!r.getBattleView().isDead()) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	public void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	public void setBattleThread(Thread newBattleThread) {
		battleThread = newBattleThread;
	}

	public void setCurrentTime(int newCurrentTime) {
		currentTime = newCurrentTime;
	}

	void setDeterministic(boolean newDeterministic) {
		deterministic = newDeterministic;
	}

	public void setExitOnComplete(boolean newExitOnComplete) {
		exitOnComplete = newExitOnComplete;
	}

	public void setGunCoolingRate(double newGunCoolingRate) {
		gunCoolingRate = newGunCoolingRate;
	}

	public void setInactivityTime(long newInactivityTime) {
		inactivityTime = newInactivityTime;
	}

	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public void setProperties(BattleProperties battleProperties) {
		try {
			setNumRounds(battleProperties.getNumRounds());
			setGunCoolingRate(battleProperties.getGunCoolingRate());
			setInactivityTime(battleProperties.getInactivityTime());
			setInitialPositions(battleProperties.getInitialPositions());
		} catch (Exception e) {
			log("Exception setting battle properties", e);
		}
	}

    //TODO ZAMO synchronizet
    public void setRobotsLoaded(boolean newRobotsLoaded) {
		robotsLoaded = newRobotsLoaded;
	}

	public void setupRound() {
		log("----------------------");
		log("Round " + (roundNum + 1) + " initializing..", false);
		currentTime = 0;

		setRobotsLoaded(false);
		while (!isUnsafeLoaderThreadRunning()) {
			// waiting for loader to start
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}

		for (IBattleRobotView robotView : getBattleRobots()) {
			if (roundNum > 0) {
				robotView.b_preInitialize();
			} // fake dead so robot won't display

			robotView.getOut().println("=========================");
			robotView.getOut().println("Round " + (roundNum + 1) + " of " + numRounds);
			robotView.getOut().println("=========================");
		}

		// Notifying loader
		//TODO ZAMO synchronizet (unsafeLoaderMonitor) 
        {
			unsafeLoaderMonitor.notifyAll();
		}
		while (!isRobotsLoaded()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}

		for (IBattleRobotView robotView : getBattleRobots()) {
			if (robotView.getRobotClassManager().getClassNameManager().getFullPackage() != null
					&& robotView.getRobotClassManager().getClassNameManager().getFullPackage().length() > 18) {
				robotView.getOut().println("SYSTEM: Your package name is too long.  16 characters maximum please.");
				robotView.getOut().println("SYSTEM: Robot disabled.");
				robotView.b_setEnergy(0);
			}
			if (robotView.getRobotClassManager().getClassNameManager().getShortClassName().length() > 35) {
				robotView.getOut().println("SYSTEM: Your classname is too long.  32 characters maximum please.");
				robotView.getOut().println("SYSTEM: Robot disabled.");
				robotView.b_setEnergy(0);
			}
		}

		activeRobots = getBattleRobots().size();

		if (!replay) {
			manager.getThreadManager().reset();

			// Turning on robots
			for (IBattleRobotView robotView : getRobotsAtRandom()) {
				manager.getThreadManager().addThreadGroup(robotView.getRobotThreadManager().getThreadGroup(), (RobotPeer)robotView.getPeer());
				long waitTime = min(300 * manager.getCpuManager().getCpuConstant(), 10000000000L);

				//TODO ZAMO synchronizet (robotView)
                {
					try {
						log(".", false);

						// Add StatusEvent for the first turn
						robotView.getBattleEventManager().add(new StatusEvent(robotView));

						// Start the robot thread
						robotView.getRobotThreadManager().start();

						// Wait for the robot to go to sleep (take action)
						robotView.wait(waitTime / 1000000, (int) (waitTime % 1000000));

					} catch (InterruptedException e) {
						log("Wait for " + robotView + " interrupted.");
					}
				}
				if (!robotView.isSleeping()) {
					log("\n" + robotView.getName() + " still has not started after " + (waitTime / 100000) + " ms... giving up.");
				}
			}
		}

		log("");
	}

	public void stop() {
		//TODO ZAMO synchronizet (battleMonitor) 
        {
			// Return immediately if the battle is not running
			if (!running) {
				return;
			}

			// Notify that the battle is aborted
			aborted = true;
			battleMonitor.notifyAll();

			// Adjust the desired TPS temporary to maximum rate to stop the battle as quickly as possible
			int savedTPS = desiredTPS;			

			desiredTPS = 10000;

			// Wait till the battle is not running anymore
			while (running) {
				try {
					battleMonitor.wait();
				} catch (InterruptedException e) {
					break;
				}
			}

			// Restore the desired TPS
			desiredTPS = savedTPS;
		}
	}

	public void unsafeLoadRobots() {
		while (true) {
			// Loader waiting
			//TODO ZAMO synchronizet (unsafeLoaderMonitor) 
            {
				try {
					setUnsafeLoaderThreadRunning(true);
					unsafeLoaderMonitor.wait();
				} catch (InterruptedException e) {}
			}
			// Loader awake
			if (roundNum >= numRounds || isAborted()) {
				// Robot loader thread terminating
				return;
			}
			// Loading robots
			for (IBattleRobotView robotView : getBattleRobots()) {
				robotView.getPeer().setRobot(null);
				Class<?> robotClass = null;

				try {
					manager.getThreadManager().setLoadingRobot((RobotPeer)robotView.getPeer());
					robotClass = robotView.getRobotClassManager().getRobotClass();
					if (robotClass == null) {
						robotView.getOut().println("SYSTEM: Skipping robot: " + robotView.getName());
						continue;
					}
					IBasicRobot bot = (IBasicRobot) robotClass.newInstance();

					robotView.getPeer().setRobot(bot);
				} catch (IllegalAccessException e) {
					robotView.getOut().println("SYSTEM: Unable to instantiate this robot: " + e);
					robotView.getOut().println("SYSTEM: Is your constructor marked public?");
				} catch (Throwable e) {
					robotView.getOut().println("SYSTEM: An error occurred during initialization of "
							+ robotView.getRobotClassManager());
					robotView.getOut().println("SYSTEM: " + e);
					e.printStackTrace(robotView.getOut());
				}
				if (roundNum > 0) {
					initializeRobotPosition(robotView);
				}
			} // for
			manager.getThreadManager().setLoadingRobot(null);
			setRobotsLoaded(true);
		}
	}

	private void setInitialPositions(String initialPositions) {
		initialRobotPositions = null;

		if (initialPositions == null || initialPositions.trim().length() == 0) {
			return;
		}

		List<String> positions = new ArrayList<String>();

		Pattern pattern = Pattern.compile("([^,(]*[(][^)]*[)])?[^,]*,?");
		Matcher matcher = pattern.matcher(initialPositions);

		while (matcher.find()) {
			String pos = matcher.group();

			if (pos.length() > 0) {
				positions.add(pos);
			}
		}

		if (positions.size() == 0) {
			return;
		}

		initialRobotPositions = new double[positions.size()][3];

		String[] coords;
		double x, y, heading;

		for (int i = 0; i < positions.size(); i++) {
			coords = positions.get(i).split(",");

			x = BattleRobotView.WIDTH + random() * (battleField.getWidth() - 2 * BattleRobotView.WIDTH);
			y = BattleRobotView.HEIGHT + random() * (battleField.getHeight() - 2 * BattleRobotView.HEIGHT);
			heading = 2 * PI * random();

			int len = coords.length;

			if (len >= 1) {
				try {
					x = Double.parseDouble(coords[0].replaceAll("[\\D]", ""));
				} catch (NumberFormatException e) {}

				if (len >= 2) {
					try {
						y = Double.parseDouble(coords[1].replaceAll("[\\D]", ""));
					} catch (NumberFormatException e) {}

					if (len >= 3) {
						try {
							heading = Math.toRadians(Double.parseDouble(coords[2].replaceAll("[\\D]", "")));
						} catch (NumberFormatException e) {}
					}
				}
			}
			initialRobotPositions[i][0] = x;
			initialRobotPositions[i][1] = y;
			initialRobotPositions[i][2] = heading;
		}
	}

	private void initializeRobotPosition(IBattleRobotView robot) {
		if (initialRobotPositions != null) {
			int index = getBattleRobots().indexOf(robot);

			if (index >= 0 && index < initialRobotPositions.length) {
				double[] pos = initialRobotPositions[index];

				robot.b_initialize(pos[0], pos[1], pos[2], getBattleRobots());
				if (validSpot(robot)) {
					return;
				}
			}
		}

		double x, y, heading;

		for (int j = 0; j < 1000; j++) {
			x = BattleRobotView.WIDTH + random() * (battleField.getWidth() - 2 * BattleRobotView.WIDTH);
			y = BattleRobotView.HEIGHT + random() * (battleField.getHeight() - 2 * BattleRobotView.HEIGHT);
			heading = 2 * PI * random();

			robot.b_initialize(x, y, heading, getBattleRobots());

			if (validSpot(robot)) {
				break;
			}
		}
	}

	private boolean validSpot(IBattleRobotView robotView) {
		robotView.b_updateBoundingBox();
		for (IBattleRobotView otherRobotView : getBattleRobots()) {
			if (otherRobotView != null && otherRobotView != robotView) {
				if (robotView.getBoundingBox().intersects(otherRobotView.getBoundingBox())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the activeRobots.
	 * 
	 * @return Returns a int
	 */

    //TODO ZAMO synchronizet  
    public int getActiveRobots() {
		return activeRobots;
	}

	private boolean oneTeamRemaining() {
		if (getActiveRobots() <= 1) {
			return true;
		}

		boolean found = false;
		TeamPeer currentTeam = null;

		for (IBattleRobotView robotView : getBattleRobots()) {
			if (!robotView.isDead()) {
				if (!found) {
					found = true;
					currentTeam = robotView.getTeamPeer();
				} else {
					if (currentTeam == null && robotView.getTeamPeer() == null) {
						return false;
					}
					if (currentTeam != robotView.getTeamPeer()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Sets the activeRobots.
	 * 
	 * @param activeRobots The activeRobots to set
	 */
    //TODO ZAMO synchronizet  
    private void setActiveRobots(int activeRobots) {
		this.activeRobots = activeRobots;
	}

	/**
	 * Gets the roundNum.
	 * 
	 * @return Returns a int
	 */
	public int getRoundNum() {
		return roundNum;
	}

	/**
	 * Sets the roundNum.
	 * 
	 * @param roundNum The roundNum to set
	 */
	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	/**
	 * Gets the unsafeLoaderThreadRunning.
	 * 
	 * @return Returns a boolean
	 */
    //TODO ZAMO synchronizet
    public boolean isUnsafeLoaderThreadRunning() {
		return unsafeLoaderThreadRunning;
	}

	/**
	 * Sets the unsafeLoaderThreadRunning.
	 * 
	 * @param unsafeLoaderThreadRunning The unsafeLoaderThreadRunning to set
	 */

    //TODO ZAMO synchronizet
    public void setUnsafeLoaderThreadRunning(boolean unsafeLoaderThreadRunning) {
		this.unsafeLoaderThreadRunning = unsafeLoaderThreadRunning;
	}

	/**
	 * Gets the battleSpecification.
	 * 
	 * @return Returns a BattleSpecification
	 */
	public BattleSpecification getBattleSpecification() {
		return battleSpecification;
	}

	/**
	 * Sets the battleSpecification.
	 * 
	 * @param battleSpecification The battleSpecification to set
	 */
	public void setBattleSpecification(BattleSpecification battleSpecification) {
		this.battleSpecification = battleSpecification;
	}

	/**
	 * Gets the manager.
	 * 
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

	/**
	 * Plays sounds.
	 */
	private void playSounds() {
		if (manager.isSoundEnabled()) {
			for (IBattleBulletView bp : getBattleBullets()) {
				if (bp.getFrame() == 0) {
					manager.getSoundManager().playBulletSound(bp, battleField.getWidth());
				}
			}

			boolean playedRobotHitRobot = false;

			for (IBattleRobotView rp : getBattleRobots()) {
				// Make sure that robot-hit-robot events do not play twice (one per colliding robot)
				if (rp.getState() == RobotPeerStatus.STATE_HIT_ROBOT) {
					if (playedRobotHitRobot) {
						continue;
					}
					playedRobotHitRobot = true;
				}

				manager.getSoundManager().playRobotSound(rp);
			}
		}
	}

	/**
	 * Informs on whether the battle is running or not.
	 * 
	 * @return true if the battle is running, false otherwise
	 */
	public boolean isRunning() {
        //TODo ZAMo synchronizet (battleMonitor)
        {
			return running;
		}
	}

	/**
	 * Informs on whether the battle is aborted or not.
	 * 
	 * @return true if the battle is aborted, false otherwise
	 */
	private boolean isAborted() {
        //TODo ZAMO synchronizet (battleMonitor) 
        {
			return aborted;
		}
	}

	private void updateTitle() {
		if (battleView == null) {
			return;
		}

		StringBuffer title = new StringBuffer("Robocode");

		if (isRunning()) {
			title.append(": ");

			if (currentTime == 0) {
				title.append("Starting round");
			} else {
				title.append(replay ? "Replaying round " : "Round ");
				title.append(roundNum + 1).append(" of ").append(numRounds);

				if (!battleManager.isPaused()) {
					boolean dispTps = battleView.isDisplayTPS();
					boolean dispFps = battleView.isDisplayFPS();

					if (dispTps | dispFps) {
						title.append(" (");

						if (dispTps) {
							title.append(turnsThisSec).append(" TPS");
						}
						if (dispTps & dispFps) {
							title.append(", ");
						}
						if (dispFps) {
							title.append(framesThisSec).append(" FPS");
						}
						title.append(')');
					}
				}
			}
		}
		if (battleManager.isPaused()) {
			title.append(" (paused)");
		}
		battleView.setTitle(title.toString());
	}

	public void mouseClicked(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotView robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseClickedEvent(mirroredMouseEvent(e)));
                }
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseEnteredEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}

	public void mouseExited(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseExitedEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}

	public void mousePressed(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MousePressedEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}

	public void mouseReleased(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseReleasedEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}

	public void mouseMoved(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseMovedEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}
	
	public void mouseDragged(final MouseEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseDraggedEvent(mirroredMouseEvent(e)));
                }
            }
        }
	}

	public void mouseWheelMoved(final MouseWheelEvent e) {
        if (isRunning()) {
            for (IDisplayRobotView robotPeer : getDisplayRobots()) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                    robotPeer.onInteractiveEvent(new MouseWheelMovedEvent(mirroredMouseWheelEvent(e)));
                }
            }
        }
	}

	// Returns a dummy component used to preventing robots in accessing the real source component
	private static Component getSafeEventComponent() {
		if (safeEventComponent == null) {
			safeEventComponent = new Label();
		}
		return safeEventComponent;
	}
	
	private MouseEvent mirroredMouseEvent(final MouseEvent e) {
		double scale;

		if (battleView.getWidth() < battleField.getWidth() || battleView.getHeight() < battleField.getHeight()) {
			scale = min((double) battleView.getWidth() / battleField.getWidth(),
					(double) battleView.getHeight() / battleField.getHeight());
		} else {
			scale = 1;
		}

		double dx = (battleView.getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (battleView.getHeight() - scale * battleField.getHeight()) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (battleField.getHeight() - (e.getY() - dy) / scale + 0.5);

		return new MouseEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	private MouseWheelEvent mirroredMouseWheelEvent(final MouseWheelEvent e) {
		double scale;

		if (battleView.getWidth() < battleField.getWidth() || battleView.getHeight() < battleField.getHeight()) {
			scale = min((double) battleView.getWidth() / battleField.getWidth(),
					(double) battleView.getHeight() / battleField.getHeight());
		} else {
			scale = 1;
		}

		double dx = (battleView.getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (battleView.getHeight() - scale * battleField.getHeight()) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (battleField.getHeight() - (e.getY() - dy) / scale + 0.5);

		return new MouseWheelEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation()); 
	}

	private final class KeyEventHandler implements KeyEventDispatcher {
		private Battle battle;
		private List<IDisplayRobotView> robots;

		public KeyEventHandler(Battle battle, List<IDisplayRobotView> robots) {
			this.battle = battle;
			this.robots = robots;
		}

		public boolean dispatchKeyEvent(KeyEvent e) {
			if (battle != null && battle.isRunning()) {
                for (IDisplayRobotView robotPeer : robots) {
                    if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
                        KeyEvent clone = cloneKeyEvent(e);
                        switch (e.getID()) {
                        case KeyEvent.KEY_TYPED:
                            robotPeer.onInteractiveEvent(new KeyTypedEvent(clone));
                            break;

                        case KeyEvent.KEY_PRESSED:
                            robotPeer.onInteractiveEvent(new KeyPressedEvent(clone));
                            break;

                        case KeyEvent.KEY_RELEASED:
                            robotPeer.onInteractiveEvent(new KeyReleasedEvent(clone));
                            break;
                        }
                    }
                }
			}
			return false;
		}

		private KeyEvent cloneKeyEvent(final KeyEvent e) {
			return new KeyEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getKeyCode(),
					e.getKeyChar(), e.getKeyLocation());
		}

		public void cleanup() {
			robots = null;
			battle = null;
		}
	}


	private class UnsafeLoadRobotsThread extends Thread {

		public UnsafeLoadRobotsThread() {
			super(new ThreadGroup("Robot Loader Group"), "Robot Loader");
			setDaemon(true);
		}

		@Override
		public void run() {
			// Load robots
			unsafeLoadRobots();
		}
	}
}
