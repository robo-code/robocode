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
    /// Super class of all events that originates from the keyboard.
    /// </summary>
    [Serializable]
    public abstract class KeyEvent : Event
    {
        private readonly char keyChar;
        private readonly int keyCode;
        private readonly int keyLocation;
        private readonly int id;
        private readonly int modifiersEx;
        private readonly long when;

        /// <summary>
        /// Called by game
        /// </summary>
        protected KeyEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiersEx, long when)
        {
            this.keyChar = keyChar;
            this.keyCode = keyCode;
            this.keyLocation = keyLocation;
            this.id = id;
            this.modifiersEx = modifiersEx;
            this.when = when;
        }

        /// <summary>
        /// Char of they key pressed
        /// </summary>
        public char KeyChar
        {
            get { return keyChar; }
        }

        /// <summary>
        /// <see cref="Keys"/>
        /// </summary>
        public int KeyCode
        {
            get { return keyCode; }
        }

        internal int KeyLocation
        {
            get { return keyLocation; }
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