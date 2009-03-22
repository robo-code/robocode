using IKVM.Attributes;

namespace robocode.robotinterfaces.peer
{
    [Implements(new[] {"robocode.robotinterfaces.peer.IBasicRobotPeer"})]
    public interface IJuniorRobotPeer : IBasicRobotPeer
    {
        void turnAndMove(double d1, double d2);
    }
}