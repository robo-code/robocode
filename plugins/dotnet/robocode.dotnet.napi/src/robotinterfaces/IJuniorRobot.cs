using IKVM.Attributes;

namespace robocode.robotinterfaces
{
    [Implements(new[] {"robocode.robotinterfaces.IBasicRobot"})]
    public interface IJuniorRobot : IBasicRobot
    {
    }
}