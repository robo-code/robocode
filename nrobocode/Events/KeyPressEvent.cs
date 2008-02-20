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
    public class KeyPressEvent : java.awt.@event.KeyEvent
    {
        public KeyPressEventArgs realEvent;

        public KeyPressEvent(KeyPressEventArgs src)
            : base(new FakeComponent(), 0, 0, 0, 0, src.KeyChar)
        {
            realEvent = new KeyPressEventArgs(src.KeyChar);
        }
    }
}