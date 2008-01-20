using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public sealed class SourceIncludedAttribute : Attribute
    {
        // Fields
        private bool m_SourceIncluded;

        // Methods
        public SourceIncludedAttribute(bool SourceIncluded)
        {
            this.m_SourceIncluded = SourceIncluded;
        }

        // Properties
        public bool SourceIncluded
        {
            get { return this.m_SourceIncluded; }
        }
    }
}


