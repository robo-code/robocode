/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode;


import java.awt.*;


/**
 * This event occurs when your robot should paint, where the {@link
 * Robot#onPaint(Graphics2D) onPaint()} is called on your robot.
 * </p>
 * You can use this event for setting the event priority by calling
 * {@link AdvancedRobot#setEventPriority(String, int)
 * setEventPriority("PaintEvent", priority)}
 *
 * @author Flemming N. Larsen (original)
 */
public class PaintEvent extends Event {

	/**
	 * Called by the game to create a new PaintEvent.
	 */
	public PaintEvent() {
		super();
	}
}
