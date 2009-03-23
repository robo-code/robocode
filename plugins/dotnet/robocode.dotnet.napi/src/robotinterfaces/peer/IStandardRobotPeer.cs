namespace robocode.robotinterfaces.peer
{
    public interface IStandardRobotPeer : IBasicRobotPeer
    {
        void rescan();
        void resume();
        void setAdjustGunForBodyTurn(bool b);
        void setAdjustRadarForBodyTurn(bool b);
        void setAdjustRadarForGunTurn(bool b);
        void stop(bool b);
        void turnRadar(double d);
    }
}