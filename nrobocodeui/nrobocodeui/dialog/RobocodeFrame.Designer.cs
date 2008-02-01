namespace nrobocodeui.dialog
{
    partial class RobocodeFrame
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(RobocodeFrame));
            this.menuStrip = new System.Windows.Forms.MenuStrip();
            this.battleMenu = new System.Windows.Forms.ToolStripMenuItem();
            this.newMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
            this.saveMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveAsMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator5 = new System.Windows.Forms.ToolStripSeparator();
            this.exitMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.robotMenu = new System.Windows.Forms.ToolStripMenuItem();
            this.editorMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.importRobotMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.packageRobotMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.teamMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.createTeamMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.optionsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.preferencesMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.defaultWindowSizeMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.rankingPanelMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.recalculateCPUConstantMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.cleanRobotCacheMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpMenu = new System.Windows.Forms.ToolStripMenuItem();
            this.onlineHelpMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.robocodeAPIMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.netDocumentationMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.robocodeFAQMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator6 = new System.Windows.Forms.ToolStripSeparator();
            this.robocodeHomeMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.roboWikiMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.yahooGroupMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.robocodeRepositoryMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator4 = new System.Windows.Forms.ToolStripSeparator();
            this.checkForNewVersionMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.versionInfoMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator7 = new System.Windows.Forms.ToolStripSeparator();
            this.aboutMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.statusStrip = new System.Windows.Forms.StatusStrip();
            this.toolStripStatusLabel = new System.Windows.Forms.ToolStripStatusLabel();
            this.BottomToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.TopToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.RightToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.LeftToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.ContentPanel = new System.Windows.Forms.ToolStripContentPanel();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.pauseDebugButton = new System.Windows.Forms.ToolStripButton();
            this.nextTurnButton = new System.Windows.Forms.ToolStripButton();
            this.stopButton = new System.Windows.Forms.ToolStripButton();
            this.restartButton = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator8 = new System.Windows.Forms.ToolStripSeparator();
            this.speedSlider = new nrobocodeui.dialog.SliderToolStrip(this.components);
            this.toolStripContainer1 = new System.Windows.Forms.ToolStripContainer();
            this.splitContainer = new System.Windows.Forms.SplitContainer();
            this.battleView = new nrobocodeui.battleview.BattleView();
            this.fpRobotButtons = new System.Windows.Forms.FlowLayoutPanel();
            this.saveFileDialog = new System.Windows.Forms.SaveFileDialog();
            this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
            this.menuStrip.SuspendLayout();
            this.statusStrip.SuspendLayout();
            this.toolStrip1.SuspendLayout();
            this.toolStripContainer1.BottomToolStripPanel.SuspendLayout();
            this.toolStripContainer1.ContentPanel.SuspendLayout();
            this.toolStripContainer1.SuspendLayout();
            this.splitContainer.Panel1.SuspendLayout();
            this.splitContainer.Panel2.SuspendLayout();
            this.splitContainer.SuspendLayout();
            this.SuspendLayout();
            // 
            // menuStrip
            // 
            this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.battleMenu,
            this.robotMenu,
            this.optionsToolStripMenuItem,
            this.helpMenu});
            this.menuStrip.Location = new System.Drawing.Point(0, 0);
            this.menuStrip.Name = "menuStrip";
            this.menuStrip.Size = new System.Drawing.Size(891, 24);
            this.menuStrip.TabIndex = 0;
            this.menuStrip.Text = "MenuStrip";
            // 
            // battleMenu
            // 
            this.battleMenu.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.newMenuItem,
            this.openMenuItem,
            this.toolStripSeparator3,
            this.saveMenuItem,
            this.saveAsMenuItem,
            this.toolStripSeparator5,
            this.exitMenuItem});
            this.battleMenu.ImageTransparentColor = System.Drawing.SystemColors.ActiveBorder;
            this.battleMenu.Name = "battleMenu";
            this.battleMenu.Size = new System.Drawing.Size(46, 20);
            this.battleMenu.Text = "&Battle";
            // 
            // newMenuItem
            // 
            this.newMenuItem.ImageTransparentColor = System.Drawing.Color.Black;
            this.newMenuItem.Name = "newMenuItem";
            this.newMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.N)));
            this.newMenuItem.Size = new System.Drawing.Size(176, 22);
            this.newMenuItem.Text = "&New";
            this.newMenuItem.Click += new System.EventHandler(this.newMenuItem_Click);
            // 
            // openMenuItem
            // 
            this.openMenuItem.ImageTransparentColor = System.Drawing.Color.Black;
            this.openMenuItem.Name = "openMenuItem";
            this.openMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.O)));
            this.openMenuItem.Size = new System.Drawing.Size(176, 22);
            this.openMenuItem.Text = "&Open";
            this.openMenuItem.Click += new System.EventHandler(this.OpenFileMenuItem_Click);
            // 
            // toolStripSeparator3
            // 
            this.toolStripSeparator3.Name = "toolStripSeparator3";
            this.toolStripSeparator3.Size = new System.Drawing.Size(173, 6);
            // 
            // saveMenuItem
            // 
            this.saveMenuItem.ImageTransparentColor = System.Drawing.Color.Black;
            this.saveMenuItem.Name = "saveMenuItem";
            this.saveMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.S)));
            this.saveMenuItem.Size = new System.Drawing.Size(176, 22);
            this.saveMenuItem.Text = "&Save";
            this.saveMenuItem.Click += new System.EventHandler(this.saveMenuItem_Click);
            // 
            // saveAsMenuItem
            // 
            this.saveAsMenuItem.Name = "saveAsMenuItem";
            this.saveAsMenuItem.ShortcutKeyDisplayString = "Ctrl+Shift+S";
            this.saveAsMenuItem.Size = new System.Drawing.Size(176, 22);
            this.saveAsMenuItem.Text = "Save &As";
            this.saveAsMenuItem.Click += new System.EventHandler(this.SaveAsMenuItem_Click);
            // 
            // toolStripSeparator5
            // 
            this.toolStripSeparator5.Name = "toolStripSeparator5";
            this.toolStripSeparator5.Size = new System.Drawing.Size(173, 6);
            // 
            // exitMenuItem
            // 
            this.exitMenuItem.Name = "exitMenuItem";
            this.exitMenuItem.Size = new System.Drawing.Size(176, 22);
            this.exitMenuItem.Text = "E&xit";
            this.exitMenuItem.Click += new System.EventHandler(this.ExitMenuItem_Click);
            // 
            // robotMenu
            // 
            this.robotMenu.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.editorMenuItem,
            this.importRobotMenuItem,
            this.packageRobotMenuItem,
            this.teamMenuItem});
            this.robotMenu.Name = "robotMenu";
            this.robotMenu.Size = new System.Drawing.Size(48, 20);
            this.robotMenu.Text = "&Robot";
            // 
            // editorMenuItem
            // 
            this.editorMenuItem.Name = "editorMenuItem";
            this.editorMenuItem.ShortcutKeyDisplayString = "";
            this.editorMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.E)));
            this.editorMenuItem.Size = new System.Drawing.Size(194, 22);
            this.editorMenuItem.Text = "&Editor";
            // 
            // importRobotMenuItem
            // 
            this.importRobotMenuItem.Name = "importRobotMenuItem";
            this.importRobotMenuItem.Size = new System.Drawing.Size(194, 22);
            this.importRobotMenuItem.Text = "&Import downloaded robot";
            // 
            // packageRobotMenuItem
            // 
            this.packageRobotMenuItem.Name = "packageRobotMenuItem";
            this.packageRobotMenuItem.Size = new System.Drawing.Size(194, 22);
            this.packageRobotMenuItem.Text = "&Package robot for upload";
            // 
            // teamMenuItem
            // 
            this.teamMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.createTeamMenuItem});
            this.teamMenuItem.Name = "teamMenuItem";
            this.teamMenuItem.Size = new System.Drawing.Size(194, 22);
            this.teamMenuItem.Text = "&Team";
            // 
            // createTeamMenuItem
            // 
            this.createTeamMenuItem.Name = "createTeamMenuItem";
            this.createTeamMenuItem.Size = new System.Drawing.Size(135, 22);
            this.createTeamMenuItem.Text = "&Create Team";
            // 
            // optionsToolStripMenuItem
            // 
            this.optionsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.preferencesMenuItem,
            this.toolStripSeparator2,
            this.defaultWindowSizeMenuItem,
            this.rankingPanelMenuItem,
            this.toolStripSeparator1,
            this.recalculateCPUConstantMenuItem,
            this.cleanRobotCacheMenuItem});
            this.optionsToolStripMenuItem.Name = "optionsToolStripMenuItem";
            this.optionsToolStripMenuItem.Size = new System.Drawing.Size(55, 20);
            this.optionsToolStripMenuItem.Text = "&Options";
            // 
            // preferencesMenuItem
            // 
            this.preferencesMenuItem.Name = "preferencesMenuItem";
            this.preferencesMenuItem.Size = new System.Drawing.Size(201, 22);
            this.preferencesMenuItem.Text = "&Preferences";
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(198, 6);
            // 
            // defaultWindowSizeMenuItem
            // 
            this.defaultWindowSizeMenuItem.Name = "defaultWindowSizeMenuItem";
            this.defaultWindowSizeMenuItem.Size = new System.Drawing.Size(201, 22);
            this.defaultWindowSizeMenuItem.Text = "&Default Window Size";
            // 
            // rankingPanelMenuItem
            // 
            this.rankingPanelMenuItem.Name = "rankingPanelMenuItem";
            this.rankingPanelMenuItem.Size = new System.Drawing.Size(201, 22);
            this.rankingPanelMenuItem.Text = "&Ranking Panel";
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(198, 6);
            // 
            // recalculateCPUConstantMenuItem
            // 
            this.recalculateCPUConstantMenuItem.Name = "recalculateCPUConstantMenuItem";
            this.recalculateCPUConstantMenuItem.Size = new System.Drawing.Size(201, 22);
            this.recalculateCPUConstantMenuItem.Text = "R&ecalculate CPU Constant";
            // 
            // cleanRobotCacheMenuItem
            // 
            this.cleanRobotCacheMenuItem.Name = "cleanRobotCacheMenuItem";
            this.cleanRobotCacheMenuItem.Size = new System.Drawing.Size(201, 22);
            this.cleanRobotCacheMenuItem.Text = "&Clean Robot Cache";
            // 
            // helpMenu
            // 
            this.helpMenu.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.onlineHelpMenuItem,
            this.robocodeAPIMenuItem,
            this.netDocumentationMenuItem,
            this.robocodeFAQMenuItem,
            this.toolStripSeparator6,
            this.robocodeHomeMenuItem,
            this.roboWikiMenuItem,
            this.yahooGroupMenuItem,
            this.robocodeRepositoryMenuItem,
            this.toolStripSeparator4,
            this.checkForNewVersionMenuItem,
            this.versionInfoMenuItem,
            this.toolStripSeparator7,
            this.aboutMenuItem});
            this.helpMenu.Name = "helpMenu";
            this.helpMenu.Size = new System.Drawing.Size(41, 20);
            this.helpMenu.Text = "&Help";
            // 
            // onlineHelpMenuItem
            // 
            this.onlineHelpMenuItem.Name = "onlineHelpMenuItem";
            this.onlineHelpMenuItem.Size = new System.Drawing.Size(205, 22);
            this.onlineHelpMenuItem.Text = "&Online Help";
            // 
            // robocodeAPIMenuItem
            // 
            this.robocodeAPIMenuItem.Name = "robocodeAPIMenuItem";
            this.robocodeAPIMenuItem.Size = new System.Drawing.Size(205, 22);
            this.robocodeAPIMenuItem.Text = "Robocode &API";
            // 
            // netDocumentationMenuItem
            // 
            this.netDocumentationMenuItem.Name = "netDocumentationMenuItem";
            this.netDocumentationMenuItem.Size = new System.Drawing.Size(205, 22);
            this.netDocumentationMenuItem.Text = ".&Net 2.0 Documentation";
            // 
            // robocodeFAQMenuItem
            // 
            this.robocodeFAQMenuItem.Name = "robocodeFAQMenuItem";
            this.robocodeFAQMenuItem.Size = new System.Drawing.Size(205, 22);
            this.robocodeFAQMenuItem.Text = "Robocode &FAQ";
            // 
            // toolStripSeparator6
            // 
            this.toolStripSeparator6.Name = "toolStripSeparator6";
            this.toolStripSeparator6.Size = new System.Drawing.Size(202, 6);
            // 
            // robocodeHomeMenuItem
            // 
            this.robocodeHomeMenuItem.Name = "robocodeHomeMenuItem";
            this.robocodeHomeMenuItem.Size = new System.Drawing.Size(205, 22);
            this.robocodeHomeMenuItem.Text = "Robocode &Home";
            // 
            // roboWikiMenuItem
            // 
            this.roboWikiMenuItem.Name = "roboWikiMenuItem";
            this.roboWikiMenuItem.Size = new System.Drawing.Size(205, 22);
            this.roboWikiMenuItem.Text = "Robo&Wiki";
            // 
            // yahooGroupMenuItem
            // 
            this.yahooGroupMenuItem.Name = "yahooGroupMenuItem";
            this.yahooGroupMenuItem.Size = new System.Drawing.Size(205, 22);
            this.yahooGroupMenuItem.Text = "&Yahoo Group for Robocode";
            // 
            // robocodeRepositoryMenuItem
            // 
            this.robocodeRepositoryMenuItem.Name = "robocodeRepositoryMenuItem";
            this.robocodeRepositoryMenuItem.Size = new System.Drawing.Size(205, 22);
            this.robocodeRepositoryMenuItem.Text = "Robocode &Repository";
            // 
            // toolStripSeparator4
            // 
            this.toolStripSeparator4.Name = "toolStripSeparator4";
            this.toolStripSeparator4.Size = new System.Drawing.Size(202, 6);
            // 
            // checkForNewVersionMenuItem
            // 
            this.checkForNewVersionMenuItem.Name = "checkForNewVersionMenuItem";
            this.checkForNewVersionMenuItem.Size = new System.Drawing.Size(205, 22);
            this.checkForNewVersionMenuItem.Text = "&Check for new version";
            // 
            // versionInfoMenuItem
            // 
            this.versionInfoMenuItem.Name = "versionInfoMenuItem";
            this.versionInfoMenuItem.Size = new System.Drawing.Size(205, 22);
            this.versionInfoMenuItem.Text = "&Version Info";
            // 
            // toolStripSeparator7
            // 
            this.toolStripSeparator7.Name = "toolStripSeparator7";
            this.toolStripSeparator7.Size = new System.Drawing.Size(202, 6);
            // 
            // aboutMenuItem
            // 
            this.aboutMenuItem.Name = "aboutMenuItem";
            this.aboutMenuItem.Size = new System.Drawing.Size(205, 22);
            this.aboutMenuItem.Text = "&About";
            // 
            // statusStrip
            // 
            this.statusStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel});
            this.statusStrip.Location = new System.Drawing.Point(0, 520);
            this.statusStrip.Name = "statusStrip";
            this.statusStrip.Size = new System.Drawing.Size(891, 22);
            this.statusStrip.TabIndex = 2;
            this.statusStrip.Text = "StatusStrip";
            // 
            // toolStripStatusLabel
            // 
            this.toolStripStatusLabel.Name = "toolStripStatusLabel";
            this.toolStripStatusLabel.Size = new System.Drawing.Size(37, 17);
            this.toolStripStatusLabel.Text = "Status";
            // 
            // BottomToolStripPanel
            // 
            this.BottomToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.BottomToolStripPanel.Name = "BottomToolStripPanel";
            this.BottomToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.BottomToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.BottomToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // TopToolStripPanel
            // 
            this.TopToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.TopToolStripPanel.Name = "TopToolStripPanel";
            this.TopToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.TopToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.TopToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // RightToolStripPanel
            // 
            this.RightToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.RightToolStripPanel.Name = "RightToolStripPanel";
            this.RightToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.RightToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.RightToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // LeftToolStripPanel
            // 
            this.LeftToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.LeftToolStripPanel.Name = "LeftToolStripPanel";
            this.LeftToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.LeftToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.LeftToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // ContentPanel
            // 
            this.ContentPanel.Size = new System.Drawing.Size(632, 382);
            // 
            // toolStrip1
            // 
            this.toolStrip1.Dock = System.Windows.Forms.DockStyle.None;
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.pauseDebugButton,
            this.nextTurnButton,
            this.stopButton,
            this.restartButton,
            this.toolStripSeparator8,
            this.speedSlider});
            this.toolStrip1.Location = new System.Drawing.Point(3, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.RenderMode = System.Windows.Forms.ToolStripRenderMode.System;
            this.toolStrip1.Size = new System.Drawing.Size(517, 45);
            this.toolStrip1.TabIndex = 20;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // pauseDebugButton
            // 
            this.pauseDebugButton.CheckOnClick = true;
            this.pauseDebugButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.pauseDebugButton.Name = "pauseDebugButton";
            this.pauseDebugButton.Size = new System.Drawing.Size(78, 42);
            this.pauseDebugButton.Text = "Pause/Debug";
            // 
            // nextTurnButton
            // 
            this.nextTurnButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.nextTurnButton.Name = "nextTurnButton";
            this.nextTurnButton.Size = new System.Drawing.Size(58, 42);
            this.nextTurnButton.Text = "Next Turn";
            // 
            // stopButton
            // 
            this.stopButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.stopButton.Name = "stopButton";
            this.stopButton.Size = new System.Drawing.Size(33, 42);
            this.stopButton.Text = "Stop";
            // 
            // restartButton
            // 
            this.restartButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.restartButton.Name = "restartButton";
            this.restartButton.Size = new System.Drawing.Size(45, 42);
            this.restartButton.Text = "Restart";
            // 
            // toolStripSeparator8
            // 
            this.toolStripSeparator8.Name = "toolStripSeparator8";
            this.toolStripSeparator8.Size = new System.Drawing.Size(6, 45);
            // 
            // speedSlider
            // 
            this.speedSlider.Name = "trackBar";
            this.speedSlider.Size = new System.Drawing.Size(256, 42);
            this.speedSlider.Text = "1";
            this.speedSlider.Maximum = 201;
            this.speedSlider.Minimum = 1;
            this.speedSlider.Scroll += new System.EventHandler(this.speedSlider_Scroll);
            // 
            // toolStripContainer1
            // 
            // 
            // toolStripContainer1.BottomToolStripPanel
            // 
            this.toolStripContainer1.BottomToolStripPanel.Controls.Add(this.toolStrip1);
            // 
            // toolStripContainer1.ContentPanel
            // 
            this.toolStripContainer1.ContentPanel.Controls.Add(this.splitContainer);
            this.toolStripContainer1.ContentPanel.Size = new System.Drawing.Size(891, 426);
            this.toolStripContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.toolStripContainer1.Location = new System.Drawing.Point(0, 24);
            this.toolStripContainer1.Name = "toolStripContainer1";
            this.toolStripContainer1.Size = new System.Drawing.Size(891, 496);
            this.toolStripContainer1.TabIndex = 24;
            this.toolStripContainer1.Text = "toolStripContainer1";
            // 
            // splitContainer
            // 
            this.splitContainer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer.Location = new System.Drawing.Point(0, 0);
            this.splitContainer.Name = "splitContainer";
            // 
            // splitContainer.Panel1
            // 
            this.splitContainer.Panel1.Controls.Add(this.battleView);
            // 
            // splitContainer.Panel2
            // 
            this.splitContainer.Panel2.Controls.Add(this.fpRobotButtons);
            this.splitContainer.Size = new System.Drawing.Size(891, 426);
            this.splitContainer.SplitterDistance = 741;
            this.splitContainer.TabIndex = 0;
            // 
            // battleView
            // 
            this.battleView.BackColor = System.Drawing.Color.Black;
            this.battleView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.battleView.Location = new System.Drawing.Point(0, 0);
            this.battleView.Name = "battleView";
            this.battleView.Size = new System.Drawing.Size(741, 426);
            this.battleView.TabIndex = 0;
            // 
            // fpRobotButtons
            // 
            this.fpRobotButtons.Dock = System.Windows.Forms.DockStyle.Fill;
            this.fpRobotButtons.FlowDirection = System.Windows.Forms.FlowDirection.TopDown;
            this.fpRobotButtons.Location = new System.Drawing.Point(0, 0);
            this.fpRobotButtons.Name = "fpRobotButtons";
            this.fpRobotButtons.Size = new System.Drawing.Size(146, 426);
            this.fpRobotButtons.TabIndex = 0;
            // 
            // RobocodeFrame
            // 
            this.AccessibleRole = System.Windows.Forms.AccessibleRole.None;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(891, 542);
            this.Controls.Add(this.toolStripContainer1);
            this.Controls.Add(this.statusStrip);
            this.Controls.Add(this.menuStrip);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.IsMdiContainer = true;
            this.MainMenuStrip = this.menuStrip;
            this.Name = "RobocodeFrame";
            this.Text = "Robocode";
            this.Load += new System.EventHandler(this.RobocodeFrame_Load);
            this.menuStrip.ResumeLayout(false);
            this.menuStrip.PerformLayout();
            this.statusStrip.ResumeLayout(false);
            this.statusStrip.PerformLayout();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.toolStripContainer1.BottomToolStripPanel.ResumeLayout(false);
            this.toolStripContainer1.BottomToolStripPanel.PerformLayout();
            this.toolStripContainer1.ContentPanel.ResumeLayout(false);
            this.toolStripContainer1.ResumeLayout(false);
            this.toolStripContainer1.PerformLayout();
            this.splitContainer.Panel1.ResumeLayout(false);
            this.splitContainer.Panel2.ResumeLayout(false);
            this.splitContainer.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }
        #endregion

        private System.Windows.Forms.MenuStrip menuStrip;
        private System.Windows.Forms.StatusStrip statusStrip;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator5;
        private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel;
        private System.Windows.Forms.ToolStripMenuItem aboutMenuItem;
        private System.Windows.Forms.ToolStripMenuItem battleMenu;
        private System.Windows.Forms.ToolStripMenuItem newMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveAsMenuItem;
        private System.Windows.Forms.ToolStripMenuItem exitMenuItem;
        private System.Windows.Forms.ToolStripMenuItem robotMenu;
        private System.Windows.Forms.ToolStripMenuItem helpMenu;
        private System.Windows.Forms.ToolStripMenuItem optionsToolStripMenuItem;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton pauseDebugButton;
        private System.Windows.Forms.ToolStripButton nextTurnButton;
        private System.Windows.Forms.ToolStripButton stopButton;
        private System.Windows.Forms.ToolStripButton restartButton;
        private System.Windows.Forms.ToolStripPanel BottomToolStripPanel;
        private System.Windows.Forms.ToolStripPanel TopToolStripPanel;
        private System.Windows.Forms.ToolStripPanel RightToolStripPanel;
        private System.Windows.Forms.ToolStripPanel LeftToolStripPanel;
        private System.Windows.Forms.ToolStripContentPanel ContentPanel;
        private System.Windows.Forms.ToolStripContainer toolStripContainer1;
        private System.Windows.Forms.ToolStripMenuItem editorMenuItem;
        private System.Windows.Forms.ToolStripMenuItem importRobotMenuItem;
        private System.Windows.Forms.ToolStripMenuItem packageRobotMenuItem;
        private System.Windows.Forms.ToolStripMenuItem teamMenuItem;
        private System.Windows.Forms.ToolStripMenuItem createTeamMenuItem;
        private System.Windows.Forms.ToolStripMenuItem preferencesMenuItem;
        private System.Windows.Forms.ToolStripMenuItem defaultWindowSizeMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripMenuItem rankingPanelMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripMenuItem recalculateCPUConstantMenuItem;
        private System.Windows.Forms.ToolStripMenuItem cleanRobotCacheMenuItem;
        private System.Windows.Forms.ToolStripMenuItem onlineHelpMenuItem;
        private System.Windows.Forms.ToolStripMenuItem robocodeAPIMenuItem;
        private System.Windows.Forms.ToolStripMenuItem netDocumentationMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator4;
        private System.Windows.Forms.ToolStripMenuItem robocodeFAQMenuItem;
        private System.Windows.Forms.ToolStripMenuItem robocodeHomeMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator6;
        private System.Windows.Forms.ToolStripMenuItem roboWikiMenuItem;
        private System.Windows.Forms.ToolStripMenuItem yahooGroupMenuItem;
        private System.Windows.Forms.ToolStripMenuItem robocodeRepositoryMenuItem;
        private System.Windows.Forms.ToolStripMenuItem checkForNewVersionMenuItem;
        private System.Windows.Forms.ToolStripMenuItem versionInfoMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator7;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator8;
        private System.Windows.Forms.SaveFileDialog saveFileDialog;
        private System.Windows.Forms.SplitContainer splitContainer;
        private battleview.BattleView battleView;
        private System.Windows.Forms.FlowLayoutPanel fpRobotButtons;
        private System.Windows.Forms.OpenFileDialog openFileDialog;
        private SliderToolStrip speedSlider;

    }
}



