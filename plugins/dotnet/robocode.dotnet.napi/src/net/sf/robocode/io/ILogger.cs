using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace robocode.net.sf.robocode.io
{
    internal interface ILogger
    {
        void logError(string s);
        void logMessage(string s);
    }
}
