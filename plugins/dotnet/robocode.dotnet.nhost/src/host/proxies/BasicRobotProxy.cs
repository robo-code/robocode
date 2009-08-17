using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Threading;
using net.sf.robocode.dotnet.host.events;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode;
using robocode.exception;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class BasicRobotProxy:HostingRobotProxy ,IBasicRobotPeer
    {
	private const long
			MAX_SET_CALL_COUNT = 10000,
			MAX_GET_CALL_COUNT = 10000;

	//private IGraphicsProxy graphicsProxy;

	private RobotStatus status;
	protected ExecCommands commands;
	private ExecResults execResults;
	private readonly Dictionary<int, Bullet> bullets = new Dictionary<int, Bullet>();
	private int bulletCounter; 

	private volatile int setCallCount = 0;
	private volatile int getCallCount = 0;

	protected Condition waitCondition;
	private bool testingCondition;
	private double firedEnergy;
	private double firedHeat;

	public BasicRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) 
		:base(specification, hostManager, peer, statics)
    {

		eventManager = new EventManager(this);

		//TODO graphicsProxy = new Graphics2DSerialized();

		// dummy
		execResults = new ExecResults(null, null, null, null, null, false, false, false);

		setSetCallCount(0);
		setGetCallCount(0);
	}

	protected override void initializeRound(ExecCommands commands, RobotStatus status) {
		updateStatus(commands, status);
		eventManager.reset();
		StatusEvent start = new StatusEvent(status);

		eventManager.add(start);
		setSetCallCount(0);
		setGetCallCount(0);
	}

	public override void cleanup() {
		base.cleanup();

		// Cleanup and remove current wait condition
		if (waitCondition != null) {
			waitCondition = null;
		}

		// Cleanup and remove the event manager
		if (eventManager != null) {
			eventManager.cleanup();
			eventManager = null;
		}

		// Cleanup graphics proxy
		//TODO graphicsProxy = null;
		execResults = null;
		status = null;
		commands = null;
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		setCall();
		return setFireImpl(power);
	}

	// blocking actions
	public void execute() {
		executeImpl();
	}

	public void move(double distance) {
		setMoveImpl(distance);
		do {
			execute(); // Always tick at least once
		} while (getDistanceRemaining() != 0);
	}

	public void turnBody(double radians) {
		setTurnBodyImpl(radians);
		do {
			execute(); // Always tick at least once
		} while (getBodyTurnRemaining() != 0);
	}

	public void turnGun(double radians) {
		setTurnGunImpl(radians);
		do {
			execute(); // Always tick at least once
		} while (getGunTurnRemaining() != 0);
	}

	public Bullet fire(double power) {
		Bullet bullet = setFire(power);

		execute();
		return bullet;
	}

	// fast setters
	public void setBodyColor(Color color) {
		setCall();
		commands.setBodyColor(color != null ? color.ToArgb() : ExecCommands.defaultBodyColor);
	}

	public void setGunColor(Color color) {
		setCall();
		commands.setGunColor(color != null ? color.ToArgb() : ExecCommands.defaultGunColor);
	}

	public void setRadarColor(Color color) {
		setCall();
		commands.setRadarColor(color != null ? color.ToArgb() : ExecCommands.defaultRadarColor);
	}

	public void setBulletColor(Color color) {
		setCall();
		commands.setBulletColor(color != null ? color.ToArgb() : ExecCommands.defaultBulletColor);
	}

	public void setScanColor(Color color) {
		setCall();
		commands.setScanColor(color != null ? color.ToArgb() : ExecCommands.defaultScanColor);
	}

	// counters
	public void setCall() {
	    int res = Interlocked.Increment(ref setCallCount);

		if (res >= MAX_SET_CALL_COUNT) {
			println("SYSTEM: You have made " + res + " calls to setXX methods without calling execute()");
			throw new DisabledException("Too many calls to setXX methods");
		}
	}

	public void getCall() {
	    int res = Interlocked.Increment(ref getCallCount);

		if (res >= MAX_GET_CALL_COUNT) {
			println("SYSTEM: You have made " + res + " calls to getXX methods without calling execute()");
			throw new DisabledException("Too many calls to getXX methods");
		}
	}

	public double getDistanceRemaining() {
		getCall();
		return commands.getDistanceRemaining();
	}

	public double getRadarTurnRemaining() {
		getCall();
		return commands.getRadarTurnRemaining();
	}

	public double getBodyTurnRemaining() {
		getCall();
		return commands.getBodyTurnRemaining();
	}

	public double getGunTurnRemaining() {
		getCall();
		return commands.getGunTurnRemaining();
	}

	public double getVelocity() {
		getCall();
		return status.getVelocity();
	}

	public double getGunCoolingRate() {
		getCall();
	    return 0; //TODO statics.getBattleRules().getGunCoolingRate();
	}

	public String getName() {
		getCall();
		return statics.getName();
	}

	public long getTime() {
		getCall();
		return getTimeImpl();
	}

	public double getBodyHeading() {
		getCall();
		return status.getHeadingRadians();
	}

	public double getGunHeading() {
		getCall();
		return status.getGunHeadingRadians();
	}

	public double getRadarHeading() {
		getCall();
		return status.getRadarHeadingRadians();
	}

	public double getEnergy() {
		getCall();
		return getEnergyImpl();
	}

	public double getGunHeat() {
		getCall();
		return getGunHeatImpl();
	}

	public double getX() {
		getCall();
		return status.getX();
	}

	public double getY() {
		getCall();
		return status.getY();
	}

	public int getOthers() {
		getCall();
		return status.getOthers();
	}

	public double getBattleFieldHeight() {
		getCall();
        return 0; //TODO statics.getBattleRules().getBattlefieldHeight();
	}

	public double getBattleFieldWidth() {
		getCall();
        return 0; //TODO statics.getBattleRules().getBattlefieldWidth();
	}

	public int getNumRounds() {
		getCall();
        return 0; //TODO statics.getBattleRules().getNumRounds();
	}

	public int getRoundNum() {
		getCall();
		return status.getRoundNum();
	}

	public Graphics getGraphics() {
		getCall();
		commands.setTryingToPaint(true);
		return getGraphicsImpl();
	}

	public void setDebugProperty(String key, String value) {
		setCall();
		commands.setDebugProperty(key, value);
	}

	public void rescan() {
		bool reset = false;
		bool resetValue = false;

		if (eventManager.getCurrentTopEventPriority() == eventManager.getScannedRobotEventPriority()) {
			reset = true;
			resetValue = eventManager.getInterruptible(eventManager.getScannedRobotEventPriority());
			eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), true);
		}

		commands.setScan(true);
		executeImpl();
		if (reset) {
			eventManager.setInterruptible(eventManager.getScannedRobotEventPriority(), resetValue);
		}
	}
	
	// -----------
	// implementations
	// -----------

	public long getTimeImpl() {
		return status.getTime();
	}

	public Graphics getGraphicsImpl() {
	    return null; //TODO (Graphics) graphicsProxy;
	}

	protected sealed override void executeImpl() {
		if (execResults == null) {
			// this is to slow down undead robot after cleanup, from fast exception-loop
			Thread.Sleep(1000);
		}

		// Entering tick
		//TODO robotThreadManager.checkRunThread();
		if (testingCondition) {
			throw new RobotException(
					"You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
		}

		setSetCallCount(0);
		setGetCallCount(0);

		// This stops autoscan from scanning...
		if (waitCondition != null && waitCondition.test()) {
			waitCondition = null;
			commands.setScan(true);
		}

		//TODO commands.setOutputText(out.readAndReset());
		//TODO commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

		// call server
		callBattle();

		updateStatus(execResults.getCommands(), execResults.getStatus());
		//TODO graphicsProxy.setPaintingEnabled(execResults.isPaintEnabled());
		firedEnergy = 0;
		firedHeat = 0;

		// add new events
		eventManager.add(new StatusEvent(execResults.getStatus()));
		if (statics.isPaintRobot() && (execResults.isPaintEnabled())) {
			// Add paint event, if robot is a paint robot and its painting is enabled
			eventManager.add(new PaintEvent());
		}

		// add other events
		if (execResults.getEvents() != null) {
			foreach (Event evnt in execResults.getEvents()) {
				eventManager.add(evnt);
				HiddenAccessN.updateBullets(evnt, bullets);
			}
		}

		if (execResults.getBulletUpdates() != null) {
			foreach (BulletStatus s in execResults.getBulletUpdates()) {
				Bullet bullet = bullets[s.bulletId];

				if (bullet != null) {
					HiddenAccessN.update(bullet, s.x, s.y, s.victimName, s.isActive);
					if (!s.isActive) {
						bullets.Remove(s.bulletId);
					}
				}
			}
		}

		// add new team messages
		loadTeamMessages(execResults.getTeamMessages());

		eventManager.processEvents();
	}

	private void callBattle() {
        //TODO execResults = peer.executeImpl(commands);
	}

	/* TODO this stuff will be used only for IPC/serialized communication with battle 
	 private void callBattleSerial() {
	 // we need to elevate just for interactive robot, because of MouseEvent constructors calling security check
	 ExecutePrivilegedAction rpa = new ExecutePrivilegedAction();

	 rpa.newCommands = commands;
	 execResults  = AccessController.doPrivileged(rpa);
	 }

	 public class ExecutePrivilegedAction implements PrivilegedAction<ExecResults> {
	 public ExecCommands newCommands;

	 public ExecResults run() {
	 final ByteBuffer result;
	 try {
	 result = peer.executeImplSerial(RbSerializer.serializeToBuffer(commands));
	 return RbSerializer.deserializeFromBuffer(result);
	 } catch (IOException e) {
	 Logger.logError(e);
	 return null;
	 }
	 }
	 }*/

	protected sealed override void waitForBattleEndImpl() {
		eventManager.clearAllEvents(false);
		//TODO graphicsProxy.setPaintingEnabled(false);
		do {
			//TODO commands.setOutputText(out.readAndReset());
			//TODO commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

			// call server
            //TODO execResults = peer.waitForBattleEndImpl(commands);

			updateStatus(execResults.getCommands(), execResults.getStatus());

			// add new events
			if (execResults.getEvents() != null) {
				foreach(Event evnt in execResults.getEvents()) {
					if (evnt is BattleEndedEvent) {
						eventManager.add(evnt);
					}
				}
			}
			eventManager.resetCustomEvents();
			eventManager.processEvents();
		} while (!execResults.isHalt() && execResults.isShouldWait());
	}

	private void updateStatus(ExecCommands commands, RobotStatus status) {
		this.status = status;
		this.commands = commands;
	}

	protected virtual void loadTeamMessages(List<TeamMessage> teamMessages) {}

	protected double getEnergyImpl() {
		return status.getEnergy() - firedEnergy;
	}

	protected double getGunHeatImpl() {
		return status.getGunHeat() + firedHeat;
	}

	protected void setMoveImpl(double distance) {
		if (getEnergyImpl() == 0) {
			return;
		}
		commands.setDistanceRemaining(distance);
		commands.setMoved(true);
	}

	protected Bullet setFireImpl(double power) {
		if (Double.IsNaN(power)) {
			println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (getGunHeatImpl() > 0 || getEnergyImpl() == 0) {
			return null;
		}

		power = Math.Min(getEnergyImpl(), Math.Min(Math.Max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

		Bullet bullet;
		BulletCommand wrapper;
		Event currentTopEvent = eventManager.getCurrentTopEvent();

		bulletCounter++;

		if (currentTopEvent != null && currentTopEvent.getTime() == status.getTime() && !statics.isAdvancedRobot()
				&& status.getGunHeadingRadians() == status.getRadarHeadingRadians()
				&& typeof(ScannedRobotEvent).IsAssignableFrom(currentTopEvent.GetType())) {
			// this is angle assisted bullet
			ScannedRobotEvent e = (ScannedRobotEvent) currentTopEvent;
			double fireAssistAngle = Utils.normalAbsoluteAngle(status.getHeadingRadians() + e.getBearingRadians());

			bullet = new Bullet(fireAssistAngle, getX(), getY(), power, statics.getName(), null, true, bulletCounter);
			wrapper = new BulletCommand(power, true, fireAssistAngle, bulletCounter);
		} else {
			// this is normal bullet
			bullet = new Bullet(status.getGunHeadingRadians(), getX(), getY(), power, statics.getName(), null, true,
					bulletCounter);
			wrapper = new BulletCommand(power, false, 0, bulletCounter);
		}

		firedEnergy += power;
		firedHeat += Rules.getGunHeat(power);

		commands.getBullets().Add(wrapper);

		bullets.Add(bulletCounter, bullet);

		return bullet;
	}

	protected void setTurnGunImpl(double radians) {
		commands.setGunTurnRemaining(radians);
	}

	protected void setTurnBodyImpl(double radians) {
		if (getEnergyImpl() > 0) {
			commands.setBodyTurnRemaining(radians);
		}
	}

	protected void setTurnRadarImpl(double radians) {
		commands.setRadarTurnRemaining(radians);
	}

	// -----------
	// battle driven methods
	// -----------

	private void setSetCallCount(int setCallCount) {
		this.setCallCount = setCallCount;
	}

	private void setGetCallCount(int getCallCount) {
		this.getCallCount = getCallCount;
	}

	// -----------
	// for robot thread
	// -----------

	public void setTestingCondition(bool testingCondition) {
		this.testingCondition = testingCondition;
	}

	
	public override String ToString() {
		return statics.getShortName() + "(" + (int) status.getEnergy() + ") X" + (int) status.getX() + " Y"
				+ (int) status.getY();
	}
    }
}
