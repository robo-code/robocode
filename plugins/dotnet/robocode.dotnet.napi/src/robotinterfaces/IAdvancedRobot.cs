using IKVM.Attributes;

namespace robocode.robotinterfaces
{
    [Implements(new[] {"robocode.robotinterfaces.IBasicRobot"})]
    public interface IAdvancedRobot : IBasicRobot
    {
        IAdvancedEvents getAdvancedEventListener();
    }
}