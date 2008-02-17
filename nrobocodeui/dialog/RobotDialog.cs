using System;
using System.Windows.Forms;
using java.io;
using java.nio;
using robocode.io;
using robocode.manager;
using robocode.peer;

namespace nrobocodeui.dialog
{
    public partial class RobotDialog : Form
    {
        public RobotDialog()
        {
            InitializeComponent();
        }


        private RobocodeManager manager;
        private RobotPeer robotPeer;
        private BufferedPipedInputStream stream;

        public void setRobotPeer(RobotPeer robotPeer) 
        {
            this.robotPeer = robotPeer;
            stream = robotPeer.getOut().getInputStream();
            this.Text = robotPeer.getName();
        }

        CharBuffer cb = CharBuffer.allocate(10000);

        private void timer_Tick(object sender, EventArgs e)
        {
            //TODO I'm too lazy now to do it better way, sorry
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                string line;

                cb.clear();
                while (reader.ready())
                {
                    reader.read(cb);
                }

                console.Text = cb.toString();
                console.Select(console.Text.Length, 0);
                console.ScrollToCaret();
            }
            catch(Exception ex)
            {
                //swalow
            }
        }

        private void RobotDialog_Activated(object sender, EventArgs e)
        {
            timer.Enabled = true;
        }

        private void RobotDialog_Deactivate(object sender, EventArgs e)
        {
            timer.Enabled = false;
        }

    }
}
