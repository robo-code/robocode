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
        /// @see #OnKeyReleased(KeyEvent)
        /// @see #OnKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void OnKeyPressed(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.KeyListener#keyReleased(KeyEvent)
        /// @see #OnKeyPressed(KeyEvent)
        /// @see #OnKeyTyped(KeyEvent)
        /// @since 1.3.4
        /// </summary>
        void OnKeyReleased(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been typed (pressed and released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.KeyListener#keyTyped(KeyEvent)
        /// @see #OnKeyPressed(KeyEvent)
        /// @see #OnKeyReleased(KeyEvent)
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
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseClicked(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has entered the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseEntered(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has exited the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseExited(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseExited(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mousePressed(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMousePressed(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseReleased(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has been moved.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
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
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseWheelMoved(MouseWheelEvent)
        /// @since 1.3.4
        /// </summary>
        void OnMouseDragged(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse wheel has been rotated.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        ///
        /// @param evnt holds details about current event
        /// @see java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent)
        /// @see #OnMouseMoved(MouseEvent)
        /// @see #OnMousePressed(MouseEvent)
        /// @see #OnMouseReleased(MouseEvent)
        /// @see #OnMouseClicked(MouseEvent)
        /// @see #OnMouseEntered(MouseEvent)
        /// @see #OnMouseExited(MouseEvent)
        /// @see #OnMouseDragged(MouseEvent)
        /// </summary>
        void OnMouseWheelMoved(MouseWheelMovedEvent evnt);
    }
}

//happy