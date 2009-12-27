using System.Drawing;
using robocode;

namespace samplecs
{
    /// <summary>
    ///  PaintingRobot - a sample robot that demonstrates the onPaint() and
    ///  getGraphics() methods.
    ///  Also demonstrate feature of debugging properties on RobotDialog
    ///  <p/>
    ///  Moves in a seesaw motion, and spins the gun around at each end.
    ///  When painting is enabled for this robot, a red circle will be painted
    ///  around this robot.
    /// </summary>
    public class PaintingRobot : Robot
    {
        /// <summary>
        /// PaintingRobot's run method - Seesaw
        /// </summary>
        public override void run()
        {
            while (true)
            {
                ahead(100);
                turnGunRight(360);
                back(100);
                turnGunRight(360);
            }
        }

        /// <summary>
        /// Fire when we see a robot
        /// </summary>
        public override void onScannedRobot(ScannedRobotEvent e)
        {
            // demonstrate feature of debugging properties on RobotDialog
            setDebugProperty("lastScannedRobot", e.getName() + " at " + e.getBearing() + " degrees at time " + getTime());

            fire(1);
        }

        /// <summary>
        /// We were hit!  Turn perpendicular to the bullet, 
        /// so our seesaw might avoid a future shot. 
        /// In addition, draw orange circles where we were hit.
        /// </summary>
        public override void onHitByBullet(HitByBulletEvent e)
        {
            // demonstrate feature of debugging properties on RobotDialog
            setDebugProperty("lastHitBy",
                             e.getName() + " with power of bullet " + e.getPower() + " at time " + getTime());

            // show how to remove debugging property
            setDebugProperty("lastScannedRobot", null);

            // gebugging by painting to battle view
            IGraphics g = getGraphics();

            g.DrawEllipse(Pens.Orange, (int) (getX() - 55), (int) (getY() - 55), 110, 110);
            g.DrawEllipse(Pens.Orange, (int) (getX() - 56), (int) (getY() - 56), 112, 112);
            g.DrawEllipse(Pens.Orange, (int) (getX() - 59), (int) (getY() - 59), 118, 118);
            g.DrawEllipse(Pens.Orange, (int) (getX() - 60), (int) (getY() - 60), 120, 120);

            turnLeft(90 - e.getBearing());
        }

        /// <summary>
        /// Paint a red circle around our PaintingRobot
        /// </summary>
        public override void onPaint(IGraphics g)
        {
            g.DrawEllipse(Pens.Red, (int) (getX() - 50), (int) (getY() - 50), 100, 100);
            g.FillEllipse(new SolidBrush(Color.FromArgb(0, 0xFF, 0, 30)), (int) (getX() - 60), (int) (getY() - 60), 120,
                          120);
        }
    }
}
