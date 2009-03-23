using System;

namespace robocode.exception
{
    public class RobotException : Exception
    {
        private const long serialVersionUID = 1L;

        public RobotException()
        {
        }

        public RobotException(string s) : base(s)
        {
        }
    }
}