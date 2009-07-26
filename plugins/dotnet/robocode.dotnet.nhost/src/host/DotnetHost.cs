using System;
using System.Drawing;
using System.Reflection;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.control;
using robocode.robotinterfaces;
using String=java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    public class DotnetHost : IHost
    {
        public IHostingRobotProxy createRobotProxy(IHostManager par0, RobotSpecification par1, RobotStatics par2,
                                                   IRobotPeer par3)
        {
            throw new NotImplementedException();
        }

        public String[] getReferencedClasses(IRobotRepositoryItem par0)
        {
            return new String[] {};
        }

        public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, bool resolve, bool message)
        {
            RobotLoader loader = null;

            try
            {
                loader = new RobotLoader(robotRepositoryItem);
                Type robotClass = loader.LoadRobotMainClass();

                if (robotClass == null || robotClass.IsAbstract)
                {
                    // this class is not robot
                    return RobotType.INVALID;
                }
                return checkInterfaces(robotClass, robotRepositoryItem);
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
        }


        private RobotType checkInterfaces(Type robotClass, IRobotRepositoryItem robotRepositoryItem)
        {
            bool isJuniorRobot = false;
            bool isStandardRobot = false;
            bool isInteractiveRobot = false;
            bool isPaintRobot = false;
            bool isAdvancedRobot = false;
            bool isTeamRobot = false;
            bool isDroid = false;

            if (typeof (Droid).IsAssignableFrom(robotClass))
            {
                isDroid = true;
            }

            if (typeof (ITeamRobot).IsAssignableFrom(robotClass))
            {
                isTeamRobot = true;
            }

            if (typeof (IAdvancedRobot).IsAssignableFrom(robotClass))
            {
                isAdvancedRobot = true;
            }

            if (typeof (IInteractiveRobot).IsAssignableFrom(robotClass))
            {
                // in this case we make sure that robot don't waste time
                if (checkMethodOverride(robotClass, typeof (Robot), "getInteractiveEventListener")
                    || checkMethodOverride(robotClass, typeof (Robot), "onKeyPressed", typeof (KeyEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onKeyReleased", typeof (KeyEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onKeyTyped", typeof (KeyEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseClicked", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseEntered", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseExited", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMousePressed", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseReleased", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseMoved", typeof (MouseEvent))
                    || checkMethodOverride(robotClass, typeof (Robot), "onMouseDragged", typeof (MouseEvent))
                    ||
                    checkMethodOverride(robotClass, typeof (Robot), "onMouseWheelMoved", typeof (MouseWheelMovedEvent))
                    )
                {
                    isInteractiveRobot = true;
                }
            }

            if (typeof (IPaintRobot).IsAssignableFrom(robotClass))
            {
                if (checkMethodOverride(robotClass, typeof (Robot), "getPaintEventListener")
                    || checkMethodOverride(robotClass, typeof (Robot), "onPaint", typeof (Graphics))
                    )
                {
                    isPaintRobot = true;
                }
            }

            if (typeof (Robot).IsAssignableFrom(robotClass) && !isAdvancedRobot)
            {
                isStandardRobot = true;
            }

            if (typeof (IJuniorRobot).IsAssignableFrom(robotClass))
            {
                isJuniorRobot = true;
                if (isAdvancedRobot)
                {
                    throw new AccessViolationException(robotRepositoryItem.getFullClassName()
                                                       + ": Junior robot should not implement IAdvancedRobot interface.");
                }
            }

            if (typeof (IBasicRobot).IsAssignableFrom(robotClass))
            {
                if (!(isAdvancedRobot || isJuniorRobot))
                {
                    isStandardRobot = true;
                }
            }
            return new RobotType(isJuniorRobot, isStandardRobot, isInteractiveRobot, isPaintRobot, isAdvancedRobot,
                                 isTeamRobot, isDroid);
        }

        private static bool checkMethodOverride(Type robotClass, Type knownBase, String name,
                                                params Type[] parameterTypes)
        {
            if (knownBase.IsAssignableFrom(robotClass))
            {
                MethodInfo getInteractiveEventListener;

                getInteractiveEventListener = robotClass.GetMethod(name, parameterTypes);
                if (getInteractiveEventListener == null)
                {
                    return false;
                }


                if (getInteractiveEventListener.DeclaringType.Equals(knownBase))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
