using System;
using System.Collections;
using System.Diagnostics;
using System.Drawing;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Security.Policy;
using net.sf.robocode.repository;
using robocode;
using robocode.robotinterfaces;

namespace net.sf.robocode.dotnet.utils
{
    class Reflection
    {
        public static PermissionSet GetNamedPermissionSet(string name)
        {
            IEnumerator policyEnumerator = SecurityManager.PolicyHierarchy();

            // Move through the policy levels to the machine policy level.
            while (policyEnumerator.MoveNext())
            {
                PolicyLevel currentLevel = (PolicyLevel)policyEnumerator.Current;

                if (currentLevel.Label == "Machine")
                {
                    return currentLevel.GetNamedPermissionSet(name);
                }
            }
            return null;
        }

        public static StrongName GetStrongName(Assembly assembly)
        {
            if (assembly == null)
                throw new ArgumentNullException("assembly");

            AssemblyName assemblyName = assembly.GetName();
            Debug.Assert(assemblyName != null, "Could not get assembly name");

            // Get the public key blob.
            byte[] publicKey = assemblyName.GetPublicKey();
            if (publicKey == null || publicKey.Length == 0)
                throw new InvalidOperationException("Assembly is not strongly named");

            StrongNamePublicKeyBlob keyBlob = new StrongNamePublicKeyBlob(publicKey);

            // Return the strong name.
            return new StrongName(keyBlob, assemblyName.Name, assemblyName.Version);
        }

        public static RobotType CheckInterfaces(Type robotClass)
        {
            if (robotClass == null || robotClass.IsAbstract)
            {
                // this class is not robot
                return RobotType.Invalid;
            }
            bool isJuniorRobot = false;
            bool isStandardRobot = false;
            bool isInteractiveRobot = false;
            bool isPaintRobot = false;
            bool isAdvancedRobot = false;
            bool isTeamRobot = false;
            bool isDroid = false;

            if (typeof(Droid).IsAssignableFrom(robotClass))
            {
                isDroid = true;
            }

            if (typeof(ITeamRobot).IsAssignableFrom(robotClass))
            {
                isTeamRobot = true;
            }

            if (typeof(IAdvancedRobot).IsAssignableFrom(robotClass))
            {
                isAdvancedRobot = true;
            }

            if (typeof(IInteractiveRobot).IsAssignableFrom(robotClass))
            {
                // in this case we make sure that robot don't waste time
                if (checkMethodOverride(robotClass, typeof(Robot), "getInteractiveEventListener")
                    || checkMethodOverride(robotClass, typeof(Robot), "onKeyPressed", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onKeyReleased", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onKeyTyped", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseClicked", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseEntered", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseExited", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMousePressed", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseReleased", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseMoved", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "onMouseDragged", typeof(MouseEvent))
                    ||
                    checkMethodOverride(robotClass, typeof(Robot), "onMouseWheelMoved", typeof(MouseWheelMovedEvent))
                    )
                {
                    isInteractiveRobot = true;
                }
            }

            if (typeof(IPaintRobot).IsAssignableFrom(robotClass))
            {
                if (checkMethodOverride(robotClass, typeof(Robot), "getPaintEventListener")
                    || checkMethodOverride(robotClass, typeof(Robot), "onPaint", typeof(IGraphics))
                    )
                {
                    isPaintRobot = true;
                }
            }

            if (typeof(Robot).IsAssignableFrom(robotClass) && !isAdvancedRobot)
            {
                isStandardRobot = true;
            }

            if (typeof(IJuniorRobot).IsAssignableFrom(robotClass))
            {
                isJuniorRobot = true;
                if (isAdvancedRobot)
                {
                    throw new AccessViolationException(robotClass.Name +
                                                       ": Junior robot should not implement IAdvancedRobot interface.");
                }
            }

            if (typeof(IBasicRobot).IsAssignableFrom(robotClass))
            {
                if (!(isAdvancedRobot || isJuniorRobot))
                {
                    isStandardRobot = true;
                }
            }
            if (!isAdvancedRobot && !isStandardRobot &&!isJuniorRobot)
            {
                return RobotType.Invalid;
            }

            return new RobotType(isJuniorRobot, isStandardRobot, isInteractiveRobot, isPaintRobot, isAdvancedRobot,
                                 isTeamRobot, isDroid);
        }

        private static bool checkMethodOverride(Type robotClass, Type knownBase, String name, params Type[] parameterTypes)
        {
            if (knownBase.IsAssignableFrom(robotClass))
            {
                MethodInfo method;

                method = robotClass.GetMethod(name, parameterTypes);
                if (method == null)
                {
                    return false;
                }


                if (method.DeclaringType.Equals(knownBase))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
