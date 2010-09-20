using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleMessageEvent is sent to {@link IBattleListener#onBattleMessage(BattleMessageEvent)
     * onBattleMessage()} when an information message is sent from the game in the during the battle. 
     *
     * @see IBattleListener
     * @see BattleErrorEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleMessageEvent : BattleEvent
    {
	    private readonly string message;

	    /**
	     * Called by the game to create a new BattleMessageEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param message the information message.
	     */
	    public BattleMessageEvent(string message) : base()
        {
		    this.message = message;
	    }

	    /**
	     * Returns the information message.
	     *
	     * @return the information message.
	     */
	    public string Message {
            get { return message; }
	    }
    }
}
