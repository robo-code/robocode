package robocode.control;

import robocode.NavalBattleResults;

/**
 * Handles the results for a specific Ship
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class ShipResults extends NavalBattleResults implements IResults{

	private static final long serialVersionUID = 2L;
	private final RobotSpecification ship;

	/**
	 * Constructs a new ShipResults.
	 *
	 * @param ship is the ship these results are for
	 * @param teamLeaderName is the team name
	 * @param rank is the rank of the ship in the battle
	 * @param score is the total score for the ship in the battle
	 * @param survival is the survival score for the ship in the battle
	 * @param lastSurvivorBonus is the last survivor bonus for the ship in the battle
	 * @param bulletDamage is the bullet damage score for the ship in the battle
	 * @param bulletDamageBonus is the bullet damage bonus for the ship in the battle
	 * @param ramDamage is the ramming damage for the ship in the battle
	 * @param ramDamageBonus is the ramming damage bonus for the ship in the battle
	 * @param mineDamage is the damage the mines of this ship have done in this battle
	 * @param mineDamageBonus is the mine bonus for the ship
	 * @param firsts is the number of rounds this ship placed first
	 * @param seconds is the number of rounds this ship placed second
	 * @param thirds is the number of rounds this ship placed third
	 */
	public ShipResults(
			RobotSpecification ship,
			String teamLeaderName,
			int rank,
			double score,
			double survival,
			double lastSurvivorBonus,
			double bulletDamage,
			double bulletDamageBonus,
			double ramDamage,
			double ramDamageBonus,
			double mineDamage,
			double mineDamageBonus,
			int firsts,
			int seconds,
			int thirds
			) {
		super(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage, bulletDamageBonus, ramDamage,
				ramDamageBonus,mineDamage, mineDamageBonus, firsts, seconds, thirds);
		this.ship = ship;
	}

	/**
	 * Constructs new ShipResults based on a {@link RobotSpecification} and {@link robocode.NavalBattleResults NavalBattleResults}.
	 *
	 * @param ship   the ship these results are for
	 * @param results the battle results for the ship
	 */
	public ShipResults(
			RobotSpecification ship,
			NavalBattleResults results) {
		super(results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(),
				results.getLastSurvivorBonus(), results.getBulletDamage(), results.getBulletDamageBonus(),
				results.getRamDamage(), results.getRamDamageBonus(),
				results.getMineDamage(), results.getMineDamageBonus(),
				results.getFirsts(), results.getSeconds(),
				results.getThirds());
		this.ship = ship;
	}

	/**
	 * Returns the ship these results are meant for.
	 * Note that ships use RobotSpecifications, just like robots.
	 *
	 * @return the ship these results are meant for.
	 */
	public RobotSpecification getRobot() {
		return ship;
	}

	/**
	 * Converts an array of {@link NavalBattleResults} into an array of {@link ShipResults}.
	 *
	 * @param results an array of NavalBattleResults to convert.
	 * @return an array of ShipResults converted from NavalBattleResults.
	 * @since 1.9.1.2
	 */
	public static ShipResults[] convertResults(NavalBattleResults[] results) {
		ShipResults[] resultsConv = new ShipResults[results.length];

		for (int i = 0; i < results.length; i++) {
			resultsConv[i] = (ShipResults) results[i];
		}
		return resultsConv;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;

		temp = Double.doubleToLongBits(bulletDamage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(bulletDamageBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + firsts;
		temp = Double.doubleToLongBits(lastSurvivorBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ramDamage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ramDamageBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + rank;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + seconds;
		temp = Double.doubleToLongBits(survival);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((teamLeaderName == null) ? 0 : teamLeaderName.hashCode());
		result = prime * result + thirds;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ShipResults other = (ShipResults) obj;

		if (Double.doubleToLongBits(bulletDamage) != Double.doubleToLongBits(other.bulletDamage)) {
			return false;
		}
		if (Double.doubleToLongBits(bulletDamageBonus) != Double.doubleToLongBits(other.bulletDamageBonus)) {
			return false;
		}
		if (firsts != other.firsts) {
			return false;
		}
		if (Double.doubleToLongBits(lastSurvivorBonus) != Double.doubleToLongBits(other.lastSurvivorBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(ramDamage) != Double.doubleToLongBits(other.ramDamage)) {
			return false;
		}
		if (Double.doubleToLongBits(ramDamageBonus) != Double.doubleToLongBits(other.ramDamageBonus)) {
			return false;
		}
		if (rank != other.rank) {
			return false;
		}
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score)) {
			return false;
		}
		if (seconds != other.seconds) {
			return false;
		}
		if (Double.doubleToLongBits(survival) != Double.doubleToLongBits(other.survival)) {
			return false;
		}
		if (teamLeaderName == null) {
			if (other.teamLeaderName != null) {
				return false;
			}
		} else if (!teamLeaderName.equals(other.teamLeaderName)) {
			return false;
		}
		if (thirds != other.thirds) {
			return false;
		}
		return true;
	}
}
