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
    public class MouseWheelEvent : java.awt.@event.MouseWheelEvent
    {
        public MouseEventArgs realEvent;

        public MouseWheelEvent(MouseEventArgs src)
            : base(
                new FakeComponent(), 0, 0, 0, src.X, src.Y, src.Clicks, false, WHEEL_BLOCK_SCROLL, (int)src.Delta,
                (int)src.Delta)
        {
            realEvent = new MouseEventArgs(src.Button, src.Clicks, src.X, src.Y, src.Delta);
        }
    }
}