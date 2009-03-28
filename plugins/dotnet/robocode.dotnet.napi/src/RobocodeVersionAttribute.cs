using System;
using System.Collections.Generic;
using System.Text;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class RobocodeVersionAttribute : Attribute
    {
        private readonly Version version;

        public Version Version
        {
            get { return version; }
        }

        public RobocodeVersionAttribute(int major, int minor, int release)
        {
            this.version = new Version(major, minor, release);
        }

        public RobocodeVersionAttribute(Version version)
        {
            this.version = version;
        }
    }
}
