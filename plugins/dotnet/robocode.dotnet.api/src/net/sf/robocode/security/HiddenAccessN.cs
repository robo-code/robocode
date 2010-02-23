#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.Reflection;
using System.Security.Permissions;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using Robocode;
using Robocode.RobotInterfaces;

namespace net.sf.robocode.security
{
#pragma warning disable 1591
    /// <exclude/>
    [RobocodeInternalPermission(SecurityAction.LinkDemand)]
    public class HiddenAccessN
    {
        private static IHiddenEventHelper eventHelper;
        private static IHiddenBulletHelper bulletHelper;
        private static IHiddenStatusHelper statusHelper;
        private static IHiddenRulesHelper rulesHelper;
        private static bool initialized;
        public static IHiddenRandomHelper randomHelper;

        public static void init()
        {
            if (initialized)
            {
                return;
            }
            MethodInfo method;

            try
            {
                method = typeof (Event).GetMethod("createHiddenHelper", BindingFlags.Static | BindingFlags.NonPublic);
                eventHelper = (IHiddenEventHelper) method.Invoke(null, null);

                method = typeof (Bullet).GetMethod("createHiddenHelper", BindingFlags.Static | BindingFlags.NonPublic);
                bulletHelper = (IHiddenBulletHelper) method.Invoke(null, null);

                method = typeof (RobotStatus).GetMethod("createHiddenSerializer",
                                                        BindingFlags.Static | BindingFlags.NonPublic);
                statusHelper = (IHiddenStatusHelper) method.Invoke(null, null);

                method = typeof (BattleRules).GetMethod("createHiddenHelper",
                                                        BindingFlags.Static | BindingFlags.NonPublic);
                rulesHelper = (IHiddenRulesHelper) method.Invoke(null, null);

                initialized = true;
            }
            catch (Exception e)
            {
                LoggerN.logError(e);
                Environment.Exit(-1);
            }
        }

        public static bool isCriticalEvent(Event e)
        {
            return eventHelper.isCriticalEvent(e);
        }

        public static void setEventTime(Event e, long newTime)
        {
            eventHelper.setTime(e, newTime);
        }

        public static void setEventPriority(Event e, int newPriority)
        {
            eventHelper.setPriority(e, newPriority);
        }

        public static void dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            eventHelper.dispatch(evnt, robot, statics, graphics);
        }

        public static void setDefaultPriority(Event e)
        {
            eventHelper.setDefaultPriority(e);
        }

        public static byte getSerializationType(Event e)
        {
            return eventHelper.getSerializationType(e);
        }

        public static void updateBullets(Event e, Dictionary<int, Bullet> bullets)
        {
            eventHelper.updateBullets(e, bullets);
        }

        public static void update(Bullet bullet, double x, double y, string victimName, bool isActive)
        {
            bulletHelper.update(bullet, x, y, victimName, isActive);
        }

        public static RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading,
                                               double radarHeading, double velocity, double bodyTurnRemaining,
                                               double radarTurnRemaining, double gunTurnRemaining,
                                               double distanceRemaining, double gunHeat, int others, int roundNum,
                                               int numRounds, long time)
        {
            return statusHelper.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
                                             bodyTurnRemaining, radarTurnRemaining, gunTurnRemaining, distanceRemaining,
                                             gunHeat, others, roundNum,
                                             numRounds, time);
        }

        public static BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds,
                                              double gunCoolingRate, long inactivityTime)
        {
            return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate,
                                           inactivityTime);
        }

        public static string GetRobotName()
        {
            string name = (string)AppDomain.CurrentDomain.GetData("robotName");
            return name ?? "";
        }
    }
#pragma warning restore 1591
}

//happy