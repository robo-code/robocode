/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer.proxies;


import robocode.peer.IDisplayRobotPeer;
import robocode.peer.robot.RobotOutputStream;
import robocode.robotinterfaces.IBasicEvents;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public class DisplayRobotProxy extends ReadingRobotProxy implements IDisplayRobotProxy {

	private IDisplayRobotPeer peer;

	public DisplayRobotProxy(IDisplayRobotPeer peer) {
		super(peer);
		this.peer = peer;
	}

	@Override
	public void cleanup() {
		super.cleanup();
		peer = null;
	}

	public void lockRead() {
		peer.lockRead();
	}

	public void unlockRead() {
		peer.unlockRead();
	}

	public RobotOutputStream getOut() {
		return peer.getOut();
	}

	public void displaySetPaintEnabled(boolean enabled) {
		peer.lockWrite();
		try {
			info.setPaintEnabled(enabled);
		} finally {
			peer.unlockWrite();
		}
	}

	public void displaySetSGPaintEnabled(boolean enabled) {
		peer.lockWrite();
		try {
			info.setSGPaintEnabled(enabled);
		} finally {
			peer.unlockWrite();
		}
	}

	public void displayKill() {
		peer.lockWrite();
		try {
			peer.getBattleProxy().battleKill();
		} finally {
			peer.unlockWrite();
		}
	}

	public void onInteractiveEvent(robocode.Event e) {
		peer.lockWrite();
		try {
			peer.getDisplayEventManager().add(e);
		} finally {
			peer.unlockWrite();
		}
	}

	public void onPaint(Graphics2D g) {
		try {
			// TODO Warning: robot is called from UI thread.
			// Security hole ?
			// Synchronization issue for client robot ?
			IBasicEvents listener = peer.getRobot().getBasicEventListener();

			if (listener != null) {
				listener.onPaint(g);
			}
		} catch (Exception e) {
			// Make sure that Robocode is not halted by an exception caused by letting the robot paint

			peer.getOut().println("SYSTEM: Exception occurred on onPaint(Graphics2D):");
			e.printStackTrace(peer.getOut());
		}
	}

}
