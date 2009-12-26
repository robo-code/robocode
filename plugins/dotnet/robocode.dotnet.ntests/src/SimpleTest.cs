using System.Drawing;
using System.Drawing.Drawing2D;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.nio;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class SimpleTest : TestBase
    {

        public void Graphics()
        {
            GraphicsProxy sg = new GraphicsProxy();
		    sg.setPaintingEnabled(true);
            sg.DrawArc(Pens.Red, new RectangleF(0,0,80,80),-30,10);

            GraphicsPath path = new GraphicsPath();
            path.AddLine(99,98,78,3);
            sg.DrawPath(Pens.Blue, path);
            sg.DrawRectangle(Pens.Yellow, 20,20,30,50);

            sg.DrawLine(Pens.Black, 99, 3, 78, 3);
            sg.DrawRectangle(Pens.Black, 90, 20, 30, 50);

            Brush brush = new HatchBrush(HatchStyle.Vertical, Color.Cyan);
            sg.FillRectangle(brush, new RectangleF(20, 70, 30, 50));
            sg.FillEllipse(brush, new RectangleF(70, 70, 30, 50 ));

            byte[] readoutQueuedCalls = sg.readoutQueuedCalls();
            Assert.Greater(0, readoutQueuedCalls.Length);
        }
    }
}
