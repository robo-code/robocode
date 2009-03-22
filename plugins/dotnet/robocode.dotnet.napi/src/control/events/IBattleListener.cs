namespace robocode.control.events
{
    public interface IBattleListener
    {
        void onBattleCompleted(BattleCompletedEvent bce);
        void onBattleError(BattleErrorEvent bee);
        void onBattleFinished(BattleFinishedEvent bfe);
        void onBattleMessage(BattleMessageEvent bme);
        void onBattlePaused(BattlePausedEvent bpe);
        void onBattleResumed(BattleResumedEvent bre);
        void onBattleStarted(BattleStartedEvent bse);
        void onRoundEnded(RoundEndedEvent ree);
        void onRoundStarted(RoundStartedEvent rse);
        void onTurnEnded(TurnEndedEvent tee);
        void onTurnStarted(TurnStartedEvent tse);
    }
}