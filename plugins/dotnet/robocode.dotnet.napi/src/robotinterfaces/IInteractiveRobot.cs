namespace robocode.robotinterfaces
{
    public interface IInteractiveRobot : IBasicRobot
    {
        IInteractiveEvents getInteractiveEventListener();
    }
}