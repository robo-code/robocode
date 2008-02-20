using System.Windows.Forms;
using java.awt;

namespace nrobocode.Events
{
    public class MouseEvent : java.awt.@event.MouseEvent
    {
        public System.Windows.Forms.MouseEventArgs realEvent;

        public MouseEvent(System.Windows.Forms.MouseEventArgs src, int id)
            : base(new FakeComponent(), id, 0, 0, src.X, src.Y, src.Clicks, false, ConvertButton(src.Button))
        {
            realEvent = new System.Windows.Forms.MouseEventArgs(src.Button, src.Clicks, src.X, src.Y, src.Delta);
        }

        public static int ConvertButton(MouseButtons bt)
        {
            switch(bt)
            {
                case MouseButtons.Left :
                    return BUTTON1;
                case MouseButtons.Middle :
                    return BUTTON2;
                case MouseButtons.Right :
                    return BUTTON3;
                default :
                    return NOBUTTON;
            }
        }
    }
}
