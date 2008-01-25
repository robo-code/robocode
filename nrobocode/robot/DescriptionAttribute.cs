using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Assembly, AllowMultiple = false)]
    public sealed class DescriptionAttribute : Attribute
    {
        // Fields
        private string m_Description;

        // Methods
        public DescriptionAttribute(string Description)
        {
            this.m_Description = Description;
        }

        // Properties
        public string Description
        {
            get { return this.m_Description; }
        }
    }
}


