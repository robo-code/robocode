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
            this.battleField = new DefaultBattleField(800, 600);
            // 
            // BattleView
            // 
            this.SuspendLayout();

            this.Name = "BattleView";
//            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
//            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.Black;
            this.DoubleBuffered = true;
//            this.Size = new System.Drawing.Size(435, 390);*/
            this.Paint += new System.Windows.Forms.PaintEventHandler(this.BattleView_Paint);

            this.ResumeLayout(false);
        }

        #endregion
    }
}
