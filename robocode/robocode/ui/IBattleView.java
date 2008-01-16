/**
 * ****************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 *     Pavel Savara
 *     - Refactoring
 * *****************************************************************************
 */

package robocode.ui;

import robocode.battle.Battle;
import robocode.battlefield.BattleField;

public interface IBattleView {
    int getWidth();
    int getHeight();

    void setVisible(boolean value);
    void setInitialized(boolean value);
    void setTitle(String title);
    boolean isDisplayTPS();
    boolean isDisplayFPS();
    void update();
    void setDisplayOptions();
    void repaint();
    void setBattle(Battle battle);
    void setBattleField(BattleField bf);
}
