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
        }

        private IBattleView battleView;
        private ISynchronizeInvoke synchronizer;


        public void setBattleField(BattleField value)
        {
            synchronizer.Invoke(new System.Action<BattleField>(battleView.setBattleField), new object[] { value });
        }

        public void setVisible(bool value)
        {
            synchronizer.Invoke(new System.Action<bool>(battleView.setVisible), new object[] { value });
        }

        public void setInitialized(bool value)
        {
            synchronizer.Invoke(new System.Action<bool>(battleView.setInitialized), new object[] { value });
        }

        public void setBattle(Battle value)
        {
            synchronizer.Invoke(new System.Action<Battle>(battleView.setBattle), new object[] { value });
        }

        public void repaint()
        {
            synchronizer.Invoke(new Action(battleView.repaint), new object[]{});
        }

        public void setDisplayOptions()
        {
            synchronizer.Invoke(new Action(battleView.setDisplayOptions), new object[] { });
        }

        public void update()
        {
            synchronizer.Invoke(new Action(battleView.update), new object[] { });
        }

        #region Fast

        public bool isDisplayTPS()
        {
            //return (bool)synchronizer.Invoke(new Delegate<bool>(battleView.isDisplayTPS), new object[] { });
            return battleView.isDisplayTPS();
        }

        public bool isDisplayFPS()
        {
            //return (bool)synchronizer.Invoke(new Delegate<bool>(battleView.isDisplayFPS), new object[] { });
            return battleView.isDisplayFPS();
        }

        public int getWidth()
        {
            //return (int)synchronizer.Invoke(new Delegate<int>(battleView.getWidth), new object[] { });
            return battleView.getWidth();
        }

        public int getHeight()
        {
            //return (int)synchronizer.Invoke(new Delegate<int>(battleView.getHeight), new object[] { });
            return battleView.getHeight();
        }

        #endregion
    }
}
