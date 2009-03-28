using IKVM.Attributes;
using java.lang;

namespace robocode.control.events
{
    [Implements(new[] {"robocode.control.events.IBattleListener"})]
    public abstract class BattleAdaptor : Object, IBattleListener
    {
        #region IBattleListener Members

        public virtual void onBattleCompleted(BattleCompletedEvent @event)
        {
        }

        public virtual void onBattleError(BattleErrorEvent @event)
        {
        }

        public virtual void onBattleFinished(BattleFinishedEvent @event)
        {
        }

        public virtual void onBattleMessage(BattleMessageEvent @event)
        {
        }

        public virtual void onBattlePaused(BattlePausedEvent @event)
        {
        }

        public virtual void onBattleResumed(BattleResumedEvent @event)
        {
        }

        public virtual void onBattleStarted(BattleStartedEvent @event)
        {
        }

        public virtual void onRoundEnded(RoundEndedEvent @event)
        {
        }

        public virtual void onRoundStarted(RoundStartedEvent @event)
        {
        }

        public virtual void onTurnEnded(TurnEndedEvent @event)
        {
        }

        public virtual void onTurnStarted(TurnStartedEvent @event)
        {
        }

        #endregion
    }
}