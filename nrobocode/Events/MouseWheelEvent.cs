namespace nrobocode.Events
{

    public class MouseWheelEvent : java.awt.@event.MouseWheelEvent
    {
        public System.Windows.Forms.MouseEventArgs realEvent;

        public MouseWheelEvent(System.Windows.Forms.MouseEventArgs src)
            : base(new FakeComponent(), 0, 0, 0, src.X, src.Y, src.Clicks, false, WHEEL_BLOCK_SCROLL, (int)src.Delta, (int)src.Delta)
        {
            realEvent = new System.Windows.Forms.MouseEventArgs(src.Button, src.Clicks, src.X, src.Y, src.Delta);
        }
    }
}
