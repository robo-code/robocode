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
    /// <see cref="IInteractiveRobot"/>.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// <seealso cref="IInteractiveRobot"/>
    /// @since 1.6
    /// </summary>
    public interface IInteractiveEvents
    {
        /// <summary>
        /// This method is called when a key has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        /// <seealso cref="OnKeyReleased(KeyEvent)"/>
        /// <seealso cref="OnKeyTyped(KeyEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnKeyPressed(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        /// <seealso cref="OnKeyPressed(KeyEvent)"/>
        /// <seealso cref="OnKeyTyped(KeyEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnKeyReleased(KeyEvent evnt);

        /// <summary>
        /// This method is called when a key has been typed (pressed and released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// key events.
        /// <seealso cref="OnKeyPressed(KeyEvent)"/>
        /// <seealso cref="OnKeyReleased(KeyEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnKeyTyped(KeyEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been clicked (pressed and
        /// released).
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseClicked(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has entered the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseEntered(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has exited the battle view.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseExited(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMousePressed(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been released.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseReleased(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse has been moved.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseMoved(MouseEvent evnt);

        /// <summary>
        /// This method is called when a mouse button has been pressed and then
        /// dragged.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseWheelMoved(MouseWheelMovedEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseDragged(MouseEvent evnt);

        /// <summary>
        /// This method is called when the mouse wheel has been rotated.
        /// <p/>
        /// See the {@code sample.Interactive} robot for an example of how to use
        /// mouse events.
        /// <seealso cref="OnMouseMoved(MouseEvent)"/>
        /// <seealso cref="OnMousePressed(MouseEvent)"/>
        /// <seealso cref="OnMouseReleased(MouseEvent)"/>
        /// <seealso cref="OnMouseClicked(MouseEvent)"/>
        /// <seealso cref="OnMouseEntered(MouseEvent)"/>
        /// <seealso cref="OnMouseExited(MouseEvent)"/>
        /// <seealso cref="OnMouseDragged(MouseEvent)"/>
        /// </summary>
        /// <param name="evnt">holds details about current event</param>
        void OnMouseWheelMoved(MouseWheelMovedEvent evnt);
    }
}

//happy