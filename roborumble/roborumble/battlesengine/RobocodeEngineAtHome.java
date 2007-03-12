package roborumble.battlesengine;


import java.io.File;
import java.io.IOException;
import java.security.Policy;

import robocode.RobocodeFileOutputStream;
import robocode.manager.RobocodeManager;
import robocode.repository.FileSpecificationVector;
import robocode.security.RobocodeSecurityManager;
import robocode.security.RobocodeSecurityPolicy;
import robocode.security.SecureInputStream;
import robocode.security.SecurePrintStream;
import robocode.util.Constants;
import robocode.util.Utils;
import robocode.control.*;
import robocode.repository.*;
import robocode.battle.*;


/**
 * Project RoboRumble@home
 * RobocodeEngineForTeams 
 * Based on RobocodeEngine
 */
public class RobocodeEngineAtHome {
	
	private RobocodeListener listener = null;
	private RobocodeManager manager = null;   

	private RobocodeEngineAtHome() {/* empty */}
    
	public RobocodeEngineAtHome(File robocodeHome, RobocodeListener listener) {
		init(robocodeHome, listener);
	}
    
	public RobocodeEngineAtHome(RobocodeListener listener) {
		File robocodeDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
		File robotsDir = new File(robocodeDir, "robots");

		if (robotsDir.exists()) {
			init(robocodeDir, listener);
		} else {
			throw new RuntimeException("File not found: " + robotsDir);
		}
	}
    
	private void init(File robocodeHome, RobocodeListener listener) {
		this.listener = listener;
		manager = new RobocodeManager(true, listener);
		try {
			Constants.setWorkingDirectory(robocodeHome);
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
		Thread.currentThread().setName("Application Thread");
		RobocodeSecurityPolicy securityPolicy = new RobocodeSecurityPolicy(Policy.getPolicy());

		Policy.setPolicy(securityPolicy);
		System.setSecurityManager(new RobocodeSecurityManager(Thread.currentThread(), manager.getThreadManager(), true));
		RobocodeFileOutputStream.setThreadManager(manager.getThreadManager());
		for (ThreadGroup tg = Thread.currentThread().getThreadGroup(); tg != null; tg = tg.getParent()) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThreadGroup(tg);
		}
		SecurePrintStream sysout = new SecurePrintStream(System.out, true, "System.out");
		SecurePrintStream syserr = new SecurePrintStream(System.err, true, "System.err");
		SecureInputStream sysin = new SecureInputStream(System.in, "System.in");

		System.setOut(sysout);
		if (!System.getProperty("debug", "false").equals("true")) {
			System.setErr(syserr);
		}
		System.setIn(sysin);
	}
    
	public void close() {
		manager.getWindowManager().getRobocodeFrame().dispose();
	}
    
	public String getVersion() {
		return manager.getVersionManager().getVersion();
	}
    
	public void setVisible(boolean visible) {
		manager.getWindowManager().getRobocodeFrame().setVisible(visible);
	}

	public String[] getLocalRepository() {
		Repository robotRepository = manager.getRobotRepositoryManager().getRobotRepository();
		FileSpecificationVector v = robotRepository.getRobotSpecificationsVector(false, false, false, false, true, false);
		String[] robotSpecs = new String[v.size()];

		for (int i = 0; i < robotSpecs.length; i++) {
			robotSpecs[i] = ((robocode.repository.RobotSpecification) v.elementAt(i)).getName();
		}
		return robotSpecs;
	}
	
	public void runBattle(BattleSpecification battle, String selectedRobots) {
		boolean run_aborted = false; 

		Utils.setLogListener(listener);
		BattleProperties battleProperties = battle.getBattleProperties();

		battleProperties.setSelectedRobots(selectedRobots);
		manager.getBattleManager().startNewBattle(battleProperties, false); 
	}

	public boolean isBattleRunning() {
		return manager.getBattleManager().isBattleRunning();
	}
    
	public void abortCurrentBattle() {
		manager.getBattleManager().stop(false);
	}

	public void printBattle() {
		manager.getBattleManager().printResultsData(manager.getBattleManager().getBattle());
	}
	
	public BattleResultsTableModel getResults() {
		return new BattleResultsTableModel(manager.getBattleManager().getBattle());
	}
}
