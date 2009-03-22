using IKVM.Attributes;

namespace robocode.robotinterfaces.peer
{
    [Implements(new[] {"robocode.robotinterfaces.peer.IBasicRobotPeer"})]
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