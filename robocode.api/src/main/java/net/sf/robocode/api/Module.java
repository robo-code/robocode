/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.api;


import net.sf.robocode.core.IModule;
import net.sf.robocode.serialization.RbSerializer;
import robocode.*;

import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class Module implements IModule {
	public void afterLoaded(List<IModule> allModules) {
		RbSerializer.register(RobotStatus.class, RbSerializer.RobotStatus_TYPE);
		RbSerializer.register(BattleResults.class, RbSerializer.BattleResults_TYPE);
		RbSerializer.register(Bullet.class, RbSerializer.Bullet_TYPE);

		// events
		RbSerializer.register(RoundEndedEvent.class, RbSerializer.RoundEndedEvent_TYPE);
		RbSerializer.register(BattleEndedEvent.class, RbSerializer.BattleEndedEvent_TYPE);
		RbSerializer.register(BulletHitBulletEvent.class, RbSerializer.BulletHitBulletEvent_TYPE);
		RbSerializer.register(BulletHitEvent.class, RbSerializer.BulletHitEvent_TYPE);
		RbSerializer.register(BulletMissedEvent.class, RbSerializer.BulletMissedEvent_TYPE);
		RbSerializer.register(DeathEvent.class, RbSerializer.DeathEvent_TYPE);

		RbSerializer.register(WinEvent.class, RbSerializer.WinEvent_TYPE);
		RbSerializer.register(HitWallEvent.class, RbSerializer.HitWallEvent_TYPE);
		RbSerializer.register(RobotDeathEvent.class, RbSerializer.RobotDeathEvent_TYPE);
		RbSerializer.register(SkippedTurnEvent.class, RbSerializer.SkippedTurnEvent_TYPE);
		RbSerializer.register(ScannedRobotEvent.class, RbSerializer.ScannedRobotEvent_TYPE);
		RbSerializer.register(HitByBulletEvent.class, RbSerializer.HitByBulletEvent_TYPE);
		RbSerializer.register(HitRobotEvent.class, RbSerializer.HitRobotEvent_TYPE);
		RbSerializer.register(KeyPressedEvent.class, RbSerializer.KeyPressedEvent_TYPE);
		RbSerializer.register(KeyReleasedEvent.class, RbSerializer.KeyReleasedEvent_TYPE);
		RbSerializer.register(KeyTypedEvent.class, RbSerializer.KeyTypedEvent_TYPE);
		RbSerializer.register(MouseClickedEvent.class, RbSerializer.MouseClickedEvent_TYPE);
		RbSerializer.register(MouseDraggedEvent.class, RbSerializer.MouseDraggedEvent_TYPE);
		RbSerializer.register(MouseEnteredEvent.class, RbSerializer.MouseEnteredEvent_TYPE);
		RbSerializer.register(MouseExitedEvent.class, RbSerializer.MouseExitedEvent_TYPE);
		RbSerializer.register(MouseMovedEvent.class, RbSerializer.MouseMovedEvent_TYPE);
		RbSerializer.register(MousePressedEvent.class, RbSerializer.MousePressedEvent_TYPE);
		RbSerializer.register(MouseReleasedEvent.class, RbSerializer.MouseReleasedEvent_TYPE);
		RbSerializer.register(MouseWheelMovedEvent.class, RbSerializer.MouseWheelMovedEvent_TYPE);
	}
}
