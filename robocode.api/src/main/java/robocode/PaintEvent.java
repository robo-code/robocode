/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IPaintEvents;
import robocode.robotinterfaces.IPaintRobot;

import java.awt.*;


/**
 * This event occurs when your robot should paint, where the {@link
 * Robot#onPaint(Graphics2D) onPaint()} is called on your robot.
 * <p>
 * You can use this event for setting the event priority by calling
 * {@link AdvancedRobot#setEventPriority(String, int)
 * setEventPriority("PaintEvent", priority)}
 *
 * @author Flemming N. Larsen (original)
 */
public final class PaintEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 5;

	/**
	 * Called by the game to create a new PaintEvent.
	 */
	public PaintEvent() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		if (statics.isPaintRobot()) {
			IPaintEvents listener = ((IPaintRobot) robot).getPaintEventListener();

			if (listener != null) {
				listener.onPaint(graphics);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		throw new Error("Serialization of this type is not supported");
	}
}
