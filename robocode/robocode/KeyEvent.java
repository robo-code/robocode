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


/**
 * Super class of all events that originates from the keyboard.
 *
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public abstract class KeyEvent extends Event {
	private java.awt.event.KeyEvent source;

	/**
	 * Called by the game to create a new KeyEvent.
	 *
	 * @param source the source key event originating from the AWT.
	 */
	public KeyEvent(java.awt.event.KeyEvent source) {
		this.source = source;
	}

	/**
	 * Do not call this method!
	 * <p/>
	 * This method is used by the game to determine the type of the source key
	 * event that occurred in the AWT.
	 *
	 * @return the source key event that originates from the AWT.
	 */
	public java.awt.event.KeyEvent getSourceEvent() {
		return source;
	}
}
