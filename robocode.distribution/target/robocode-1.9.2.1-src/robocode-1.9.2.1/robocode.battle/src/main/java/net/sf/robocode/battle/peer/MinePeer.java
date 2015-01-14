package net.sf.robocode.battle.peer;

import static java.lang.Math.sqrt;

import java.awt.geom.Rectangle2D;
import java.util.List;

import net.sf.robocode.battle.BoundingRectangle;
import net.sf.robocode.peer.MineStatus;
import robocode.BattleRules;
import robocode.HitByMineEvent;
import robocode.Mine;
import robocode.MineHitEvent;
import robocode.control.snapshot.MineState;
import robocode.naval.NavalRules;
import robocode.naval.interfaces.IProjectile;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Collision;
import robocode.MineHitMineEvent;

/**
 * Manages everything about a certain Mine.
 * Heavily influenced by {@link BulletPeer}.
 * @author Thales B.V. / Thomas Hakkers
 */
public class MinePeer implements IProjectile{
	MinePeer mine;
	private static final int EXPLOSION_LENGTH = 17;

	protected final ShipPeer owner;

	private final BattleRules battleRules;
	private final int mineId;

	protected ShipPeer victim;

	protected MineState state;

	protected double x;
	protected double y;

	protected double power;
	
	private BoundingRectangle boundingBox = new BoundingRectangle();

	
	protected int frame; // Do not set to -1

	protected int explosionImageIndex; // Do not set to -1
	
	/**
	 * Constructor for the MinePeer
	 * @param owner The ship this mine was launched from
	 * @param battleRules The rules this battle is currently holding
	 * @param mineId The id of the mine
	 */
	public MinePeer(ShipPeer owner, BattleRules battleRules, int mineId) {
		super();
		this.owner = owner;
		this.battleRules = battleRules;
		this.mineId = mineId;
		state = MineState.PLACED;
	}
	
	private MineStatus createStatus() {
		return new MineStatus(mineId, x, y, victim == null ? null : getNameForEvent(victim), isActive());
	}
	
	/**
	 * Creates a Mine object for the user
	 * @param hideOwnerName true if owner shouldn't be visible, false otherwise
	 * @return a Mine object holding a variety of variables of a Mine.
	 */
	private Mine createMine(boolean hideOwnerName) {
		String ownerName = (owner == null) ? null : (hideOwnerName ? getNameForEvent(owner) : owner.getName());
		String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : getNameForEvent(victim));

		return new Mine(x, y, power, ownerName, victimName, isActive(), mineId);
	}

	private String getNameForEvent(RobotPeer otherRobot) {
		if (battleRules.getHideEnemyNames() && !owner.isTeamMate(otherRobot)) {
			return otherRobot.getAnnonymousName();
		}
		return otherRobot.getName();
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getHeading() {
		return 0;
	}

	@Override
	public double getPower() {
		return power;
	}

	public boolean isActive() {
		return state.isActive();
	}
	
	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}
	
	public int getMineId(){
		return mineId;
	}

	public int getColor(){
		return (int)((power /NavalRules.MIN_MINE_POWER) * 0x00FFFFFF) + 0xFF000000;
	}
	
	public MineState getState() {
		return state;
	}
	
	public int getFrame(){
		return frame;
	}
	
	public ShipPeer getVictim(){
		return victim;
	}
	
	public ShipPeer getOwner(){
		return owner;
	}
	
	public void setPower(double newPower) {
		power = newPower;
	}

	public void setVictim(ShipPeer newVictim) {
		victim = newVictim;
	}

	public void setX(double newX) {
		x = newX;
	}

	public void setY(double newY) {
		y =  newY;
	}

	public void setState(MineState newState) {
		state = newState;
	}
	
	/**
	 * @returns the bounding box of the mine in the form of a BoundingRectangle.
	 */
	public BoundingRectangle getBoundingRectangle(){
		if(boundingBox.isEmpty()){
			double scale = 2 * sqrt(10 * getPower());
			double length = scale;
			boundingBox = new BoundingRectangle((int)(x - length/2), (int)(y - length/2), length, length);

		}
		return boundingBox;
	}
	
	/**
	 * Updates the mine based on the ships and mines currently in the battlefield
	 * If the mine is still active, it checks for ship collision and mine collision
	 * @param ships A list of robotpeers currently in the battlefield.
	 * @param mines A list of minepeers currently in the battlefield.
	 */
	public void update(List<RobotPeer> ships, List<MinePeer> mines) {
		if (isActive()) {
			checkShipCollision(ships);
			if (mines != null) {
				checkMineCollision(mines);
			}
		}
		else{
			//THOMA_NOTE: Not too sure how frames are supposed to work. But this seems to work for me. D:
			frame++;
		}
		updateMineState();
		owner.addMineStatus(createStatus());
	}
	
	/**
	 * Checks whether the mine is in collision with any other mines
	 * @param mines A list of MinePeers currently in the battlefield.
	 */
	private void checkMineCollision(List<MinePeer> mines) {
		for (MinePeer mine: mines) {
			if (getBooleanCollisionWithMine(mine)) {

				state = MineState.HIT_MINE;
				frame = 0;

				mine.state = MineState.HIT_MINE;
				mine.frame = 0;

				owner.addEvent(new MineHitMineEvent(createMine(false), mine.createMine(true)));
				mine.owner.addEvent(new MineHitMineEvent(mine.createMine(false), createMine(true)));
				break;
			}
		}
	}
	
	/**
	 * Tests whether your mine has hit another mine
	 * @param mine The mine you're testing for
	 * @return true if your mine is within the same area as the mine you've given, false if your mine hasn't hit anything
	 */
	private boolean getBooleanCollisionWithMine(MinePeer mine){
		//Make sure the mine isn't 0, isn't you and make sure that it's an active mine
		//Also check whether the mine intersects or is within your own mine
		return (mine != null && mine != this && mine.isActive() 
				&& (boundingBox.intersects(mine.getBoundingRectangle()) || boundingBox.contains(mine.getBoundingRectangle())));

	}

	protected void updateMineState() {
		switch (state) {
		case PLACED:
			// Note that the bullet must be in the FIRED state before it goes to the MOVING state
			if (frame > 0) {
				state = MineState.FLOATING;
			}
			break;
		case FLOATING:
		case HIT_MINE:
		case HIT_VICTIM:
		case EXPLODED:
			// Note that the bullet explosion must be ended before it goes into the INACTIVE state
			if (frame >= getExplosionLength()) {
				state = MineState.INACTIVE;
			}
			break;

		default:
		}
	}
	
	/**
	 * Checks whether this mine has hit a ship based on a list of robots in the field
	 * @param ships the ships currently in the battlefield.
	 */
	private void checkShipCollision(List<RobotPeer> ships) {
		ShipPeer otherShip = null;
		for (RobotPeer otherRobot : ships) {
			if (!(otherRobot == null || otherRobot.isDead()) && otherRobot instanceof ShipPeer) {
				otherShip = (ShipPeer)otherRobot;
				if(Collision.collide((ITransformable)otherShip, (Rectangle2D)getBoundingRectangle())){
					state = MineState.HIT_VICTIM;
					frame = 0;
					victim = (ShipPeer)otherShip;
					
					double damage = NavalRules.getMineDamage(power);
	
					double score = damage;
					if (score > otherShip.getEnergy()) {
						score = otherShip.getEnergy();
					}
					otherShip.updateEnergy(-damage);
					
					
					((ShipStatistics)owner.getRobotStatistics()).scoreMineDamage(otherRobot.getName(), score);

					if (otherShip.getEnergy() <= 0 && otherShip.isAlive()) {
						otherShip.kill();
						double bonus = ((ShipStatistics)owner.getRobotStatistics()).scoreMineKill(otherShip.getName());
						if (bonus > 0) {
							owner.println(
									"SYSTEM: Bonus for killing "
											+ (owner.getNameForEvent(otherShip) + ": " + (int) (bonus + .5)));
						}
						
					}
					if(otherShip != owner)
						owner.updateEnergy(NavalRules.getMineHitBonus(power));
					
					otherShip.addEvent(
							new HitByMineEvent(createMine(true))); 
					owner.addEvent(
							new MineHitEvent(owner.getNameForEvent(victim), victim.getEnergy(), createMine(false))); // Bugfix #366
	
					break;
				}
			}
		}
	}	
}
