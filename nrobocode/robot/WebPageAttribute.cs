using System;

namespace nrobocode.robot
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public sealed class WebPageAttribute : Attribute
    {
        // Fields
        private string m_webPage;

        // Methods
        public WebPageAttribute(string webPage)
        {
            this.m_webPage = webPage;
        }

        // Properties
        public string webPage
        {
            get { return this.m_webPage; }
        }
    }
}


