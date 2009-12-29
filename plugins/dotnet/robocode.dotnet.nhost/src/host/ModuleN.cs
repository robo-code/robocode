﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using net.sf.jni4net;
using net.sf.robocode.core;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.manager;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode;
using robocode.control;

namespace net.sf.robocode.dotnet.nhost
{
    public class ModuleN
    {
        public static void InitN()
        {
            HiddenAccessN.init();
            LoggerN.IsSafeThread = true;
            LoggerN.setLogListener(new Logger(true));
            HiddenAccessN.randomHelper = new RandomFactory(true);

            var versionManager =
                Bridge.Cast<IVersionManagerBase>(ContainerBase.getComponent(IVersionManagerBase_._class));
            int currentVersion = versionManager.getVersionAsInt();
            RbSerializer.Init(currentVersion);
            RbSerializerN.Init(currentVersion);

            RbSerializerN.register(typeof (RobotStatus), RbSerializer.RobotStatus_TYPE);
            RbSerializerN.register(typeof (BattleResults), RbSerializer.BattleResults_TYPE);
            RbSerializerN.register(typeof (Bullet), RbSerializer.Bullet_TYPE);
            RbSerializerN.register(typeof (RobotStatics), RbSerializer.RobotStatics_TYPE);

            // events
            RbSerializerN.register(typeof (BattleEndedEvent), RbSerializer.BattleEndedEvent_TYPE);
            RbSerializerN.register(typeof (BulletHitBulletEvent), RbSerializer.BulletHitBulletEvent_TYPE);
            RbSerializerN.register(typeof (BulletHitEvent), RbSerializer.BulletHitEvent_TYPE);
            RbSerializerN.register(typeof (BulletMissedEvent), RbSerializer.BulletMissedEvent_TYPE);
            RbSerializerN.register(typeof (DeathEvent), RbSerializer.DeathEvent_TYPE);
            RbSerializerN.register(typeof (WinEvent), RbSerializer.WinEvent_TYPE);
            RbSerializerN.register(typeof (HitWallEvent), RbSerializer.HitWallEvent_TYPE);
            RbSerializerN.register(typeof (RobotDeathEvent), RbSerializer.RobotDeathEvent_TYPE);
            RbSerializerN.register(typeof (SkippedTurnEvent), RbSerializer.SkippedTurnEvent_TYPE);
            RbSerializerN.register(typeof (ScannedRobotEvent), RbSerializer.ScannedRobotEvent_TYPE);
            RbSerializerN.register(typeof (HitByBulletEvent), RbSerializer.HitByBulletEvent_TYPE);
            RbSerializerN.register(typeof (HitRobotEvent), RbSerializer.HitRobotEvent_TYPE);
            RbSerializerN.register(typeof (KeyPressedEvent), RbSerializer.KeyPressedEvent_TYPE);
            RbSerializerN.register(typeof (KeyReleasedEvent), RbSerializer.KeyReleasedEvent_TYPE);
            RbSerializerN.register(typeof (KeyTypedEvent), RbSerializer.KeyTypedEvent_TYPE);
            RbSerializerN.register(typeof (MouseClickedEvent), RbSerializer.MouseClickedEvent_TYPE);
            RbSerializerN.register(typeof (MouseDraggedEvent), RbSerializer.MouseDraggedEvent_TYPE);
            RbSerializerN.register(typeof (MouseEnteredEvent), RbSerializer.MouseEnteredEvent_TYPE);
            RbSerializerN.register(typeof (MouseExitedEvent), RbSerializer.MouseExitedEvent_TYPE);
            RbSerializerN.register(typeof (MouseMovedEvent), RbSerializer.MouseMovedEvent_TYPE);
            RbSerializerN.register(typeof (MousePressedEvent), RbSerializer.MousePressedEvent_TYPE);
            RbSerializerN.register(typeof (MouseReleasedEvent), RbSerializer.MouseReleasedEvent_TYPE);
            RbSerializerN.register(typeof (MouseWheelMovedEvent), RbSerializer.MouseWheelMovedEvent_TYPE);

            RbSerializerN.register(typeof (ExecCommands), RbSerializer.ExecCommands_TYPE);
            RbSerializerN.register(typeof (BulletCommand), RbSerializer.BulletCommand_TYPE);
            RbSerializerN.register(typeof (TeamMessage), RbSerializer.TeamMessage_TYPE);
            RbSerializerN.register(typeof (DebugProperty), RbSerializer.DebugProperty_TYPE);
            RbSerializerN.register(typeof (ExecResults), RbSerializer.ExecResults_TYPE);
            RbSerializerN.register(typeof (BulletStatus), RbSerializer.BulletStatus_TYPE);
        }
    }
}