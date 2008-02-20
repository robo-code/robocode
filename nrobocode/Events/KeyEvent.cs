using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class KeyEvent : java.awt.@event.KeyEvent
    {
        public System.Windows.Forms.KeyEventArgs realEvent;

        public KeyEvent(System.Windows.Forms.KeyEventArgs src, bool down)
            //TODO modifiers
            : base(new FakeComponent(), down ? KEY_PRESSED : KEY_RELEASED, 0, 0, (int)src.KeyCode, (char)0)
        {
            realEvent = new System.Windows.Forms.KeyEventArgs(src.KeyData);
        }
    }
}
