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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode;


import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IInteractiveEvents;
import robocode.robotinterfaces.IInteractiveRobot;
import robocode.peer.RobotStatics;

import java.awt.*;


/**
 * A KeyReleasedEvent is sent to {@link Robot#onKeyReleased(java.awt.event.KeyEvent)
 * onKeyReleased()} when a key has been released on the keyboard.
 *
 * @author Pavel Savara (original)
 * @see KeyPressedEvent
 * @see KeyTypedEvent
 * @since 1.6.1
 */
public final class KeyReleasedEvent extends KeyEvent {

	/**
	 * Called by the game to create a new KeyReleasedEvent.
	 *
	 * @param source the source key event originating from the AWT.
	 */
	public KeyReleasedEvent(java.awt.event.KeyEvent source) {
		super(source);
	}

	private static int classPriority = 98;
    
	@Override
	protected final int getClassPriorityImpl() {
		return classPriority;
	}

	@Override
	protected void setClassPriorityImpl(int priority) {
		classPriority = priority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		if (statics.isInteractiveRobot()) {
			IInteractiveEvents listener = ((IInteractiveRobot) robot).getInteractiveEventListener();

			if (listener != null) {
				listener.onKeyReleased(getSourceEvent());
			}
		}
	}
}
