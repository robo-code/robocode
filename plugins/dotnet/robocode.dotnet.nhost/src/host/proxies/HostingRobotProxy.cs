using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using java.lang;
using net.sf.robocode.dotnet.host.events;
using net.sf.robocode.dotnet.host.security;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.host.io;
using net.sf.robocode.host.proxies;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using Exception=System.Exception;
using Object=java.lang.Object;
using Runnable=robocode.robotinterfaces.Runnable;
using String=System.String;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal abstract class HostingRobotProxy : IHostingRobotProxy, IHostedThread
    {
 	protected EventManager eventManager;
	protected RobotThreadManager robotThreadManager;
	protected RobotFileSystemManager robotFileSystemManager;
	private  IRobotRepositoryItem robotSpecification;
	protected RobotStatics statics;
	protected IRobotPeer peer;
    internal RobotClassLoader robotClassLoader;
	protected IHostManager hostManager;
	protected IBasicRobot robot;
    private TextWriter output;

	protected HostingRobotProxy(IRobotRepositoryItem robotSpecification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		this.peer = peer;
		this.statics = statics;
		this.hostManager = hostManager;
		this.robotSpecification = robotSpecification;

		robotThreadManager = new RobotThreadManager(this);

		loadClassBattle();

		robotFileSystemManager = new RobotFileSystemManager(this, hostManager.getRobotFilesystemQuota(),
				robotSpecification.getWritableDirectory(), robotSpecification.getReadableDirectory(),
				robotSpecification.getRootFile());

		robotFileSystemManager.initialize();
	}

	public virtual void cleanup() {
		robot = null;

		// Remove the file system and the manager
		robotFileSystemManager = null;

		if (robotThreadManager != null) {
			robotThreadManager.cleanup();
		}
		robotThreadManager = null;
	}

	public java.io.PrintStream getOut() {
		return null;
	}

        public TextWriter GetOut()
    {
        return null;
    }

	public void println(String s) {
		output.WriteLine(s);
	}

	public void println(Exception ex) {
        output.WriteLine(ex);
    }

	public RobotStatics getStatics() {
		return statics;
	}

	public RobotFileSystemManager getRobotFileSystemManager() {
		return robotFileSystemManager;
	}
	
	public ClassLoader getRobotClassloader() {
        return null;
	}

        public void println(java.lang.String par0)
        {
            throw new NotImplementedException();
        }

        // -----------
	// battle driven methods
	// -----------

	protected abstract void initializeRound(ExecCommands commands, RobotStatus status);

	public void startRound(ExecCommands commands, RobotStatus status) {
		initializeRound(commands, status);
		robotThreadManager.start();
	}


        public void startRound(Object par0, Object par1)
        {
            //TODO
            throw new NotImplementedException();
        }

        public void forceStopThread() {
		if (!robotThreadManager.forceStop()) {
			peer.punishBadBehavior();
			peer.setRunning(false);
		}
	}

	public void waitForStopThread() {
		if (!robotThreadManager.waitForStop()) {
			peer.punishBadBehavior();
			peer.setRunning(false);
		}
	}

	private void loadClassBattle() {
		try {
			robotClassLoader.LoadRobotMainClass();
		} catch (Throwable e) {
			println("SYSTEM: Could not load " + statics.getName() + " : ");
			println(e);
			drainEnergy();
		}
	}

	private bool loadRobotRound() {
		robot = null;
		try {
			robot = robotClassLoader.CreateRobotInstance();
			if (robot == null) {
				println("SYSTEM: Skipping robot: " + statics.getName());
				return false;
			}
			robot.setOut(output);
			robot.setPeer((IBasicRobotPeer) this);
			eventManager.setRobot(robot);
		} /*catch (IllegalAccessException e) {
			println("SYSTEM: Unable to instantiate this robot: " + e);
			println("SYSTEM: Is your constructor marked public?");
			println(e);
			robot = null;
			logMessage(e);
			return false;
		} */catch (Throwable e) {
			println("SYSTEM: An error occurred during initialization of " + statics.getName());
			println("SYSTEM: " + e);
			println(e);
			robot = null;
			Logger.logMessage(e);
			return false;
		} finally {
			//threadManager.setLoadingRobot(null);
		}
		return true;
	}

	// /


	protected abstract void executeImpl();

	public void run() {
		//robotThreadManager.initAWT();

		peer.setRunning(true);

		if (!robotSpecification.isValid() || !loadRobotRound()) {
			drainEnergy();
			peer.punishBadBehavior();
			waitForBattleEndImpl();
		} else {
			try {
				if (robot != null) {

					// Process all events for the first turn.
					// This is done as the first robot status event must occur before the robot
					// has started running.
					eventManager.processEvents();

					Runnable runnable = robot.getRobotRunnable();

					if (runnable != null) {
						runnable.run();
					}
				}

				// noinspection InfiniteLoopStatement
				for (;;) {
					executeImpl();
				}
			} catch (WinException e) {// Do nothing
			} catch (AbortedException e) {// Do nothing
			} catch (DeathException e) {
				println("SYSTEM: " + statics.getName() + " has died");
			} catch (DisabledException e) {
				drainEnergy();
				String msg = e.Message;

				if (msg == null) {
					msg = "";
				} else {
					msg = ": " + msg;
				}
				println("SYSTEM: Robot disabled" + msg);
				Logger.logMessage(statics.getName() + "Robot disabled");
			} catch (Exception e) {
				drainEnergy();
				println(e);
				Logger.logMessage(statics.getName() + ": Exception: " + e); // without stack here
			} finally {
				waitForBattleEndImpl();
			}
		}

		// If battle is waiting for us, well, all done!
	    lock(this) {
			peer.setRunning(false);
			//TODO notifyAll();
		}
	}

	protected abstract void waitForBattleEndImpl();

	public void drainEnergy() {
		peer.drainEnergy();
	}
   }
}
