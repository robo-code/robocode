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
using System.Drawing;
using System.Windows.Forms;
using nrobocodeui.battleview;
using nrobocodeui.manager;
using robocode.battle;
using robocode.manager;
using robocode.peer;
using robocode.ui;

namespace nrobocodeui.dialog
{
    public partial class RobocodeFrame : Form, IRobocodeFrame
    {
        #region Constructors

        public RobocodeFrame(robocode.manager.RobocodeManager manager)
        {
            this.manager = manager;
            InitializeComponent();
            battleView.InitFrame(this);
            string path = robocode.io.FileUtil.getBattlesDir().getAbsolutePath();
            openFileDialog.InitialDirectory = path;
            saveFileDialog.InitialDirectory = path;

            battleViewProxy = new BattleViewProxy(battleView);
        }

        #endregion

        #region Private members

        private int childFormNumber = 0;
        private FormWindowState lastState = FormWindowState.Normal;
        private robocode.manager.RobocodeManager manager;
        private BattleViewProxy battleViewProxy;

        #endregion

        #region Public fields

        public BattleViewProxy BattleViewProxy
        {
            get
            {
                return battleViewProxy;
            }
        }

        #endregion

        #region IRobocodeFrame Members

        public IBattleView getBattleView()
        {
            if (battleView == null)
            {
                //TODO
                battleView = new BattleView();
            }
            return battleView;
        }

        public void setEnableBattleSaveAsMenuItem(bool b)
        {
            saveAsMenuItem.Enabled = b;
        }

        public void setEnableBattleSaveMenuItem(bool b)
        {
            saveMenuItem.Enabled = b;
        }

        public void setTitle(string str)
        {
            this.Text = str;
        }

        public string saveBattleDialog(string file)
        {
            return SaveBattleAs(file);
        }

        public void messageError(string str)
        {
            MessageBox.Show(str, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        public void messageWarning(string str)
        {
            MessageBox.Show(str, "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
        }

        public void setIconified(bool b)
        {
            if (b)
            {
                lastState = WindowState;
                WindowState = FormWindowState.Minimized;
            }
            else
            {
                WindowState = lastState;
            }
        }

        public void setStatus(string str)
        {
            statusStrip.Text = str;
        }

        public void setEnableStopButton(bool b)
        {
            stopButton.Enabled = b;
        }

        public void setEnableRestartButton(bool b)
        {
            restartButton.Enabled = b;
        }

        public void setEnableReplayButton(bool b)
        {
            //TODO
        }

        public void clearRobotButtons()
        {
            fpRobotButtons.Controls.Clear();
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            Button rb = new Button();
            rb.Tag = rp;
            rb.Text = rp.getName();
            rb.Size = new Size(100, 20);
            fpRobotButtons.Controls.Add(rb);
            //TODO event handler
        }

        public void validate()
        {
            //TODO
        }

        public bool isIconified()
        {
            return WindowState == FormWindowState.Minimized;
        }


        public void dispose()
        {
            //TODO
        }

        #endregion

        #region Private Implementation

        private void ShowNewBattleDialog(BattleProperties battleProperties)
        {
            manager.getBattleManager().pauseBattle();

            NewBattleDialog newBattleDialog = new NewBattleDialog(manager, battleProperties);

            newBattleDialog.ShowDialog(this);
        }

        private void OpenBattle()
        {
            BattleManager battleManager = manager.getBattleManager();
            battleManager.pauseBattle();

            //openFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            openFileDialog.Filter = "Battle files (*.battle)|*.battle|All Files (*.*)|*.*";
            if (openFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                battleManager.setBattleFilename(openFileDialog.FileName);
                battleManager.loadBattleProperties();
                ShowNewBattleDialog(battleManager.getBattleProperties());
            }
            battleManager.resumeBattle();
        }

        private string SaveBattleAs(string fileName)
        {
            saveFileDialog.FileName = fileName;
            //saveFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            saveFileDialog.Filter = "Battle files (*.battle)|*.battle|All Files (*.*)|*.*";
            if (saveFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                return saveFileDialog.FileName;
            }
            return null;
        }

        #endregion

        #region Event Handlers

        private void OpenFileMenuItem_Click(object sender, EventArgs e)
        {
            OpenBattle();
        }

        private void saveMenuItem_Click(object sender, EventArgs e)
        {
            manager.getBattleManager().saveBattle();
        }

        private void SaveAsMenuItem_Click(object sender, EventArgs e)
        {
            manager.getBattleManager().saveBattleAs();
        }

        private void ExitMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void RobocodeFrame_Load(object sender, EventArgs e)
        {
            ((WindowManager)manager.getWindowManager()).OnDisplayLoaded();
        }


        private void newMenuItem_Click(object sender, EventArgs e)
        {
            //TODO
        }

        private void speedSlider_Scroll(object sender, EventArgs e)
        {
            int tps = speedSlider.Value;
            if (tps == speedSlider.Maximum)
            {
                tps = 10000;
            }
            manager.getProperties().setOptionsBattleDesiredTPS(tps);
            speedSlider.Text= "  " + tps;
        }

        #endregion
    }
}
