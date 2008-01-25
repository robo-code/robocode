using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Assembly, AllowMultiple = false)]
    public sealed class NameAttribute : Attribute
    {
        // Fields
        private Type robot;

        // Methods
        public NameAttribute(Type robot)
        {
            this.robot = robot;
        }

        // Properties
        public string Name
        {
            get { return robot.FullName; }
        }

        public Type Type
        {
            get { return robot; }
        }
    }
}


