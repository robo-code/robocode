using IKVM.Attributes;

namespace robocode.robotinterfaces
{
    [Implements(new[] {"robocode.robotinterfaces.IAdvancedRobot"})]
    public interface ITeamRobot : IBasicRobot, IAdvancedRobot
    {
        ITeamEvents getTeamEventListener();
    }
}