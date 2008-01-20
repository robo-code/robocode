using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public sealed class AuthorNameAttribute : Attribute
    {
        // Fields
        private string m_AuthorName;

        // Methods
        public AuthorNameAttribute(string AuthorName)
        {
            this.m_AuthorName = AuthorName;
        }

        // Properties
        public string AuthorName
        {
            get { return this.m_AuthorName; }
        }
    }
}


