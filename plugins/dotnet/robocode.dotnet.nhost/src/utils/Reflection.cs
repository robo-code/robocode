﻿using System;
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

            if (typeof(IDroid).IsAssignableFrom(robotClass))
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
                if (checkMethodOverride(robotClass, typeof(Robot), "GetInteractiveEventListener")
                    || checkMethodOverride(robotClass, typeof(Robot), "OnKeyPressed", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnKeyReleased", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnKeyTyped", typeof(KeyEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseClicked", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseEntered", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseExited", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMousePressed", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseReleased", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseMoved", typeof(MouseEvent))
                    || checkMethodOverride(robotClass, typeof(Robot), "OnMouseDragged", typeof(MouseEvent))
                    ||
                    checkMethodOverride(robotClass, typeof(Robot), "OnMouseWheelMoved", typeof(MouseWheelMovedEvent))
                    )
                {
                    isInteractiveRobot = true;
                }
            }

            if (typeof(IPaintRobot).IsAssignableFrom(robotClass))
            {
                if (checkMethodOverride(robotClass, typeof(Robot), "GetPaintEventListener")
                    || checkMethodOverride(robotClass, typeof(Robot), "OnPaint", typeof(IGraphics))
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
