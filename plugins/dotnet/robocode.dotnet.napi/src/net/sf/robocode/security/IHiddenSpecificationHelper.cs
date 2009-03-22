using robocode.control;

namespace net.sf.robocode.security
{
    public interface IHiddenSpecificationHelper
    {
        RobotSpecification createSpecification(object obj, string str1, string str2, string str3, string str4,
                                               string str5, string str6, string str7, string str8);

        object getFileSpecification(RobotSpecification rs);
        string getTeamName(RobotSpecification rs);
        void setTeamName(RobotSpecification rs, string str);
    }
}