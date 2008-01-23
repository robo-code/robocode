using System;
using System.Windows.Forms;
using nrobocodeui.battleview;
using robocode.peer;
using robocode.ui;
using File=java.io.File;

namespace nrobocodeui.dialog
{
    public partial class RobocodeFrame : Form, IRobocodeFrame
    {
        #region Constructors

        public RobocodeFrame()
        {
            InitializeComponent();
        }

        #endregion

        #region Private members

        private int childFormNumber = 0;
        private BattleViewStub battleView;
        private FormWindowState lastState = FormWindowState.Normal;

        #endregion

        #region Event Handlers

        private void ShowNewForm(object sender, EventArgs e)
        {
            Form childForm = new Form();
            childForm.MdiParent = this;
            childForm.Text = "Window " + childFormNumber++;
            childForm.Show();
        }

        private void OpenFile(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            openFileDialog.Filter = "Text Files (*.txt)|*.txt|All Files (*.*)|*.*";
            if (openFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                string FileName = openFileDialog.FileName;
            }
        }

        private void SaveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveAs(null);
            //TODO ? call save
        }

        #endregion

        #region IRobocodeFrame Members

        public IBattleView getBattleView()
        {
            if (battleView == null)
            {
                //TODO
                battleView = new BattleViewStub();
            }
            return battleView;
        }

        public void setEnableBattleSaveAsMenuItem(bool b)
        {
            saveAsToolStripMenuItem.Enabled = b;
        }

        public void setEnableBattleSaveMenuItem(bool b)
        {
            saveToolStripMenuItem.Enabled = b;
        }

        public string saveBattleDialog(File f)
        {
            return SaveAs(f.getAbsolutePath());
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
            //TODO
        }

        public void addRobotButton(IRobotDialogManager irdm, RobotPeer rp)
        {
            //TODO
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

        private string SaveAs(string fileName)
        {
            saveFileDialog.FileName = fileName;
            saveFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            saveFileDialog.Filter = "Text Files (*.txt)|*.txt|All Files (*.*)|*.*";
            if (saveFileDialog.ShowDialog(this) == DialogResult.OK)
            {
                return saveFileDialog.FileName;
            }
            return null;
        }

        #endregion
    }
}