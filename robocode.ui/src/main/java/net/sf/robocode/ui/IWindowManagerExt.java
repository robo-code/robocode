/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.BattleResultsTableModel;
import robocode.control.events.BattleCompletedEvent;

import javax.swing.*;


/**
 * @author Pavel Savara (original)
 */
public interface IWindowManagerExt extends IWindowManager {
	void showAboutBox();

	String showBattleOpenDialog(String defExt, String name);

	String saveBattleDialog(String path, String defExt, String name);

	void showReadMe();
	
	void showVersionsTxt();

	void showHelpApi();

	void showFaq();

	void showOnlineHelp();

	void showJavaDocumentation();

	void showRobocodeHome();

	void showRoboWiki();

	void showGoogleGroupRobocode();

	void showRoboRumble();

	void showOptionsPreferences();

	void showResultsDialog(BattleCompletedEvent event);

	void showRankingDialog(boolean visible);

	void showRobocodeEditor();

	void showRobotPackager();

	void showRobotExtractor(JFrame owner);

	void showNewBattleDialog(BattleProperties battleProperties);

	boolean closeRobocodeEditor();

	void showCreateTeamDialog();

	void showImportRobotDialog();

	void showSaveResultsDialog(BattleResultsTableModel tableModel);

	int getFPS();
}
