using System;
using System.Diagnostics;
using System.Reflection;
using System.Text;
using net.sf.jni4net;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.repository;
using robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    public static class AppDomainSeed
    {
        private static string robotAssemblyFileName;
        private static string robotAssemblyShadowFileName;
        private static AppDomain domain;

        public static void Load(string[] args)
        {
            try
            {
                domain = AppDomain.CurrentDomain;
                robotAssemblyFileName = args[0];
                robotAssemblyShadowFileName = args[1];

                Bridge.Verbose = true;
                Bridge.CreateJVM(@"-Djava.class.path=..\..\..\robocode.api\target\test-classes;..\..\..\robocode.api\target\classes;..\..\..\robocode.battle\target\test-classes;..\..\..\robocode.battle\target\classes;..\..\..\robocode.core\target\test-classes;..\..\..\robocode.core\target\classes;C:\Users\Zamboch\.m2\repository\org\picocontainer\picocontainer\2.6\picocontainer-2.6.jar;C:\Users\Zamboch\.m2\repository\junit\junit\4.5\junit-4.5.jar;..\..\..\robocode.host\target\test-classes;..\..\..\robocode.host\target\classes;..\..\..\robocode.repository\target\test-classes;..\..\..\robocode.repository\target\classes;C:\Users\Zamboch\.m2\repository\net\sf\robocode\codesize\1.1\codesize-1.1.jar;..\..\..\robocode.roborumble\target\test-classes;..\..\..\robocode.roborumble\target\classes;..\..\..\robocode.samples\target\test-classes;..\..\..\robocode.samples\target\classes;..\..\..\robocode.sound\target\test-classes;..\..\..\robocode.sound\target\classes;..\..\..\robocode.tests\target\test-classes;..\..\..\robocode.tests\target\classes;..\..\..\robocode.ui\target\test-classes;..\..\..\robocode.ui\target\classes;..\..\..\robocode.ui.editor\target\test-classes;..\..\..\robocode.ui.editor\target\classes;..\robocode.dotnet.api\target\test-classes;..\robocode.dotnet.api\target\classes;..\robocode.dotnet.host\target\test-classes;..\robocode.dotnet.host\target\classes;C:\Users\Zamboch\.m2\repository\net\sf\jni4net\jni4net.j\0.2.0.0\jni4net.j-0.2.0.0.jar;..\robocode.dotnet.nhost\target\test-classes;..\robocode.dotnet.nhost\target\classes;..\robocode.dotnet.samples\target\test-classes;..\robocode.dotnet.samples\target\classes;..\robocode.dotnet.tests\target\test-classes;..\robocode.dotnet.tests\target\classes");
                Bridge.BindNative = false;
                Bridge.LoadAndRegisterAssembly(typeof (AppDomainSeed).Assembly.Location);
            }
            catch(Exception ex)
            {
                Debug.WriteLine(ex);
            }

        }

        public static void FindRobots()
        {
            var sb = new StringBuilder();
            Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
            foreach (Type type in assembly.GetTypes())
            {
                if (Reflection.CheckInterfaces(type)!=RobotType.Invalid)
                {
                    sb.Append(robotAssemblyFileName);
                    sb.Append("!/");
                    sb.Append(type.FullName);
                    sb.Append(";");
                }
            }
            if (sb.Length > 0)
            {
                sb.Length--;
            }
            domain.SetData("robotsFound", sb.ToString());
        }


        public static void GetRobotType()
        {
            var robotFullName = (string)domain.GetData("loadRobot");
            Assembly assembly = Assembly.LoadFrom(robotAssemblyFileName);
            Type robotType = assembly.GetType(robotFullName, false);
            if (robotType != null)
            {
                domain.SetData("robotLoaded", Reflection.CheckInterfaces(robotType).getCode());
            }
        }

        public static void StartRound(ExecCommands commands, RobotStatus status)
        {
        }

        public static void ForceStopThread()
        {
        }

        public static void WaitForStopThread()
        {
        }

        public static void Cleanup()
        {
        }
    }
}

/*
 
            RobotClassLoader loader = null;

            try
            {
                loader = new RobotClassLoader(robotRepositoryItem);
                Type robotClass = loader.LoadRobotMainClass();

                return Reflection.CheckInterfaces(robotClass);
            }
            catch (Exception ex)
            {
                if (message)
                {
                    Logger.logError(robotRepositoryItem.getFullClassName() + ": Got an error with this class: " + ex);
                }
                return RobotType.INVALID;
            }
            finally
            {
                if (loader != null)
                {
                    loader.Cleanup();
                }
            }

 * IHostingRobotProxy robotProxy;
            IRobotRepositoryItem specification = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(robotSpecification);

            if (specification.isTeamRobot())
            {
                robotProxy = new TeamRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isAdvancedRobot())
            {
                robotProxy = new AdvancedRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isStandardRobot())
            {
                robotProxy = new StandardRobotProxy(specification, hostManager, peer, statics);
            }
            else if (specification.isJuniorRobot())
            {
                robotProxy = new JuniorRobotProxy(specification, hostManager, peer, statics);
            }
            else
            {
                throw new AccessViolationException("Unknown robot type");
            }
            return robotProxy;

 
  
 */
