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
namespace robocode.robotinterfaces
{
    /// <summary>
    /// An evnt interface for receiving interactive events with an
    /// {@link IInteractiveRobot}.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see IInteractiveRobot
    /// @since 1.6
    /// </summary>
    public interface IInteractiveEvents
    {
        /// <summary>
        /// This method is called when a key has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.KeyListener#keyPressed(KeyEvent)
        /// @see #onKeyReleased(KeyEvent)
        /// @see #onKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void onKeyPressed(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.KeyListener#keyReleased(KeyEvent)
        /// @see #onKeyPressed(KeyEvent)
        /// @see #onKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void onKeyReleased(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been typed (pressed and released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.KeyListener#keyTyped(KeyEvent)
        /// @see #onKeyPressed(KeyEvent)
        /// @see #onKeyReleased(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void onKeyTyped(KeyEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been clicked (pressed and
        /// released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseClicked(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has entered the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseEntered(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has exited the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseExited(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseExited(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mousePressed(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMousePressed(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseReleased(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has been moved.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseMoved(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed and then
        /// dragged.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void onMouseDragged(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse wheel has been rotated.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
        /// @see #onMouseMoved(MouseEvent)
        /// @see #onMousePressed(MouseEvent)
        /// @see #onMouseReleased(MouseEvent)
        /// @see #onMouseClicked(MouseEvent)
        /// @see #onMouseEntered(MouseEvent)
        /// @see #onMouseExited(MouseEvent)
        /// @see #onMouseDragged(MouseEvent)
        /// </summary>
        void onMouseWheelMoved(MouseWheelMovedEvent evnt);
    }
}
//happy