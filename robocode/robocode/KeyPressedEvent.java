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
 * A KeyPressedEvent is sent to {@link Robot#onKeyPressed(java.awt.event.KeyEvent)
 * onKeyPressed()} when a key has been pressed on the keyboard.
 *
 * @author Pavel Savara (original)
 *
 * @see KeyReleasedEvent
 * @see KeyTypedEvent
 *
 * @since 1.6.1
 */
public final class KeyPressedEvent extends KeyEvent {

	/**
	 * Called by the game to create a new KeyPressedEvent.
	 *
     * @param source the source key event originating from the AWT.
     */
	public KeyPressedEvent(java.awt.event.KeyEvent source) {
		super(source);
	}
}
