namespace robocode.robotinterfaces
{
    public interface IPaintRobot : IBasicRobot
    {
        IPaintEvents getPaintEventListener();
    }
}