using System;
using System.Collections.Generic;
using System.Text;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class AuthorNameAttribute : Attribute
    {
        private readonly string name;

        public string Name
        {
            get { return name; }
        }

        public AuthorNameAttribute(string name)
        {
            this.name = name;
        }
    }
}
