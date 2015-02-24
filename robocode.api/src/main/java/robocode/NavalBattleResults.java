package robocode;

import java.nio.ByteBuffer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * Contains the Naval version of the battle results returned by {@link BattleEndedEvent#getResults()}
 * when a battle has ended.
 * Technically the same as {@link BattleResults}, but with the added bonus of
 * mineDamage and mineBonus.
 *
 * @see BattleEndedEvent#getResults()
 * @see Ship#onBattleEnded(BattleEndedEvent)
 *
 * @author Thales B.V. / Thomas Hakkers
 *
 * @since 1.9.2.2
 */
public class NavalBattleResults extends BattleResults{
	private static final long serialVersionUID = 4562531939960517227L;
	
	private double mineDamage;
	private double mineDamageBonus;

	/**
	 * Constructs this NavalBattleResults object.
	 *
	 * @param teamLeaderName    the name of the team leader.
	 * @param rank              the rank of the ship in the battle.
	 * @param score             the total score for the ship in the battle.
	 * @param survival          the survival score for the ship in the battle.
	 * @param lastSurvivorBonus the last survivor bonus for the ship in the battle.
	 * @param bulletDamage      the bullet damage score for the ship in the battle.
	 * @param bulletDamageBonus the bullet damage bonus for the ship in the battle.
	 * @param ramDamage         the ramming damage for the ship in the battle.
	 * @param ramDamageBonus    the ramming damage bonus for the ship in the battle.
	 * @param mineDamage		the mine damage score for the ship in the battle.
	 * @param mineBonus			the mine damage bonus for the ship in the battle.
	 * @param firsts            the number of rounds this ship placed first.
	 * @param seconds           the number of rounds this ship placed second.
	 * @param thirds            the number of rounds this ship placed third.
	 */
	public NavalBattleResults(String teamLeaderName, int rank, double score,
			double survival, double lastSurvivorBonus, double bulletDamage,
			double bulletDamageBonus, double ramDamage, double ramDamageBonus,
			double mineDamage, double mineDamageBonus,
			int firsts, int seconds, int thirds) {
		super(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage,
				bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, thirds);
		this.mineDamage = mineDamage;
		this.mineDamageBonus = mineDamageBonus;
	}

	/**
	 * @returns an int value of the mine damage
	 */
	public int getMineDamage(){
		return (int)(mineDamage + 0.5);
	}
	
	/**
	 * @returns an int value of the mine damage bonus
	 */
	public int getMineDamageBonus(){
		return (int)(mineDamageBonus + 0.5);
	}
	
	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			NavalBattleResults obj = (NavalBattleResults) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.teamLeaderName) + 4 * RbSerializer.SIZEOF_INT
					+ 9 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			NavalBattleResults obj = (NavalBattleResults) object;

			serializer.serialize(buffer, obj.teamLeaderName);
			serializer.serialize(buffer, obj.rank);
			serializer.serialize(buffer, obj.score);
			serializer.serialize(buffer, obj.survival);
			serializer.serialize(buffer, obj.lastSurvivorBonus);
			serializer.serialize(buffer, obj.bulletDamage);
			serializer.serialize(buffer, obj.bulletDamageBonus);
			serializer.serialize(buffer, obj.ramDamage);
			serializer.serialize(buffer, obj.ramDamageBonus);
			serializer.serialize(buffer, obj.firsts);
			serializer.serialize(buffer, obj.seconds);
			serializer.serialize(buffer, obj.thirds);
			serializer.serialize(buffer, obj.mineDamage);
			serializer.serialize(buffer, obj.mineDamageBonus);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String teamLeaderName = serializer.deserializeString(buffer);
			int rank = buffer.getInt();
			double score = buffer.getDouble();
			double survival = buffer.getDouble();
			double lastSurvivorBonus = buffer.getDouble();
			double bulletDamage = buffer.getDouble();
			double bulletDamageBonus = buffer.getDouble();
			double ramDamage = buffer.getDouble();
			double ramDamageBonus = buffer.getDouble();
			int firsts = buffer.getInt();
			int seconds = buffer.getInt();
			int thirds = buffer.getInt();
			double mineDamage = buffer.getDouble();
			double mineDamageBonus = buffer.getDouble();

			return new NavalBattleResults(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage,
					bulletDamageBonus, ramDamage, ramDamageBonus, mineDamage, mineDamageBonus, firsts, seconds, thirds);
		}
	}

}
