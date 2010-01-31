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
    /// Super class of all events that originates from the keyboard.
    /// </summary>
    [Serializable]
    public abstract class KeyEvent : Event
    {
        protected readonly char keyChar;
        protected readonly int keyCode;
        protected readonly int keyLocation;
        protected readonly int id;
        protected readonly int modifiersEx;
        protected readonly long when;

        protected KeyEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiersEx, long when)
        {
            this.keyChar = keyChar;
            this.keyCode = keyCode;
            this.keyLocation = keyLocation;
            this.id = id;
            this.modifiersEx = modifiersEx;
            this.when = when;
        }

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

        public int KeyLocation
        {
            get { return keyLocation; }
        }

        public int ID
        {
            get { return id; }
        }

        public int ModifiersEx
        {
            get { return modifiersEx; }
        }

        public long When
        {
            get { return when; }
        }
    }
}
//doc