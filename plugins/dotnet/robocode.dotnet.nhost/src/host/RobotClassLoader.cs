using System;
using System.Collections;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Security.Policy;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.repository;
using robocode;
using robocode.robotinterfaces;
using String=java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    internal class RobotClassLoader
    {
        private IRobotRepositoryItem item;
        private string readableDirectory;
        private string writableDirectory;

        public RobotClassLoader(IRobotRepositoryItem item)
        {
            readableDirectory = Path.GetFullPath(item.getReadableDirectory());
            writableDirectory = Path.GetFullPath(item.getWritableDirectory());
            this.item = item;
        }

        public RobotType LoadRobotMainClass()
        {
            string url = item.getFullUrl().ToString();
            string file = url.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                return null;
            }

            Type type=null;
            return Reflection.CheckInterfaces(type);
        }

        public IBasicRobot CreateRobotInstance()
        {
            return null;//TODO
        }

        public void Cleanup()
        {
            
        }
    }
}
