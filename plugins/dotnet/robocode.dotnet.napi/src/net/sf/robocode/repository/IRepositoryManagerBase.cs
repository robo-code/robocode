using robocode.control;

namespace net.sf.robocode.repository
{
    public interface IRepositoryManagerBase
    {
        RobotSpecification[] getSpecifications();
        RobotSpecification[] loadSelectedRobots(string str);
    }
}