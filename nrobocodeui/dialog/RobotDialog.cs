using System;
using System.Text;
using System.Windows.Forms;
using java.io;
using robocode.io;
using robocode.peer;

namespace nrobocodeui.dialog
{
    public partial class RobotDialog : Form
    {
        public RobotDialog()
        {
            InitializeComponent();
        }


        private BufferedPipedInputStream stream;

        public void setRobotPeer(RobotPeer robotPeer) 
        {
            stream = robotPeer.getOut().getInputStream();
            Text = robotPeer.getName();
        }

        StringBuilder sb = new StringBuilder();

        private void RobotDialog_Load(object sender, EventArgs e)
        {
            backgroundWorker.WorkerSupportsCancellation = true;
            backgroundWorker.RunWorkerAsync();
        }

        private void RobotDialog_FormClosing(object sender, FormClosingEventArgs e)
        {
            backgroundWorker.CancelAsync();
        }

        private void backgroundWorker_DoWork(object sender, System.ComponentModel.DoWorkEventArgs e)
        {
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                string line = reader.readLine();
                while (line != null)
                {
                    if (backgroundWorker.CancellationPending)
                        break;
                    sb.AppendLine(line);
                    line = reader.readLine();
                    
                    BeginInvoke(new utils.Action<string>(UpdateText), new object[]{sb.ToString()});
                }

            }
            catch (Exception)
            {
                //swalow
            }
        }

        private void UpdateText(string text)
        {
            console.Text = text;
            console.Select(text.Length, 0);
            console.ScrollToCaret();
        }

    }
}
