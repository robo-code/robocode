/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.proxies;


import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.repository.IRobotItem;
import robocode.RobotStatus;
import robocode.robotinterfaces.peer.IStandardRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class StandardRobotProxy extends BasicRobotProxy implements IStandardRobotPeer {

	private boolean isStopped;
	private double saveAngleToTurn;
	private double saveDistanceToGo;
	private double saveGunAngleToTurn;
	private double saveRadarAngleToTurn;

	public StandardRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(specification, hostManager, peer, statics);
	}

	@Override
	protected void initializeRound(ExecCommands commands, RobotStatus status) {
		super.initializeRound(commands, status);
		isStopped = true;
	}

	// blocking actions
	public void stop(boolean overwrite) {
		setStopImpl(overwrite);
		execute();
	}

	public void resume() {
		setResumeImpl();
		execute();
	}

	public void turnRadar(double radians) {
		setTurnRadarImpl(radians);
		do {
			execute(); // Always tick at least once
		} while (getRadarTurnRemaining() != 0);
	}

	// fast setters
	public void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
		setCall();
		commands.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
	}

	public void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
		setCall();
		commands.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
		if (!commands.isAdjustRadarForBodyTurnSet()) {
			commands.setAdjustRadarForBodyTurn(newAdjustRadarForGunTurn);
		}
	}

	public void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
		setCall();
		commands.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
		commands.setAdjustRadarForBodyTurnSet(true);
	}

	protected final void setResumeImpl() {
		if (isStopped) {
			isStopped = false;
			commands.setDistanceRemaining(saveDistanceToGo);
			commands.setBodyTurnRemaining(saveAngleToTurn);
			commands.setGunTurnRemaining(saveGunAngleToTurn);
			commands.setRadarTurnRemaining(saveRadarAngleToTurn);
		}
	}

	protected final void setStopImpl(boolean overwrite) {
		if (!isStopped || overwrite) {
			this.saveDistanceToGo = getDistanceRemaining();
			this.saveAngleToTurn = getBodyTurnRemaining();
			this.saveGunAngleToTurn = getGunTurnRemaining();
			this.saveRadarAngleToTurn = getRadarTurnRemaining();
		}
		isStopped = true;

		commands.setDistanceRemaining(0);
		commands.setBodyTurnRemaining(0);
		commands.setGunTurnRemaining(0);
		commands.setRadarTurnRemaining(0);
	}
}
