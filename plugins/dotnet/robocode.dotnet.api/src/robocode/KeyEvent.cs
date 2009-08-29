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
    ///
    /// @author Pavel Savara (original)
    /// @since 1.6.1
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

        public char getKeyChar()
        {
            return keyChar;
        }

        public int getKeyCode()
        {
            return keyCode;
        }

        public int getKeyLocation()
        {
            return keyLocation;
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