using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Assembly, AllowMultiple = false)]
    public sealed class AuthorEmailAttribute : Attribute
    {
        // Fields
        private string m_AuthorEmail;

        // Methods
        public AuthorEmailAttribute(string AuthorEmail)
        {
            this.m_AuthorEmail = AuthorEmail;
        }

        // Properties
        public string AuthorEmail
        {
            get { return this.m_AuthorEmail; }
        }
    }
}


