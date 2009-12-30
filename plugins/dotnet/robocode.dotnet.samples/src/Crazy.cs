using System.Drawing;
using robocode;

namespace samplecs
{
    /// <summary>
    ///   Crazy - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   This robot moves around in a crazy pattern
    /// </summary>
    public class Crazy : AdvancedRobot
    {
        private bool movingForward;

        /// <summary>
        ///   run: Crazy's main run function
        /// </summary>
        public override void Run()
        {
            BodyColor = (Color.FromArgb(0, 200, 0));
            GunColor = (Color.FromArgb(0, 150, 50));
            RadarColor = (Color.FromArgb(0, 100, 100));
            BulletColor = (Color.FromArgb(255, 255, 100));
            ScanColor = (Color.FromArgb(255, 200, 200));

            // Loop forever
            while (true)
            {
                // Tell the game we will want to move Ahead 40000 -- some large number
                SetAhead(40000);
                movingForward = true;
                // Tell the game we will want to turn right 90
                SetTurnRight(90);
                // At this point, we have indicated to the game that *when we do something*,
                // we will want to move Ahead and turn right.  That's what "set" means.
                // It is important to realize we have not done anything yet!
                // In order to actually move, we'll want to call a method that
                // takes real time, such as waitFor.
                // waitFor actually starts the action -- we start moving and turning.
                // It will not return until we have finished turning.
                WaitFor(new TurnCompleteCondition(this));
                // Note:  We are still moving Ahead now, but the turn is complete.
                // Now we'll turn the other way...
                SetTurnLeft(180);
                // ... and wait for the turn to finish ...
                WaitFor(new TurnCompleteCondition(this));
                // ... then the other way ...
                SetTurnRight(180);
                // .. and wait for that turn to finish.
                WaitFor(new TurnCompleteCondition(this));
                // then Back to the top to do it all again
            }
        }

        /**
         * onHitWall:  Handle collision with wall.
         */

        public override void OnHitWall(HitWallEvent e)
        {
            // Bounce off!
            reverseDirection();
        }

        /**
         * reverseDirection:  Switch from Ahead to Back & vice versa
         */

        public void reverseDirection()
        {
            if (movingForward)
            {
                SetBack(40000);
                movingForward = false;
            }
            else
            {
                SetAhead(40000);
                movingForward = true;
            }
        }

        /**
         * onScannedRobot:  Fire!
         */

        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }

        /// <summary>
        ///   onHitRobot:  Back up!
        /// </summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            // If we're moving the other robot, reverse!
            if (e.IsMyFault)
            {
                reverseDirection();
            }
        }
    }
}
