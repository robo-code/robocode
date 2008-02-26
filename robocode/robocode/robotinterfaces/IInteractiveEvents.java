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
package robocode.robotinterfaces;


import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * An event interface for receiving interactive events with an
 * {@link IInteractiveRobot}.
 *
 * @see IInteractiveRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IInteractiveEvents {
    
	/**
	 * This method is called when a key has been pressed.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 *
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 * @see #onKeyReleased(java.awt.event.KeyEvent)
	 * @see #onKeyTyped(java.awt.event.KeyEvent)
	 */
	void onKeyPressed(KeyEvent e);

	/**
	 * This method is called when a key has been released.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 *
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyTyped(KeyEvent)
	 */
	void onKeyReleased(KeyEvent e);

	/**
	 * This method is called when a key has been typed (pressed and released).
	 * <p>
	 * See the sample.Interactive robot for an example of how to use key events.
	 *
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyReleased(KeyEvent)
	 */
	void onKeyTyped(KeyEvent e);

	/**
	 * This method is called when a mouse button has been clicked (pressed and released).
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * @see #onMouseMoved(java.awt.event.MouseEvent)
	 * @see #onMousePressed(java.awt.event.MouseEvent)
	 * @see #onMouseReleased(java.awt.event.MouseEvent)
	 * @see #onMouseEntered(java.awt.event.MouseEvent)
	 * @see #onMouseExited(java.awt.event.MouseEvent)
	 * @see #onMouseDragged(java.awt.event.MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseClicked(MouseEvent e);

	/**
	 * This method is called when the mouse has entered the battle view.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseEntered(MouseEvent e);

	/**
	 * This method is called when the mouse has exited the battle view.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseExited(MouseEvent e);

	/**
	 * This method is called when a mouse button has been pressed.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMousePressed(MouseEvent e);

	/**
	 * This method is called when a mouse button has been released.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseReleased(MouseEvent e);

	/**
	 * This method is called when the mouse has been moved.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseMoved(MouseEvent e);

	/**
	 * This method is called when a mouse button has been pressed and then dragged.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	void onMouseDragged(MouseEvent e);

	/**
	 * This method is called when the mouse wheel has been rotated.
	 * <p>
	 * See the sample.Interactive robot for an example of how to use mouse events.
	 *
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 */
	void onMouseWheelMoved(MouseWheelEvent e);
}
