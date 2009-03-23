
using System;

namespace robocode.exception
{
    public class DisabledException : Exception
    {
        private const long serialVersionUID = 1L;

        public DisabledException()
        {
        }

        public DisabledException(string s) : base(s)
        {
        }
    }
}