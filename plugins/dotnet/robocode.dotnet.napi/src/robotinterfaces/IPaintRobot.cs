using IKVM.Attributes;

namespace robocode.robotinterfaces
{
    [Implements(new[] {"robocode.robotinterfaces.IBasicRobot"})]
    public interface IPaintRobot : IBasicRobot
    {
        IPaintEvents getPaintEventListener();
    }
}