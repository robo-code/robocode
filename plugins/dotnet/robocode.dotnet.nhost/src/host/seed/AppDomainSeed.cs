using System;
using System.Reflection;
using System.Security.Permissions;
using System.Text;
using net.sf.jni4net;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.repository;

namespace net.sf.robocode.dotnet.host.seed
{
    [ReflectionPermission(SecurityAction.Assert, Unrestricted = true)]
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    //[EnvironmentPermission(SecurityAction.Assert, Unrestricted = true)]
    //[SecurityPermission(SecurityAction.Assert, Unrestricted = true)]
    public class AppDomainSeed
    {
        protected static string robotAssemblyFileName;
        protected static string robotAssemblyShadowFileName;
        protected static AppDomain domain;

        public static void Load(string[] args)
        {
            try
            {
                domain = AppDomain.CurrentDomain;
                robotAssemblyFileName = args[0];
                robotAssemblyShadowFileName = args[1];
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                throw;
            }
        }

        public static void Bind()
        {
            try
            {
                var setup = new BridgeSetup(false);
                setup.Verbose = true;
                setup.Debug = true;
                setup.BindNative = false;
                setup.BindStatic = false;
                Bridge.CreateJVM(setup);
                Bridge.LoadAndRegisterAssembly(typeof(AppDomainSeed).Assembly.Location);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                throw;
            }
        }

        public static void FindRobots()
        {
            try
            {
                var sb = new StringBuilder();
                Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
                foreach (Type type in assembly.GetTypes())
                {
                    if (Reflection.CheckInterfaces(type) != RobotType.Invalid)
                    {
                        sb.Append("file://");
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
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                throw;
            }
        }


        public static void GetRobotType()
        {
            try
            {
                var robotFullName = (string) domain.GetData("loadRobot");
                Assembly assembly = Assembly.LoadFrom(robotAssemblyShadowFileName);
                Type robotType = assembly.GetType(robotFullName, false);
                if (robotType != null)
                {
                    domain.SetData("robotLoaded", Reflection.CheckInterfaces(robotType).getCode());
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex);
                throw;
            }
        }
    }
}