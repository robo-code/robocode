using System;

namespace robocode.exception
{
    public class WinException : Exception
    {
        private const long serialVersionUID = 1L;

        public WinException()
        {
        }

        public WinException(string s) : base(s)
        {
        }
    }
}