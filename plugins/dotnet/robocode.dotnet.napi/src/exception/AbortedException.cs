using System;
using System.Runtime.CompilerServices;

namespace robocode.exception
{
    public class AbortedException : Exception
    {
        private const long serialVersionUID = 1L;

        public AbortedException()
        {
        }

        public AbortedException(string message) : base(message)
        {
        }
    }
}