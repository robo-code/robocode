using System;
using System.Collections.Generic;
using System.Text;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class RobotLanguageAttribute : Attribute
    {
        private readonly string language;

        public string Language
        {
            get { return language; }
        }

        public RobotLanguageAttribute(string language)
        {
            this.language = language;
        }
    }
}
