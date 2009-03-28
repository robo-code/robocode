using System;
using System.Collections.Generic;
using System.Text;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class RobotNameAttribute : Attribute
    {
        private readonly string name;

        public string Name
        {
            get { return name; }
        }

        public RobotNameAttribute(string name)
        {
            this.name = name;
        }
    }
}
