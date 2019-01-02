/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;

namespace Robocode
{
    /// <summary>
    /// Super class of all events that originates from the mouse.
    /// </summary>
    [Serializable]
    public abstract class MouseEvent : Event
    {
        private readonly int button;
        private readonly int clickCount;
        private readonly int x;
        private readonly int y;
        private readonly int id;
        private readonly int modifiersEx;
        private readonly long when;

        /// <summary>
        /// Called by the game to create a new MouseEvent.
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

        /// <summary>
        /// Number of the button
        /// </summary>
        public int Button
        {
            get { return button; }
        }

        /// <summary>
        /// Click count
        /// </summary>
        public int ClickCount
        {
            get { return clickCount; }
        }

        /// <summary>
        /// Cursor coordinates
        /// </summary>
        public int X
        {
            get { return x; }
        }

        /// <summary>
        /// Cursor coordinates
        /// </summary>
        public int Y
        {
            get { return y; }
        }

        internal int ID
        {
            get { return id; }
        }

        internal int ModifiersEx
        {
            get { return modifiersEx; }
        }

        /// <summary>
        /// Age of the event
        /// </summary>
        public long When
        {
            get { return when; }
        }
    }
}
//doc