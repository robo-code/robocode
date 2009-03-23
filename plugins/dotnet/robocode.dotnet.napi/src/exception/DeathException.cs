using System;
using System.Runtime.CompilerServices;

namespace robocode.exception
{
    public class DeathException : Exception
    {
        private const long serialVersionUID = 1L;

        public DeathException()
        {
        }

        public DeathException(string message) : base(message)
        {
        }
    }
}