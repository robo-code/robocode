﻿// ****************************************************************************
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

        public RobocodeFrame(RobocodeManager manager)
        {
            this.manager = manager;
            frameProxy = new RobocodeFrameProxy(this, this);

            InitializeComponent();
            Size=new Size(971, 717);
            battleView.InitBattleView(manager, this);
            string path = robocode.io.FileUtil.getBattlesDir().getAbsolutePath();
            openFileDialog.InitialDirectory = path;
            saveFileDialog.InitialDirectory = path;

        }

        #endregion

        #region Private members

        private RobocodeFrameProxy frameProxy;
        private FormWindowState lastState = FormWindowState.Normal;
        private RobocodeManager manager;

        #endregion

        #region Public fields

        public BattleViewProxy BattleViewProxy
        {
            get
            {
                return battleView.BattleViewProxy;
            }
        }

        public RobocodeFrameProxy FrameProxy
        {
            get
            {
                return frameProxy;
            }
        }

        #endregion

        #region IRobocodeFrame Members

        public IBattleView getBattleView()
        {
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
            Text = str;
        }

        public string saveBattleDialog(string file)
        {
            return SaveBattleAs(file);
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
            foreach (Button rb in fpRobotButtons.Controls)
            {
                if (rb != null && rb.Tag is RobotDialog)
                {
                    ((RobotDialog)rb.Tag).Close();
                }
            }
            fpRobotButtons.Controls.Clear();
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            Button rb = new Button();
            rb.Tag = rp;
            rb.Text = rp.getName();
            rb.Size = new Size(100, 20);
            fpRobotButtons.Controls.Add(rb);
            rb.Click += robotButton_Click;
        }

        public void validate()
        {
            Invalidate();
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
            try
            {
                OpenBattle();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void saveMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                manager.getBattleManager().saveBattle();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void SaveAsMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                manager.getBattleManager().saveBattleAs();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void ExitMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                Close();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void RobocodeFrame_Load(object sender, EventArgs e)
        {
            try
            {
                ((WindowManager)manager.getWindowManager()).OnDisplayLoaded();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }


        private void newMenuItem_Click(object sender, EventArgs e)
        {
            //TODO
        }

        private void speedSlider_Scroll(object sender, EventArgs e)
        {
            try
            {
                int tps = speedSlider.Value;
                if (tps == speedSlider.Maximum)
                {
                    tps = 10000;
                }
                manager.getProperties().setOptionsBattleDesiredTPS(tps);
                speedSlider.Text = "  " + tps;
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void pauseDebugButton_Click(object sender, EventArgs e)
        {
            try
            {
                BattleManager battleManager = manager.getBattleManager();

                if (battleManager.isPaused())
                {
                    battleManager.resumeBattle();
                }
                else
                {
                    battleManager.pauseBattle();
                }
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void stopButton_Click(object sender, EventArgs e)
        {
            try
            {
                manager.getBattleManager().stop();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void restartButton_Click(object sender, EventArgs e)
        {
            try
            {
                manager.getBattleManager().restart();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void nextTurnButton_Click(object sender, EventArgs e)
        {
            try
            {
                manager.getBattleManager().nextTurn();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void robotButton_Click(object sender, EventArgs e)
        {
            try
            {
                Button rb = sender as Button;
                if (rb != null && rb.Tag is RobotPeer)
                {
                    RobotPeer robotPeer = rb.Tag as RobotPeer;
                    RobotDialog rd = new RobotDialog();
                    rd.setRobotPeer(robotPeer, rb);
                    rd.Show();
                    rb.Tag = rd;
                }
                if (rb != null && rb.Tag is RobotDialog)
                {
                    ((RobotDialog)rb.Tag).Activate();
                }
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        private void RobocodeFrame_FormClosing(object sender, FormClosingEventArgs e)
        {
            try
            {
                battleView.BattleViewProxy.OnClosing();
                frameProxy.OnClosing();
            }
            catch (Exception ex)
            {
                //should not pass exceptions to UI thread
                WindowManager.HandleException(ex);
            }
        }

        #endregion
    }
}
