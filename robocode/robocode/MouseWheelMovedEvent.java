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

import java.awt.event.MouseWheelEvent;


/**
 * A MouseWheelMovedEvent is sent to {@link Robot#onMouseWheelMoved(MouseWheelEvent)
 * Robot#onMouseWheelMoved()} when the mouse wheel is rotated inside the battle view.
 *
 * @author Pavel Savara (original)
 *
 * @see MouseClickedEvent
 * @see MousePressedEvent
 * @see MouseReleasedEvent
 * @see MouseEnteredEvent
 * @see MouseExitedEvent
 * @see MouseMovedEvent
 * @see MouseDraggedEvent
 *
 * @since 1.6.1
 */
public final class MouseWheelMovedEvent extends MouseEvent {

	/**
	 * Called by the game to create a new MouseWheelMovedEvent.
	 *
     * @param source the source mouse event originating from the AWT.
     */
	public MouseWheelMovedEvent(java.awt.event.MouseEvent source) {
		super(source);
	}
}
