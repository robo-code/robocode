namespace robocode.robotinterfaces
{
    public interface IBasicEvents
    {
        void onBulletHit(BulletHitEvent bhe);
        void onBulletHitBullet(BulletHitBulletEvent bhbe);
        void onBulletMissed(BulletMissedEvent bme);
        void onDeath(DeathEvent de);
        void onHitByBullet(HitByBulletEvent hbbe);
        void onHitRobot(HitRobotEvent hre);
        void onHitWall(HitWallEvent hwe);
        void onRobotDeath(RobotDeathEvent rde);
        void onScannedRobot(ScannedRobotEvent sre);
        void onStatus(StatusEvent se);
        void onWin(WinEvent we);
    }
}