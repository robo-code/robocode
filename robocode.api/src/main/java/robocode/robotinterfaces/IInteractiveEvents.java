/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IInteractiveEvents {

	/**
	 * This method is called when a key has been pressed.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * key events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 * @see #onKeyReleased(KeyEvent)
	 * @see #onKeyTyped(KeyEvent)
	 * @since 1.3.4
	 */
	void onKeyPressed(KeyEvent event);

	/**
	 * This method is called when a key has been released.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * key events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyTyped(KeyEvent)
	 * @since 1.3.4
	 */
	void onKeyReleased(KeyEvent event);

	/**
	 * This method is called when a key has been typed (pressed and released).
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * key events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 * @see #onKeyPressed(KeyEvent)
	 * @see #onKeyReleased(KeyEvent)
	 * @since 1.3.4
	 */
	void onKeyTyped(KeyEvent event);

	/**
	 * This method is called when a mouse button has been clicked (pressed and
	 * released).
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseClicked(MouseEvent event);

	/**
	 * This method is called when the mouse has entered the battle view.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseEntered(MouseEvent event);

	/**
	 * This method is called when the mouse has exited the battle view.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseExited(MouseEvent event);

	/**
	 * This method is called when a mouse button has been pressed.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMousePressed(MouseEvent event);

	/**
	 * This method is called when a mouse button has been released.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseReleased(MouseEvent event);

	/**
	 * This method is called when the mouse has been moved.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseMoved(MouseEvent event);

	/**
	 * This method is called when a mouse button has been pressed and then
	 * dragged.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseWheelMoved(MouseWheelEvent)
	 * @since 1.3.4
	 */
	void onMouseDragged(MouseEvent event);

	/**
	 * This method is called when the mouse wheel has been rotated.
	 * <p>
	 * See the {@code sample.Interactive} robot for an example of how to use
	 * mouse events.
	 *
	 * @param event holds details about current event
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
	 * @see #onMouseMoved(MouseEvent)
	 * @see #onMousePressed(MouseEvent)
	 * @see #onMouseReleased(MouseEvent)
	 * @see #onMouseClicked(MouseEvent)
	 * @see #onMouseEntered(MouseEvent)
	 * @see #onMouseExited(MouseEvent)
	 * @see #onMouseDragged(MouseEvent)
	 */
	void onMouseWheelMoved(MouseWheelEvent event);
}
