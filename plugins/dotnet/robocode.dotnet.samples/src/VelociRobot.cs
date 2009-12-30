using robocode;

namespace samplecs
{
    /// <summary>
    /// This is a sample of a robot using the RateControlRobot class by Joshua Galecki
    /// </summary>
    public class VelociRobot : RateControlRobot
    {
        private int turnCounter;

        public override void Run()
        {
            turnCounter = 0;
            GunRotationRate = (15);

            while (true)
            {
                if (turnCounter%64 == 0)
                {
                    // Straighten out, if we were hit by a bullet and are turning
                    TurnRate = 0;
                    // Go forward with a velocity of 4
                    VelocityRate = 4;
                }
                if (turnCounter%64 == 32)
                {
                    // Go backwards, faster
                    VelocityRate = -6;
                }
                turnCounter++;
                Execute();
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }

        public override void OnHitByBullet(HitByBulletEvent e)
        {
            // Turn to confuse the other robot
            TurnRate = 5;
        }

        public override void OnHitWall(HitWallEvent e)
        {
            // Move away from the wall
            VelocityRate = (-1*VelocityRate);
        }
    }
}
