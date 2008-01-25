using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Assembly, AllowMultiple = false)]
    public sealed class VersionAttribute : Attribute
    {
        private Version m_Version;

        public VersionAttribute(Version Version)
        {
            this.m_Version = Version;
        }

        public VersionAttribute(string version)
        {
            this.m_Version = new Version(version);
        }

        public Version Version
        {
            get { return this.m_Version; }
        }
    }
}


