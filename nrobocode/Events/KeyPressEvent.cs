using System;
using System.Collections.Generic;
using System.Text;
using java.awt;

namespace nrobocode.Events
{
    public class KeyPressEvent : java.awt.@event.KeyEvent
    {
        public System.Windows.Forms.KeyPressEventArgs realEvent;

        public KeyPressEvent(System.Windows.Forms.KeyPressEventArgs src)
            : base(new FakeComponent(), 0, 0, 0, 0, src.KeyChar)
        {
            realEvent = new System.Windows.Forms.KeyPressEventArgs(src.KeyChar);
        }
    }
}
