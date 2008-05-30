/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;

import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.ITeamRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.peer.robot.*;
import robocode.peer.proxies.TeamRobotProxy;
import robocode.peer.proxies.AdvancedRobotProxy;
import robocode.peer.proxies.StandardRobotProxy;
import robocode.peer.proxies.JuniorRobotProxy;
import robocode.*;
import static robocode.gfx.ColorUtil.toColor;
import robocode.manager.NameManager;
import static robocode.io.Logger.logMessage;
import robocode.exception.DeathException;
import robocode.exception.WinException;
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.robotpaint.Graphics2DProxy;
import robocode.battle.Battle;
import robocode.battle.record.RobotRecord;
import robocode.util.BoundingRectangle;
import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalNearAbsoluteAngle;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.awt.Event;
import java.awt.List;
import java.security.AccessControlException;
import static java.lang.Math.atan2;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.max;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.*;

/**
 * @author Pavel Savara (original)
 */
public class RobotPeerBase {
}
