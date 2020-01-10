/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


/**
 * Super class of all events that originates from the mouse.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public abstract class MouseEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final java.awt.event.MouseEvent source;

	/**
	 * Called by the game to create a new MouseEvent.
	 *
	 * @param source the source mouse event originating from the AWT.
	 */
	public MouseEvent(java.awt.event.MouseEvent source) {
		this.source = source;
	}

	/**
	 * Do not call this method!
	 * <p>
	 * This method is used by the game to determine the type of the source mouse
	 * event that occurred in the AWT.
	 *
	 * @return the source mouse event that originates from the AWT.
	 */
	public java.awt.event.MouseEvent getSourceEvent() {
		return source;
	}
}
