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


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IInteractiveEvents;
import robocode.robotinterfaces.IInteractiveRobot;

import java.awt.*;


/**
 * A MousePressedEvent is sent to {@link Robot#onMousePressed(java.awt.event.MouseEvent)
 * onMousePressed()} when the mouse is pressed inside the battle view.
 *
 * @author Pavel Savara (original)
 * @see MouseClickedEvent
 * @see MouseReleasedEvent
 * @see MouseEnteredEvent
 * @see MouseExitedEvent
 * @see MouseMovedEvent
 * @see MouseDraggedEvent
 * @see MouseWheelMovedEvent
 * @since 1.6.1
 */
public final class MousePressedEvent extends MouseEvent {
	private final static int classPriority = 98;

	/**
	 * Called by the game to create a new MousePressedEvent.
	 *
	 * @param source the source mouse event originating from the AWT.
	 */
	public MousePressedEvent(java.awt.event.MouseEvent source) {
		super(source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return classPriority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		if (statics.isInteractiveRobot()) {
			IInteractiveEvents listener = ((IInteractiveRobot) robot).getInteractiveEventListener();

			if (listener != null) {
				listener.onMousePressed(getSourceEvent());
			}
		}
	}
}
