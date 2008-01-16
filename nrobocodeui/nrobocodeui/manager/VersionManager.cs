using System;
using robocode.manager;

namespace nrobocodeui.manager
{
    public class VersionManager : LoadableManagerBase, IVersionManager
    {
        public void checkUpdateCheck()
        {
            Console.WriteLine("checkUpdateCheck Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
        }

        public string getVersion()
        {
            Console.WriteLine("getVersion Ha hah ah ah ah ah !!!!!");
            //TODO ZAMO
            return "0.0.0.1";
        }
    }
}
