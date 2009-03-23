namespace robocode.robotinterfaces
{
    public interface ITeamRobot : IBasicRobot, IAdvancedRobot
    {
        ITeamEvents getTeamEventListener();
    }
}