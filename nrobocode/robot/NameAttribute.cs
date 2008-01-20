using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public sealed class NameAttribute : Attribute
    {
        // Fields
        private string m_Name;

        // Methods
        public NameAttribute(string Name)
        {
            this.m_Name = Name;
        }

        // Properties
        public string Name
        {
            get { return this.m_Name; }
        }
    }
}


