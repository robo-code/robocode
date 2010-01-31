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
    /// <see cref="IInteractiveRobot}.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// <seealso cref="IInteractiveRobot
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
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.KeyListener#keyPressed(KeyEvent)
        /// <seealso cref="OnKeyReleased(KeyEvent)
        /// <seealso cref="OnKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void OnKeyPressed(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.KeyListener#keyReleased(KeyEvent)
        /// <seealso cref="OnKeyPressed(KeyEvent)
        /// <seealso cref="OnKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void OnKeyReleased(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been typed (pressed and released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.KeyListener#keyTyped(KeyEvent)
        /// <seealso cref="OnKeyPressed(KeyEvent)
        /// <seealso cref="OnKeyReleased(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void OnKeyTyped(KeyEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been clicked (pressed and
        /// released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseListener#mouseClicked(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseClicked(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has entered the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseListener#mouseEntered(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseEntered(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has exited the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseListener#mouseExited(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseExited(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseListener#mousePressed(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMousePressed(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseListener#mouseReleased(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseReleased(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has been moved.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseMoved(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed and then
        /// dragged.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseDragged(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse wheel has been rotated.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// <param name="evnt holds details about current event
        /// <seealso cref="java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
        /// <seealso cref="OnMouseMoved(MouseEvent)
        /// <seealso cref="OnMousePressed(MouseEvent)
        /// <seealso cref="OnMouseReleased(MouseEvent)
        /// <seealso cref="OnMouseClicked(MouseEvent)
        /// <seealso cref="OnMouseEntered(MouseEvent)
        /// <seealso cref="OnMouseExited(MouseEvent)
        /// <seealso cref="OnMouseDragged(MouseEvent)
        /// </summary>
        void OnMouseWheelMoved(MouseWheelMovedEvent evnt);
    }
}

//happy