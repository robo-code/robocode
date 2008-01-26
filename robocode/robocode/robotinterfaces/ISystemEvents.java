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
 *******************************************************************************/
package robocode.robotinterfaces;

import robocode.StatusEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Pavel Savara (original)
 */
public interface ISystemEvents {
    
    /**
     * This method is called every time the robot is painted. You should
     * override this method if you want to draw items for your robot on the
     * battle field, e.g. targets, virtual bullets etc.
     * <p>
     * This method is very useful for debugging your robot.
     * <p>
     * Note that the robot will only be painted if the "Paint" is enabled on the
     * robot's console window; otherwise the robot will never get painted (the
     * reason being that all robots might have graphical items that must be
     * painted, and then you might not be able to tell what graphical items that
     * have been painted for your robot).
     * <p>
     * Also note that the coordinate system for the graphical context where you
     * paint items fits for the Robocode coordinate system where (0, 0) is at
     * the buttom left corner of the battlefield, where X is towards right and Y
     * is upwards.
     *
     * @param g the graphics context to use for painting graphical items for the
     *    robot
     *
     * @since 1.1
     */
    public void onPaint(Graphics2D g);

    /**
     * This method is called when a key has been pressed.
     * <p>
     * See the sample.Interactive robot for an example of how to use key events.
     *
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     * @see #onKeyReleased(java.awt.event.KeyEvent)
     * @see #onKeyTyped(java.awt.event.KeyEvent)
     *
     * @since 1.3.4
     */
    public void onKeyPressed(KeyEvent e);

    /**
     * This method is called when a key has been released.
     * <p>
     * See the sample.Interactive robot for an example of how to use key events.
     *
     * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
     * @see #onKeyPressed(KeyEvent)
     * @see #onKeyTyped(KeyEvent)
     *
     * @since 1.3.4
     */
    public void onKeyReleased(KeyEvent e);

    /**
     * This method is called when a key has been typed (pressed and released).
     * <p>
     * See the sample.Interactive robot for an example of how to use key events.
     *
     * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
     * @see #onKeyPressed(KeyEvent)
     * @see #onKeyReleased(KeyEvent)
     *
     * @since 1.3.4
     */
    public void onKeyTyped(KeyEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseClicked(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseEntered(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseExited(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMousePressed(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseReleased(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseMoved(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseDragged(MouseEvent e);

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
     *
     * @since 1.3.4
     */
    public void onMouseWheelMoved(MouseWheelEvent e);

    /**
     * This method is called every turn to provide the current robot status as
     * a complete snapshot of the robot's state at that specific time.
     * <p>
     * The main benefit of this method is that you'll automatically receive all
     * the current data values of the robot like e.g. the x and y coordinate,
     * heading, gun heat etc., which are grouped into the exact same time/turn.
     * <p>
     * This is the only way to map the robots data values to a specific time.
     * For example, it is not possible to determine the exact time of the
     * robot's heading by calling first calling getTime() and then getHeading()
     * afterwards, as the time MIGHT change after between the getTime() and
     * getHeading() call.
     *
     * @param e the event containing the robot status and the time it was
     *    provided.
     *
     * @since 1.5
     */
    public void onStatus(StatusEvent e);
}
