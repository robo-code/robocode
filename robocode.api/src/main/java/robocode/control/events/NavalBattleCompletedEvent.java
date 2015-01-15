package robocode.control.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import robocode.BattleEndedEvent;
import robocode.BattleResults;
import robocode.BattleRules;
import robocode.NavalBattleResults;

/**
 * Contains the battle results returned by {@link BattleEndedEvent#getResults()}
 * when a battle has ended.
 *
 * @see NavalBattleEndedEvent#getResults()
 * @see Ship#onBattleEnded(BattleEndedEvent)
 * 
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Thales B.V. / Thomas Hakkers
 *
 * @since 1.9.1.2
 */
public class NavalBattleCompletedEvent extends BattleCompletedEvent{

	public NavalBattleCompletedEvent(BattleRules battleRules,
			NavalBattleResults[] results) {
		super(battleRules, results);
	}

	/**
	 * Returns the battle results sorted on score.
	 * Note that the robot index cannot be used to determine the score with the sorted results.
	 * Overrides {@link BattleResults#getSortedResults()} and instead returns an array with NavalBattleResults.
	 *
	 * @return an array of sorted BattleResults, where the results with the bigger score are placed first in the list.
	 * @see #getIndexedResults()
	 */
	public BattleResults[] getSortedResults() {
		List<NavalBattleResults> copy = new ArrayList<NavalBattleResults>(Arrays.asList((NavalBattleResults[])results));

		Collections.sort(copy);
		Collections.reverse(copy);
		return copy.toArray(new NavalBattleResults[copy.size()]);
	}

	/**
	 * Returns the battle results that can be used to determine the score for the individual ship based
	 * on the ship index.
	 * Overrides {@link BattleResults#getIndexedResults()} and instead returns an array with NavalBattleResults.
	 * 
	 * @return an array of indexed NavalBattleResults, where each index matches an index of a specific robot.
	 * @see #getSortedResults()
	 */
	@Override
	public BattleResults[] getIndexedResults() {
		NavalBattleResults[] copy = new NavalBattleResults[results.length];

		System.arraycopy(results, 0, copy, 0, results.length);	
		return copy;
	}

}
