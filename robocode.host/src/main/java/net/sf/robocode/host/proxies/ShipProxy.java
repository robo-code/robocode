package net.sf.robocode.host.proxies;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import robocode.naval.BlindSpot;
import robocode.naval.ComponentManager;
import robocode.naval.MineComponent;
import robocode.naval.NavalRules;
import robocode.naval.RadarComponent;
import robocode.naval.WeaponComponent;
import robocode.naval.interfaces.IComponent;
import robocode.robotinterfaces.peer.IBasicShipPeer;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.peer.BulletCommand;
import net.sf.robocode.peer.BulletCommandShip;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.peer.MineCommand;
import net.sf.robocode.peer.MineStatus;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.security.HiddenAccess;
import robocode.Bullet;
import robocode.Mine;
import robocode.ShipStatus;

/**
 * Handles the game mechanics of a ship. (Delegates the commands of a Ship.)
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @since 1.9.2.0 
 * @version 0.2
 */
public class ShipProxy extends AdvancedRobotProxy implements IBasicShipPeer{
	protected int nextMineId = 1;
	protected final Map<Integer, Mine> mines = new ConcurrentHashMap<Integer, Mine>();

	
	public ShipProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(specification, hostManager, peer, statics);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotate(int index, double angle) {
		setCall();		
		commands.setComponentsTurnRemaining(index, Math.toRadians(angle));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bullet fireComponent(int index) {
		Bullet bullet = setFireCannonImpl(index);
		execute();
		return bullet;
	}
	/**
	 * {@inheritDoc}
	 */
	public Bullet setFireComponent(int index){
		setCall();
		return setFireCannonImpl(index);
	}
	
	/**
	 * Basically puts a few BulletCommandShips in the ExecCommands object.
	 * Updated to now use bulletIds.
	 */
	protected Bullet setFireCannonImpl(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		WeaponComponent weaponComponent = (WeaponComponent)manager.getComponent(index);
		if(weaponComponent == null){
			return null;
		}
		if (Double.isNaN(weaponComponent.getFirePower())) {
			println("SYSTEM: You cannot call fire(NaN)");
			return null;
		}
		if (weaponComponent.getGunHeat() > 0 || getEnergyImpl() == 0) {
			return null;
		}
		
		Bullet bullet;
		BulletCommand wrapper;
		nextBulletId++;
		double offsetY = weaponComponent.getPivot().getY();
		bullet = new Bullet(PI - weaponComponent.getAngle() - getBodyHeading(),
				getX() + (offsetY * Math.cos(getBodyHeading()+ Math.PI/2)),
				getBattleFieldHeight() - (getY() + (offsetY * Math.sin(getBodyHeading() + Math.PI/2))), 
				weaponComponent.getFirePower(), statics.getName(), null, true, nextBulletId);
		wrapper = new BulletCommandShip(weaponComponent.getFirePower(), false, 0, nextBulletId, index);
		
		commands.getBullets().add(wrapper);

		bullets.put(nextBulletId, bullet);

		return bullet;	
	}
	/**
	 * {@inheritDoc}
	 */
	public double getAngleRemainingComponent(int index){
		return commands.getComponentsTurnRemaining(index);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAdjustComponentForShipTurn(int index, boolean independent){
		setCall();
		commands.setAdjustComponentForShip(index, independent);
	}
	
	protected final Mine setMineImpl(double power, int index) {
		if (Double.isNaN(power)) {
			println("SYSTEM: You cannot call mine(NaN)");
			return null;
		}
		ComponentManager manager = ((ShipStatus)status).getComponents();
		MineComponent mineComponent = (MineComponent)manager.getComponent(index);
		
		if(mineComponent == null){
			return null;
		}
		
		if (mineComponent.getMineRecharge() > 0 || getEnergyImpl() == 0) {
			return null;
		}
		//Make sure the power is within the boundries
		power = min(getEnergyImpl(), min(max(power, NavalRules.MIN_MINE_POWER), NavalRules.MAX_MINE_POWER));

		Mine mine;
		MineCommand wrapper;

		nextMineId++;

		double offsetY = mineComponent.getPivot().getY();
		mine = new Mine(getX() + (offsetY * Math.cos(getBodyHeading()+ Math.PI/2)),
				getBattleFieldHeight() - (getY() + (offsetY * Math.sin(getBodyHeading() + Math.PI/2))),
				power, statics.getName(), null, true, nextMineId);
		wrapper = new MineCommand(power, nextMineId);
		
		commands.getMines().add(wrapper);

		mines.put(nextMineId, mine);

		return mine;
	}

	/**
	 * {@inheritDoc}
	 */
	public Mine setMine(double power, int index) {
		setCall();
		return setMineImpl(power, index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Mine mine(double power, int index) {
		Mine mine = setMine(power, index);
		execute();
		return mine;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentColor(int index, Color color) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		manager.getComponent(index).setColor(color);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScanColor(int index, Color color) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof RadarComponent){
			((RadarComponent)component).setScanColor(color);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBulletColor(int index, Color color) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			((WeaponComponent)component).setBulletColor(color);
		}	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFirePowerComponent(int index, double firepower) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			((WeaponComponent)component).setFirePower(getEnergy(), firepower);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFirePowerComponent(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			return ((WeaponComponent)component).getFirePower();
		}
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getGunHeatComponent(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			((WeaponComponent)component).getGunHeat();
		}
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAngleComponentRadians(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		return component.getAngle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BlindSpot getBlindSpotWeapon(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			return ((WeaponComponent)component).getCopyOfBlindSpot();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAtBlindSpot(int index) {
		ComponentManager manager = ((ShipStatus)status).getComponents();
		IComponent component = manager.getComponent(index);
		if(component instanceof WeaponComponent){
			return ((WeaponComponent)component).getAtBlindSpot();
		}
		return false;
	}
	
	
	@Override
	protected final void executeImpl() {
		super.executeImpl();
		if (execResults.getMineUpdates() != null) {
			for (MineStatus mineStatus : execResults.getMineUpdates()) {
				final Mine mine = mines.get(mineStatus.mineId);
		
				if (mine != null) {
					//x and y don't need to be update since a mine doesn't move
					HiddenAccess.update(mine, mineStatus.victimName,
							mineStatus.isActive);
					if (!mineStatus.isActive) {
						mines.remove(mineStatus.mineId);
					}
				}
			}
		}
	}
}
