using System;
using System.Globalization;
using System.IO;
using System.Threading;
using net.sf.robocode.dotnet.host.seed;

namespace net.sf.robocode.dotnet.repository.root
{
    public class DllRootHelper
    {
        public DllRootHelper()
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
        }

        public string[] findItems(string dllPath)
        {
            string file = dllPath.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                throw new ArgumentException();
            }

            using (var shell = new AppDomainShell(file))
            {
                return shell.FindRobots();
            }
        }
    }
}
