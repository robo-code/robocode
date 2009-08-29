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
using System;

namespace robocode
{
    /// <summary>
    /// Super class of all events that originates from the mouse.
    ///
    /// @author Pavel Savara (original)
    /// @since 1.6.1
    /// </summary>
    [Serializable]
    public abstract class MouseEvent : Event
    {
        protected readonly int button;
        protected readonly int clickCount;
        protected readonly int x;
        protected readonly int y;
        protected readonly int id;
        protected readonly int modifiersEx;
        protected readonly long when;

        /// <summary>
        /// Called by the game to create a new MouseEvent.
        ///
        /// @param source the source mouse evnt originating from the AWT.
        /// </summary>
        protected MouseEvent(int button, int clickCount, int x, int y, int id, int modifiersEx, long when)
        {
            this.button = button;
            this.clickCount = clickCount;
            this.x = x;
            this.y = y;
            this.id = id;
            this.modifiersEx = modifiersEx;
            this.when = when;
        }

        public int getButton()
        {
            return button;
        }

        public int getClickCount()
        {
            return clickCount;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getID()
        {
            return id;
        }

        public int getModifiersEx()
        {
            return modifiersEx;
        }

        public long getWhen()
        {
            return when;
        }
    }
}

//happy