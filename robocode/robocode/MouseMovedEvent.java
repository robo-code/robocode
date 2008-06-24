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
 * A MouseMovedEvent is sent to {@link Robot#onMouseMoved(java.awt.event.MouseEvent)
 * Robot#onMouseMoved()} when the mouse has moved inside the battle view.
 *
 * @author Pavel Savara (original)
 *
 * @see MouseClickedEvent
 * @see MousePressedEvent
 * @see MouseReleasedEvent
 * @see MouseEnteredEvent
 * @see MouseExitedEvent
 * @see MouseDraggedEvent
 * @see MouseWheelMovedEvent
 *
 * @since 1.6.1
 */
public final class MouseMovedEvent extends MouseEvent {

	/**
	 * Called by the game to create a new MouseMovedEvent.
	 *
     * @param source the source mouse event originating from the AWT.
     */
	public MouseMovedEvent(java.awt.event.MouseEvent source) {
		super(source);
	}
}
