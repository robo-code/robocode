namespace robocode.robotinterfaces
{
    public interface IAdvancedEvents
    {
        void onCustomEvent(CustomEvent ce);
        void onSkippedTurn(SkippedTurnEvent ste);
    }
}