using System.Drawing;
using System.Windows.Forms;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.battleview
{
    public partial class BattleView : UserControl, IBattleView
    {
        public BattleView()
        {
            InitializeComponent();
        }


        #region IBattleView Members

        public void setBattleField(BattleField bf)
        {
            //TODO
        }

        public void setVisible(bool b)
        {
            //TODO
        }

        public void setInitialized(bool b)
        {
            //TODO
        }

        public void setBattle(Battle b)
        {
            //TODO
        }

        public void repaint()
        {
            //TODO
        }

        public void setDisplayOptions()
        {
            //TODO
        }

        public void update()
        {
            //TODO
        }

        public bool isDisplayTPS()
        {
            //TODO
            return false;
        }

        public bool isDisplayFPS()
        {
            //TODO
            return false;
        }

        public void setTitle(string str)
        {
            //TODO move to frame ?
        }

        public int getWidth()
        {
            return Width;
        }

        public int getHeight()
        {
            return Height;
        }

        #endregion

        private void BattleView_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;

            //g = Graphics.FromImage(resources.images.body);

            g.RotateTransform(15);
            g.TranslateTransform(100, 100);
            e.Graphics.DrawImage(resources.images.body, new Point(100, 100));
            g.ResetTransform();

            g.RotateTransform(65);
            g.TranslateTransform(100, 40);
            e.Graphics.DrawImage(resources.images.body, new Point(100, 40));
            g.ResetTransform();
        }
    }
}
