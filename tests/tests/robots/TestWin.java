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
package robots;

import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import org.junit.Assert;
import helpers.RobotTestBed;

/**
 * @author Pavel Savara (original)
 */
public class TestWin extends RobotTestBed {
    public String getRobotNames(){
        return "sample.Target,testing.BattleWin";        
    }
}
