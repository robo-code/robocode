// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using System.Threading;
using System.Windows.Forms;
using robocode.battle;
using robocode.security;

namespace nrobocodeui.dialog
{
    public partial class NewBattleDialog : Form
    {
        public NewBattleDialog(robocode.manager.RobocodeManager manager, BattleProperties battleProperties)
        {
            this.manager = manager;
            this.battleProperties = battleProperties;
            InitializeComponent();
        }

        private BattleProperties battleProperties;
        private robocode.manager.RobocodeManager manager;

        private void btnStart_Click(object sender, EventArgs e)
        {
            /* TODO
		    String selectedRobotsProperty = robotSelectionPanel.getSelectedRobotsAsString();
		    battleProperties.setSelectedRobots(selectedRobotsProperty);
		    battleProperties.setBattlefieldWidth(getBattleFieldTab().getBattleFieldWidth());
		    battleProperties.setBattlefieldHeight(getBattleFieldTab().getBattleFieldHeight());
		    battleProperties.setNumRounds(getRobotSelectionPanel().getNumRounds());
		    battleProperties.setGunCoolingRate(getRulesTab().getGunCoolingRate());
		    battleProperties.setInactivityTime(getRulesTab().getInactivityTime());
             */
		    new Thread(run).Start();
            Close();
        }

        public void run()
        {
            /*RobocodeSecurityManager rsm = java.lang.System.getSecurityManager() as RobocodeSecurityManager;
            if (rsm != null)
            {
                rsm.addSafeContext();
            }*/
            manager.getBattleManager().startNewBattle(battleProperties, false, false);
        }
    
    }
}
