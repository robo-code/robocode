using System;
using nrobocode.robot;
using robocode;

[assembly: Description("A sample robot\n     Moves in a seesaw motion, and spins the gun around at each end\n     Moves perpendicular to the direction of a bullet that hits it")]
[assembly: WebPage("http://robocode.sourceforge.net/")]
[assembly: Version("1.4")]
[assembly: SourceIncluded(true)]
[assembly: AuthorName("Flemming N. Larsen, Pavel Savara")]
[assembly: Name(typeof(nsample.MyFirstJuniorRobot))]

namespace nsample
{
    public class MyFirstJuniorRobot : JuniorRobot
    {
        public override void run()
        {
            setColors(green, black, blue);

            Console.WriteLine("Cool, we are running !");

            // Seesaw forever
            while (true)
            {
                ahead(100); // Move ahead 100
                turnGunRight(360); // Spin gun around
                back(100); // Move back 100
                turnGunRight(360); // Spin gun around
            }
        }

        public override void onScannedRobot()
        {
            Console.WriteLine("Attaaaaaaack ");

            // Turn gun to point at the scanned robot
            turnGunTo(scannedAngle);

            // Fire!
            fire(1);
        }

        public override void onHitByBullet()
        {
            Console.WriteLine("Ouch !");

            // Move ahead 100 and in the same time turn left papendicular to the bullet
            turnAheadLeft(100, 90 - hitByBulletBearing);
        }
    }
}
