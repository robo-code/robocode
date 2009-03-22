namespace robocode.control.snapshot
{
    public interface IBulletSnapshot
    {
        int getBulletId();
        int getColor();
        int getExplosionImageIndex();
        int getFrame();
        double getPaintX();
        double getPaintY();
        double getPower();
        BulletState getState();
        double getX();
        double getY();
        bool isExplosion();
    }
}