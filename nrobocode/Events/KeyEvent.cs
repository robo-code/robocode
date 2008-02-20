// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
//  - Initial implementation
// *****************************************************************************
using System.Windows.Forms;

namespace nrobocode.Events
{
    public class KeyEvent : java.awt.@event.KeyEvent
    {
        public KeyEventArgs realEvent;

        public KeyEvent(KeyEventArgs src, bool down) //TODO modifiers
            : base(new FakeComponent(), down ? KEY_PRESSED : KEY_RELEASED, 0, 0, (int)src.KeyCode, (char)0)
        {
            realEvent = new KeyEventArgs(src.KeyData);
        }
    }
}