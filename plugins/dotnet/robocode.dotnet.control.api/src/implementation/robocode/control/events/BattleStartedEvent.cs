using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleStartedEvent is sent to {@link IBattleListener#onBattleStarted(BattleStartedEvent)
     * onBattleStarted()} when a new battle is started. 
     *
     * @see IBattleListener
     * @see BattleCompletedEvent
     * @see BattleFinishedEvent
     *
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleStartedEvent : BattleEvent
    {
	    private readonly BattleRules battleRules;
	    private readonly bool isReplay;
        private readonly int robotsCount;

	    /**
	     * Called by the game to create a new BattleStartedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param battleRules the rules that will be used in the battle.
	     * @param robotsCount the number of robots participating in the battle.
	     * @param isReplay a flag specifying if this battle is a replay or real battle:
	     *                 {@code true} if the battle is a replay; {@code false} otherwise.
	     */
	    public BattleStartedEvent(BattleRules battleRules, int robotsCount, bool isReplay) : base()
        {
		    this.battleRules = battleRules;
		    this.isReplay = isReplay;
		    this.robotsCount = robotsCount;
	    }

	    /**
	     * Returns the rules that will be used in the battle.
	     *
	     * @return the rules that will be used in the battle.
	     */
	    public BattleRules BattleRules
        {
            get { return battleRules; }
	    }

	    /**
	     * Returns the number of robots participating in the battle.
	     * 
	     * @return the number of robots participating in the battle.
	     */
	    public int RobotsCount {
            get { return robotsCount; }
	    }

	    /**
	     * Checks if this battle is a replay or real battle.
	     *
	     * @return {@code true} if the battle is a replay; {@code false} otherwise.
	     */
	    public bool IsReplay {
            get { return isReplay; }
	    }
    }
}
