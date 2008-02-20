using robocode.battlefield;

namespace nrobocodeui.battleview
{
    partial class BattleView
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

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.SuspendLayout();
            // 
            // BattleView
            // 
            this.BackColor = System.Drawing.Color.Black;
            this.DoubleBuffered = true;
            this.Name = "BattleView";
            this.MouseLeave += new System.EventHandler(this.BattleView_MouseLeave);
            this.MouseMove += new System.Windows.Forms.MouseEventHandler(this.BattleView_MouseMove);
            this.KeyUp += new System.Windows.Forms.KeyEventHandler(this.BattleView_KeyUp);
            this.MouseClick += new System.Windows.Forms.MouseEventHandler(this.BattleView_MouseClick);
            this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.BattleView_MouseDown);
            this.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.BattleView_KeyPress);
            this.MouseUp += new System.Windows.Forms.MouseEventHandler(this.BattleView_MouseUp);
            this.MouseEnter += new System.EventHandler(this.BattleView_MouseEnter);
            this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.BattleView_KeyDown);
            this.ResumeLayout(false);

        }

        #endregion
    }
}
