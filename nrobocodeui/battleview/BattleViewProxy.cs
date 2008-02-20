using System;
using System.ComponentModel;
using nrobocodeui.utils;
using robocode.battle;
using robocode.battlefield;
using robocode.ui;

namespace nrobocodeui.battleview
{
    /// <summary>
    /// Proxy
    /// </summary>
    public class BattleViewProxy : IBattleView
    {
        public BattleViewProxy(IBattleView battleView, ISynchronizeInvoke synchronizer)
        {
            this.battleView = battleView;
            this.synchronizer = synchronizer;
            closing = false;
        }

        private IBattleView battleView;
        private ISynchronizeInvoke synchronizer;
        private bool closing;

        public void OnClosing()
        {
            ISynchronizeInvoke s = synchronizer;
            closing = true;
        }

        public void setBattleField(BattleField value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new System.Action<BattleField>(battleView.setBattleField), new object[] {value});
            }
        }

        public void setVisible(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new System.Action<bool>(battleView.setVisible), new object[] {value});
            }
        }

        public void setInitialized(bool value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new System.Action<bool>(battleView.setInitialized), new object[] {value});
            }
        }

        public void setBattle(Battle value)
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new System.Action<Battle>(battleView.setBattle), new object[] {value});
            }
        }

        public void repaint()
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new Action(battleView.repaint), new object[] {});
            }
        }

        public void setDisplayOptions()
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new Action(battleView.setDisplayOptions), new object[] {});
            }
        }

        public void update()
        {
            if (closing)
                return;
            //lock (synchronizer)
            {
                synchronizer.Invoke(new Action(battleView.update), new object[] {});
            }
        }

        #region Fast

        public bool isDisplayTPS()
        {
            if (closing)
                return false;
            return battleView.isDisplayTPS();
        }

        public bool isDisplayFPS()
        {
            if (closing)
                return false;
            return battleView.isDisplayFPS();
        }

        public int getWidth()
        {
            if (closing)
                return 0;
            return battleView.getWidth();
        }

        public int getHeight()
        {
            if (closing)
                return 0;
            return battleView.getHeight();
        }

        #endregion
    }
}
