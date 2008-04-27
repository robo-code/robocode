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
package robotapi;


import robotapi.Event;

import java.awt.Graphics2D;


/**
 * This event is sent to {@link robotapi.Robot#onPaint(Graphics2D) onPaint(Graphics2D)}
 * when your robot should paint.
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
