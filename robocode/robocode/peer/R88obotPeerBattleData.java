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

import robocode.peer.robot.*;
import robocode.util.BoundingRectangle;
import robocode.robotinterfaces.IBasicRobot;
import robocode.repository.RobotFileSpecification;
import robocode.battle.record.RobotRecord;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public class R88obotPeerBattleData extends R80obotPeerDisplay
{
    private RobotStatistics statistics;
    private BoundingRectangle boundingBox;

    public void initBattle(){
        boundingBox = new BoundingRectangle();
        // Create statistics after teamPeer set
        statistics = new RobotStatistics((RobotPeer)this);
    }

    public BoundingRectangle getBoundingBox() {
        return boundingBox;
    }

    protected void cleanupBattle() {
        if (statistics != null) {
            statistics.cleanup();
            statistics = null;
        }
    }

    public void setStatistics(RobotStatistics newStatistics) {
        statistics = newStatistics;
    }

	public RobotStatistics getRobotStatistics() {
		return statistics;
	}
}
