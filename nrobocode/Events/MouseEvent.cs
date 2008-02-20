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
    public class MouseEvent : java.awt.@event.MouseEvent
    {
        public MouseEventArgs realEvent;

        public MouseEvent(MouseEventArgs src, int id)
            : base(new FakeComponent(), id, 0, 0, src.X, src.Y, src.Clicks, false, ConvertButton(src.Button))
        {
            realEvent = new MouseEventArgs(src.Button, src.Clicks, src.X, src.Y, src.Delta);
        }

        public static int ConvertButton(MouseButtons bt)
        {
            switch (bt)
            {
                case MouseButtons.Left:
                    return BUTTON1;
                case MouseButtons.Middle:
                    return BUTTON2;
                case MouseButtons.Right:
                    return BUTTON3;
                default:
                    return NOBUTTON;
            }
        }
    }
}