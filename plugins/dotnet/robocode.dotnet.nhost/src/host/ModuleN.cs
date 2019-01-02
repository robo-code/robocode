/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Globalization;
using System.Windows.Forms;
using net.sf.jni4net;
using net.sf.robocode.core;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.manager;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using Robocode;
using robocode.control;

namespace net.sf.robocode.dotnet.nhost
{
    public class ModuleN
    {
        public static void InitN()
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = CultureInfo.InvariantCulture;
            System.Threading.Thread.CurrentThread.CurrentUICulture = CultureInfo.InvariantCulture;
            HiddenAccessN.init();
            LoggerN.IsSafeThread = true;
            LoggerN.setLogListener(new Logger(true));
            HiddenAccessN.randomHelper = new RandomFactory(true);

            var versionManager =
                Bridge.Cast<IVersionManagerBase>(ContainerBase.getComponent(IVersionManagerBase_._class));
            int currentVersion = versionManager.getVersionAsInt();
            RbSerializer.Init(currentVersion);
            RbSerializerN.Init(currentVersion);

            RbSerializerN.register(typeof (RobotStatus), RbSerializerN.RobotStatus_TYPE);
            RbSerializerN.register(typeof (BattleResults), RbSerializerN.BattleResults_TYPE);
            RbSerializerN.register(typeof (Bullet), RbSerializerN.Bullet_TYPE);
            RbSerializerN.register(typeof (RobotStatics), RbSerializerN.RobotStatics_TYPE);

            // events
            RbSerializerN.register(typeof (RoundEndedEvent), RbSerializerN.RoundEndedEvent_TYPE);
            RbSerializerN.register(typeof (BattleEndedEvent), RbSerializerN.BattleEndedEvent_TYPE);
            RbSerializerN.register(typeof (BulletHitBulletEvent), RbSerializerN.BulletHitBulletEvent_TYPE);
            RbSerializerN.register(typeof (BulletHitEvent), RbSerializerN.BulletHitEvent_TYPE);
            RbSerializerN.register(typeof (BulletMissedEvent), RbSerializerN.BulletMissedEvent_TYPE);
            RbSerializerN.register(typeof (DeathEvent), RbSerializerN.DeathEvent_TYPE);
            RbSerializerN.register(typeof (WinEvent), RbSerializerN.WinEvent_TYPE);
            RbSerializerN.register(typeof (HitWallEvent), RbSerializerN.HitWallEvent_TYPE);
            RbSerializerN.register(typeof (RobotDeathEvent), RbSerializerN.RobotDeathEvent_TYPE);
            RbSerializerN.register(typeof (SkippedTurnEvent), RbSerializerN.SkippedTurnEvent_TYPE);
            RbSerializerN.register(typeof (ScannedRobotEvent), RbSerializerN.ScannedRobotEvent_TYPE);
            RbSerializerN.register(typeof (HitByBulletEvent), RbSerializerN.HitByBulletEvent_TYPE);
            RbSerializerN.register(typeof (HitRobotEvent), RbSerializerN.HitRobotEvent_TYPE);
            RbSerializerN.register(typeof (KeyPressedEvent), RbSerializerN.KeyPressedEvent_TYPE);
            RbSerializerN.register(typeof (KeyReleasedEvent), RbSerializerN.KeyReleasedEvent_TYPE);
            RbSerializerN.register(typeof (KeyTypedEvent), RbSerializerN.KeyTypedEvent_TYPE);
            RbSerializerN.register(typeof (MouseClickedEvent), RbSerializerN.MouseClickedEvent_TYPE);
            RbSerializerN.register(typeof (MouseDraggedEvent), RbSerializerN.MouseDraggedEvent_TYPE);
            RbSerializerN.register(typeof (MouseEnteredEvent), RbSerializerN.MouseEnteredEvent_TYPE);
            RbSerializerN.register(typeof (MouseExitedEvent), RbSerializerN.MouseExitedEvent_TYPE);
            RbSerializerN.register(typeof (MouseMovedEvent), RbSerializerN.MouseMovedEvent_TYPE);
            RbSerializerN.register(typeof (MousePressedEvent), RbSerializerN.MousePressedEvent_TYPE);
            RbSerializerN.register(typeof (MouseReleasedEvent), RbSerializerN.MouseReleasedEvent_TYPE);
            RbSerializerN.register(typeof (MouseWheelMovedEvent), RbSerializerN.MouseWheelMovedEvent_TYPE);

            RbSerializerN.register(typeof (ExecCommands), RbSerializerN.ExecCommands_TYPE);
            RbSerializerN.register(typeof (BulletCommand), RbSerializerN.BulletCommand_TYPE);
            RbSerializerN.register(typeof (TeamMessage), RbSerializerN.TeamMessage_TYPE);
            RbSerializerN.register(typeof (DebugProperty), RbSerializerN.DebugProperty_TYPE);
            RbSerializerN.register(typeof (ExecResults), RbSerializerN.ExecResults_TYPE);
            RbSerializerN.register(typeof (BulletStatus), RbSerializerN.BulletStatus_TYPE);
        }
    }
}