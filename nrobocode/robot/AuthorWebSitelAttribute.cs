using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public sealed class AuthorWebSiteAttribute : Attribute
    {
        // Fields
        private string m_AuthorWebSite;

        // Methods
        public AuthorWebSiteAttribute(string AuthorWebSite)
        {
            this.m_AuthorWebSite = AuthorWebSite;
        }

        // Properties
        public string AuthorWebSite
        {
            get { return this.m_AuthorWebSite; }
        }
    }
}


