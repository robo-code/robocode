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
 *     - Code cleanup & optimizations
 *     - Removed getBattleView().setDoubleBuffered(false) as BufferStrategy is
 *       used now
 *     - Replaced FileSpecificationVector, RobotPeerVector, and
 *       RobotClassManagerVector with plain Vector
 *     - Added check for if GUI is enabled before using graphical components
 *     - Added restart() method
 *     - Ported to Java 5
 *     - Added support for the replay feature
 *     - Removed the clearBattleProperties()
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added PauseResumeListener interface, addListener(), removeListener(),
 *       notifyBattlePaused(), notifyBattleResumed() for letting listeners
 *       receive notifications when the game is paused or resumed
 *     - Added missing functionality in to support team battles in
 *       startNewBattle(BattleSpecification spec, boolean replay)
 *     - Added missing close() on FileInputStreams and FileOutputStreams
 *     - isPaused() is now synchronized
 *     - Extended sendResultsToListener() to handle teams as well as robots
 *     - Added setDefaultBattleProperties() for resetting battle properties
 *     - Removed the showResultsDialog parameter from the stop() method
 *     - Added null pointer check to the sendResultsToListener() method
 *     - Enhanced the getBattleFilename() to look into the battle dir and also
 *       add the .battle file extension to the returned file name if this is
 *       missing
 *     - Removed battleRunning field, isBattleRunning(), and setBattle()
 *     - Bugfix: Multiple battle threads could run in the same time when the
 *       battle thread was started in startNewBattle()
 *     Luis Crespo
 *     - Added debug step feature, including the nextTurn(), shouldStep(),
 *       startNewRound()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup() to prevent memory leaks by removing circular
 *       references
 *******************************************************************************/
package robocode.manager;


import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.battle.IBattleControl;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.events.IBattleListener;
import robocode.battle.events.BattleEndedEvent;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.control.RandomFactory;
import robocode.control.RobotSpecification;
import robocode.io.FileUtil;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.peer.TeamPeer;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.FileSpecification;
import robocode.repository.RobotFileSpecification;
import robocode.repository.TeamSpecification;
import robocode.security.RobocodeSecurityManager;
import robocode.Event;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class BattleManager implements IBattleControl {
    private RobocodeManager manager;

    private Battle battle;
    private BattleProperties battleProperties = new BattleProperties();

    private BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();

    private String battleFilename;
    private String battlePath;

    private int pauseCount = 0;
    private AtomicBoolean isManagedTPS=new AtomicBoolean(false); 

    public BattleManager(RobocodeManager manager) {
        this.manager = manager;
    }

    public synchronized void cleanup() {
        if (battle!=null){
            battle.cleanup();
            battle = null;
        }
        manager = null;
        battleEventDispatcher = null;
    }

    // Called when starting a new battle from GUI
    public synchronized void startNewBattle(BattleProperties battleProperties, boolean replay) {
        startNewBattle(battleProperties, replay, false);
    }

    // Called when starting a new battle from GUI
    public boolean startNewBattle(BattleProperties battleProperties, boolean replay, boolean waitTillOver) {
        this.battleProperties = battleProperties;

        List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
                false, false, false, false, false, false);

        List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

        if (battleProperties.getSelectedRobots() != null) {
            StringTokenizer tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");

            while (tokenizer.hasMoreTokens()) {
                String bot = tokenizer.nextToken();

                boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, null);

                if (failed) {
                    return false;
                }
            }
        }

        startNewBattleImpl(battlingRobotsList, replay, waitTillOver);
        return true;
        }

    // Called from the RobocodeEngine
	public boolean startNewBattle(BattleSpecification spec, boolean replay, boolean waitTillOver) {
        battleProperties = new BattleProperties();
        battleProperties.setBattlefieldWidth(spec.getBattlefield().getWidth());
        battleProperties.setBattlefieldHeight(spec.getBattlefield().getHeight());
        battleProperties.setGunCoolingRate(spec.getGunCoolingRate());
        battleProperties.setInactivityTime(spec.getInactivityTime());
        battleProperties.setNumRounds(spec.getNumRounds());
        battleProperties.setSelectedRobots(spec.getRobots());

        List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
                false, false, false, false, false, false);
        List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

        for (robocode.control.RobotSpecification battleRobotSpec : spec.getRobots()) {
            if (battleRobotSpec == null) {
                break;
            }

            String bot = battleRobotSpec.getClassName();

            if (!(battleRobotSpec.getVersion() == null || battleRobotSpec.getVersion().length() == 0)) {
                bot += ' ' + battleRobotSpec.getVersion();
            }

            boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, battleRobotSpec);

            if (failed) {
				return false;
            }
        }
        startNewBattleImpl(battlingRobotsList, replay, waitTillOver);
        return true;
    }

	private boolean loadRobot(List<FileSpecification> robotSpecificationsList, List<RobotClassManager> battlingRobotsList, String bot, RobotSpecification battleRobotSpec) {
        boolean found = false;

        for (FileSpecification fileSpec : robotSpecificationsList) {
            if (fileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
                if (fileSpec instanceof RobotFileSpecification) {
                    RobotClassManager rcm = new RobotClassManager((RobotFileSpecification) fileSpec);

                    if (battleRobotSpec != null) {
                        rcm.setControlRobotSpecification(battleRobotSpec);
                    }
                    battlingRobotsList.add(rcm);
                    found = true;
                    break;
                } else if (fileSpec instanceof TeamSpecification) {
                    TeamSpecification currentTeam = (TeamSpecification) fileSpec;
                    TeamPeer teamManager = new TeamPeer(currentTeam.getName());

                    StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

                    while (teamTokenizer.hasMoreTokens()) {
                        bot = teamTokenizer.nextToken();
                        RobotFileSpecification match = null;

                        for (FileSpecification teamFileSpec : robotSpecificationsList) {
                            // Teams cannot include teams
                            if (teamFileSpec instanceof TeamSpecification) {
                                continue;
                            }
                            if (teamFileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
                                // Found team member
                                match = (RobotFileSpecification) teamFileSpec;
                                if (currentTeam.getRootDir().equals(teamFileSpec.getRootDir())
                                        || currentTeam.getRootDir().equals(teamFileSpec.getRootDir().getParentFile())) {
                                    found = true;
                                    break;
                                }
                                // else, still looking
                            }
                        }
                        RobotClassManager rcm = new RobotClassManager(match, teamManager);

                        if (battleRobotSpec != null) {
                            rcm.setControlRobotSpecification(battleRobotSpec);
                        }
                        battlingRobotsList.add(rcm);
                    }
                    break;
                }
            }
        }

        if (!found) {
            logError("Aborting battle, could not find robot: " + bot);
            this.battleEventDispatcher.onBattleEnded(new BattleEndedEvent(true));
            return true;
        }
        return false;
    }

    private void startNewBattleImpl(List<RobotClassManager> battlingRobotsList, boolean replay, boolean waitTillOver) {

        logMessage("Preparing battle...");
        if (battle != null && battle.isRunning()) { // TODO is that good way ? should we rather throw exception here when battle is running ?
            battle.stop(true);
        }

        BattleField battleField = new DefaultBattleField(battleProperties.getBattlefieldWidth(),
                battleProperties.getBattlefieldHeight());

        if (manager.isGUIEnabled()) {
            BattleView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

            battleView.setup(battleField);
        }
        Logger.setLogListener(battleEventDispatcher);

        if (manager.isSoundEnabled()) {
            manager.getSoundManager().setBattleEventDispatcher(battleEventDispatcher);
        }

        // resets seed for deterministic behavior of Random
        final String seed = System.getProperty("RANDOMSEED", "none");

        if (!seed.equals("none")) {
            RandomFactory.resetDeterministic(Long.valueOf(seed));
        }

        battle = new Battle(battleField, manager, battleEventDispatcher, isPaused());

        // Set stuff the view needs to know
        battle.setProperties(battleProperties);

        Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

        battleThread.setPriority(Thread.NORM_PRIORITY);
        battleThread.setName("Battle Thread");
        battle.setBattleThread(battleThread);
        battle.setReplay(replay);

        if (!System.getProperty("NOSECURITY", "false").equals("true")) {
            ((RobocodeSecurityManager) System.getSecurityManager()).addSafeThread(battleThread);
            ((RobocodeSecurityManager) System.getSecurityManager()).setBattleThread(battleThread);
        }

        if (manager.isGUIEnabled()) {
            BattleView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

            battleView.setVisible(true);
            battleView.setInitialized(false);
        }

        for (RobotClassManager robotClassMgr : battlingRobotsList) {
            battle.addRobot(robotClassMgr);
        }

        // Start the battle thread
        battleThread.start();

        // Wait until the battle is running and ended.
        // This must be done as a new battle could be started immediately after this one causing
        // multiple battle threads to run at the same time, which must be prevented!
        battle.waitTillStarted();
        if (waitTillOver) {
            battle.waitTillOver();
        }
    }

    public String getBattleFilename() {
        String filename = battleFilename;

        if (filename != null) {
            if (filename.indexOf(File.separatorChar) < 0) {
                filename = FileUtil.getBattlesDir().getName() + File.separatorChar + filename;
            }
            if (!filename.endsWith(".battle")) {
                filename += ".battle";
            }
        }
        return filename;
    }

    public void setBattleFilename(String newBattleFilename) {
        battleFilename = newBattleFilename;
    }

    public String getBattlePath() {
        if (battlePath == null) {
            battlePath = System.getProperty("BATTLEPATH");
            if (battlePath == null) {
                battlePath = "battles";
            }
            battlePath = new File(FileUtil.getCwd(), battlePath).getAbsolutePath();
        }
        return battlePath;
    }

    public void saveBattleProperties() {
        if (battleProperties == null) {
            logError("Cannot save null battle properties");
            return;
        }
        if (battleFilename == null) {
            logError("Cannot save battle to null path, use setBattleFilename()");
            return;
        }
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(battleFilename);

            battleProperties.store(out, "Battle Properties");
        } catch (IOException e) {
            logError("IO Exception saving battle properties: " + e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {// swallow
                }
            }
        }
    }

    public BattleProperties loadBattleProperties() {
        BattleProperties res = new BattleProperties();
        FileInputStream in = null;

        try {
            in = new FileInputStream(getBattleFilename());
            res.load(in);
        } catch (FileNotFoundException e) {
            logError("No file " + battleFilename + " found, using defaults.");
        } catch (IOException e) {
            logError("IO Exception reading " + getBattleFilename() + ": " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {// swallow
                }
            }
        }
        return res;
    }

    public BattleProperties getBattleProperties() {
        if (battleProperties == null) {
            battleProperties = new BattleProperties();
        }
        return battleProperties;
    }

    public void setDefaultBattleProperties() {
        battleProperties = new BattleProperties();
    }

    public boolean hasReplayRecord() {  //TODO get rid of this after redording rework
    	if (battle == null) {
    		return false;
    	}
    	return battle.hasReplayRecord();	
    }

    public boolean isManagedTPS(){
        return isManagedTPS.get();
    }

    public void setManagedTPS(boolean value){
        isManagedTPS.set(value);
    }

    public synchronized void addListener(IBattleListener listener) {
        battleEventDispatcher.addListener(listener);
    }

    public synchronized void removeListener(IBattleListener listener) {
        battleEventDispatcher.removeListener(listener);
    }

    public synchronized void stop(boolean waitTillEnd) {
        if (battle != null && battle.isRunning()) {
            battle.stop(waitTillEnd);
        }
    }

    public synchronized void restart() {
        // Start new battle. The old battle is automatically stopped
        startNewBattle(battleProperties, false);
    }

    public synchronized void replay() {
        startNewBattle(battleProperties, true);
    }


    private boolean isPaused() {
        return (pauseCount != 0);
    }

    public synchronized void togglePauseResumeBattle() {
        if (isPaused()){
            resumeBattle();
        }
        else{
            pauseBattle();
        }
    }

    public synchronized void pauseBattle() {
        if (++pauseCount == 1) {
            if (battle!=null && battle.isRunning()){
                battle.pause();
            }
        }
    }


    public synchronized void pauseIfResumedBattle() {
        if (pauseCount==0){
            pauseCount++;
            if (battle!=null && battle.isRunning()){
                battle.pause();
            }
        }
    }

    public synchronized void resumeIfPausedBattle() {
        if (pauseCount == 1){
            pauseCount--;
            if (battle!=null && battle.isRunning()){
                battle.resume();
            }
        }
    }

    public synchronized void resumeBattle() {
        if (--pauseCount < 0){
            pauseCount= 0;
            logError("SYSTEM: pause game bug!");
        }
        else if (pauseCount == 0) {
            if (battle!=null && battle.isRunning()){
                battle.resume();
            }
        }
    }

    /**
     * Steps for a single turn, then goes back to paused
     */
    public synchronized void nextTurn() {
        if (battle != null && battle.isRunning()) {
            battle.step();
        }
    }

    public synchronized void killRobot(int robotIndex) {
        if (battle!=null && battle.isRunning()){
            battle.killRobot(robotIndex);
        }
    }

    public synchronized void setPaintEnabled(int robotIndex, boolean enable) {
        if (battle!=null && battle.isRunning()){
            battle.setPaintEnabled(robotIndex, enable);
        }
    }

    public synchronized void setSGPaintEnabled(int robotIndex, boolean enable) {
        if (battle!=null && battle.isRunning()){
            battle.setSGPaintEnabled(robotIndex, enable);
        }
    }

    public synchronized void sendInteractiveEvent(Event event) {
        if (battle!=null && battle.isRunning() && !isPaused()){
            battle.sendInteractiveEvent(event);
        }
    }
}
