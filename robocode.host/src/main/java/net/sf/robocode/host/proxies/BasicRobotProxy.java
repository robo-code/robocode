/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.host.proxies;


import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.peer.*;
import net.sf.robocode.repository.IRobotItem;
import robocode.*;
import robocode.Event;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.util.Utils;

import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

// XXX Remember to update the .NET version whenever a change is made to this class!

/**
 * @author Pavel Savara (original)
 */
public class BasicRobotProxy extends BasicBotProxy implements IBasicRobotPeer {
	public BasicRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(specification, hostManager, peer, statics);
	}

	// asynchronous actions
	public Bullet setFire(double power) {
		setCall();
		return setFireImpl(power);
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

	public void setGunColor(Color color) {
		setCall();
		commands.setGunColor(color != null ? color.getRGB() : ExecCommands.defaultGunColor);
	}

	public void setRadarColor(Color color) {
		setCall();
		commands.setRadarColor(color != null ? color.getRGB() : ExecCommands.defaultRadarColor);
	}

	public void setBulletColor(Color color) {
		setCall();
		commands.setBulletColor(color != null ? color.getRGB() : ExecCommands.defaultBulletColor);
	}

	public void setScanColor(Color color) {
		setCall();
		commands.setScanColor(color != null ? color.getRGB() : ExecCommands.defaultScanColor);
	}

	public double getRadarTurnRemaining() {
		getCall();
		return commands.getRadarTurnRemaining();
	}

	public double getGunTurnRemaining() {
		getCall();
		return commands.getGunTurnRemaining();
	}

	public double getGunCoolingRate() {
		getCall();
		return statics.getBattleRules().getGunCoolingRate();
	}

	public double getGunHeading() {
		getCall();
		return status.getGunHeadingRadians();
	}

	public double getRadarHeading() {
		getCall();
		return status.getRadarHeadingRadians();
	}

	public double getGunHeat() {
		getCall();
		return getGunHeatImpl();
	}
	
	public int getNumSentries() {
		getCall();
		return status.getNumSentries();
	}

	public int getSentryBorderSize() {
		getCall();
		return statics.getBattleRules().getSentryBorderSize();
	}
	
	// -----------
	// implementations
	// -----------
	@Override
	protected final double getGunHeatImpl() {
		return super.getGunHeatImpl();
	}

	@Override
	protected final Bullet setFireImpl(double power) {
		if (Double.isNaN(power)) {
			println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (getGunHeatImpl() > 0 || getEnergyImpl() == 0) {
			return null;
		}

		power = min(getEnergyImpl(), min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

		Bullet bullet;
		BulletCommand wrapper;
		Event currentTopEvent = eventManager.getCurrentTopEvent();

		nextBulletId++;

		if (currentTopEvent != null && currentTopEvent.getTime() == status.getTime() && !statics.isAdvancedRobot()
				&& status.getGunHeadingRadians() == status.getRadarHeadingRadians()
				&& (ScannedRobotEvent.class.isAssignableFrom(currentTopEvent.getClass()) ||
						ScannedShipEvent.class.isAssignableFrom(currentTopEvent.getClass()))) {
			// this is angle assisted bullet
			ScannedRobotEvent e = (ScannedRobotEvent) currentTopEvent;
			double fireAssistAngle = Utils.normalAbsoluteAngle(status.getHeadingRadians() + e.getBearingRadians());

			bullet = new Bullet(fireAssistAngle, getX(), getY(), power, statics.getName(), null, true, nextBulletId);
			wrapper = new BulletCommand(power, true, fireAssistAngle, nextBulletId);
		} else {
			// this is normal bullet
			bullet = new Bullet(status.getGunHeadingRadians(), getX(), getY(), power, statics.getName(), null, true,
					nextBulletId);
			wrapper = new BulletCommand(power, false, 0, nextBulletId);
		}

		super.setFireImpl(power);

		commands.getBullets().add(wrapper);

		bullets.put(nextBulletId, bullet);

		return bullet;
	}

	protected final void setTurnGunImpl(double radians) {
		commands.setGunTurnRemaining(radians);
	}

	protected final void setTurnRadarImpl(double radians) {
		commands.setRadarTurnRemaining(radians);
	}
}
