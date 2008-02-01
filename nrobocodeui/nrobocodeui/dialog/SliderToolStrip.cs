using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Text;
using System.Windows.Forms;

namespace nrobocodeui.dialog
{
    public partial class SliderToolStrip : ToolStripControlHost
    {
        public SliderToolStrip()
            : base(new TrackBar(), "trackBar")
        {
            trackBar = this.Control as TrackBar;
            InitializeComponent();
        }

        public SliderToolStrip(IContainer container)
            : base(new TrackBar(), "trackBar")
        {
            trackBar = this.Control as TrackBar;
            //trackBar.Size = new System.Drawing.Size(256, 42);
            trackBar.Scroll += new EventHandler(trackBar_Scroll);
            trackBar.ValueChanged += new EventHandler(trackBar_ValueChanged);
            trackBar.AutoSize = true;
            
            container.Add(this);

            InitializeComponent();
        }

        public TrackBar TrackBar
        {
            get
            {
                return trackBar;
            }
        }

        public int Maximum
        {
            get
            {
                return trackBar.Maximum;
            }
            set
            {
                trackBar.Maximum = value;
            }
        }

        public int Minimum
        {
            get
            {
                return trackBar.Minimum;
            }
            set
            {
                trackBar.Minimum = value;
            }
        }


        public int Value
        {
            get
            {
                return trackBar.Value;
            }
            set
            {
                trackBar.Value = value;
            }
        }

        public override string Text
        {
            get
            {
                return base.Text;
            }
            set
            {
                trackBar.Text = value;
                base.Text = value;
            }
        }

        public event System.EventHandler Scroll;

        void trackBar_ValueChanged(object sender, EventArgs e)
        {
            if (Scroll != null)
                Scroll(sender, e);
        }

        void trackBar_Scroll(object sender, EventArgs e)
        {
            if (Scroll != null)
                Scroll(sender, e);
        }

        private System.Windows.Forms.TrackBar trackBar;
    }
}
