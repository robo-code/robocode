﻿using System.Windows.Forms;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.nrobocodeui.battleview
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
            //TODO
        }

        public int getWidth()
        {
            //TODO
            return 0;
        }

        public int getHeight()
        {
            //TODO
            return 0;
        }

        #endregion
    }
}
