using System;
using System.Collections.Generic;
using System.Text;

namespace robocode
{
    [AttributeUsage(AttributeTargets.Class)]
    public class SourceIncludedAttribute : Attribute
    {
        private readonly bool included;

        public SourceIncludedAttribute(bool included)
        {
            this.included = included;
        }

        public bool Included
        {
            get { return included; }
        }

    }
}
