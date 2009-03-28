using System;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class DescriptionAttribute : Attribute
    {
        private readonly string description;

        public string Description
        {
            get { return description; }
        }

        public DescriptionAttribute(string description)
        {
            this.description = description;
        }
    }
}
