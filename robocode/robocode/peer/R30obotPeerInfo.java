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

import robocode.repository.RobotFileSpecification;
import robocode.manager.NameManager;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.ITeamRobot;
import robocode.robotinterfaces.IInteractiveRobot;
import robocode.peer.robot.RobotMessageManager;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerSync;
import robocode.Robot;

import java.awt.event.MouseWheelEvent;

/**
 * Set at start never change or very rare, read only, no synchro
 * @author Pavel Savara (original)
 */
public class R30obotPeerInfo extends R20obotPeerComponents{
    public void initInfo(RobotFileSpecification rfs){
        info=new RobotPeerSync();
        info.initInfo(rfs, (RobotPeer)this);
    }
}

