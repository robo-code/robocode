package robocode.naval;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenComponentHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.ITransformable;
import robocode.util.Utils;

/**
 * A class that resembles a weapon component for on a ship.
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @since 1.9.2.2
 * @version 1.1
 */
public class WeaponComponent extends ComponentBase {
	private static final long serialVersionUID = 4L;
	private Color bulletColor = Color.white;

	/**
	 * The half and full blind arc angle for any weapon. These
	 * can be used to determine if a weapon is currently at the
	 * minimum/maximum allowed angle. (160&deg; &amp; 80&deg;)
	 */
	public static final double BLIND_SPOT_ANGLE = 160.0d,
			HALF_BLIND_SPOT_ANGLE = (BLIND_SPOT_ANGLE / 2);
	
	/**
	 * The angular opening, in radians, of the weapons.
	 */
	private static final double
			ANGULAR_OPENING = Math.toRadians(BLIND_SPOT_ANGLE), // 160 degrees
			HALF_ANGULAR_OPENING = (ANGULAR_OPENING / 2);
	
	/**
	 * This is the blind arch for the weapon.
	 */
	private BlindSpot blindSpot;
	
	/**
	 * The current (over)heat of the gun.
	 */
	private double gunHeat;
	
	/**
	 * The fire power of the weapon.
	 */
	private double power;
		
	/** Preserved for use during the serialization! **/
	private WeaponComponent() {
		this(0.0d, 0.0d, ComponentType.UNDEFINED);
	}
	
	/**
	 * Creates a new weapon component for the ship.
	 * <p/>
	 * <i>When the {@code arcSize} is zero there will be no blind arc.</i>
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 * @param type The type of weapon component.
	 */
	public WeaponComponent(double x, double y, ComponentType type) {
		this(x, y, type, 0.0d, 0.0d);
	}
	
	/**
	 * Creates a new weapon component for the ship.
	 * <p/>
	 * <i>When the {@code arcSize} is zero there will be no blind arc.</i>
	 * @param x The X-coordinate of the pivot point of the component.
	 * @param y The Y-coordinate of the pivot point of the component.
	 * @param type The type of weapon component.
	 * @param start The starting angle (in degrees) of the blind arc.
	 * @param extent The size of the blind arc as an angle(in degrees) that
	 * 				 the weapon can not cover. (Negative means to the left
	 *               of the starting angle/positive means to the right.)
	 */
	public WeaponComponent(double x, double y, ComponentType type, double start, double extent) {
		this(new Point2D.Double(x, y), type, start, extent);
	}
	
	/**
	 * Creates a new weapon component for the ship.
	 * <p/>
	 * <i>When the {@code arcSize} is zero there will be no blind arc.</i>
	 * @param pivot The pivot point of the component.
	 * @param type The type of weapon component.
	 * @param start The starting angle (in degrees) of the blind arc.
	 * @param extent The size of the blind arc as an angle(in degrees) that
	 * 				 the weapon can not cover. (Negative means to the left
	 *               of the starting angle/positive means to the right.)
	 */
	public WeaponComponent(Point2D pivot, ComponentType type, double start, double extent) {
		super(pivot, type, 0.0d);
		this.power = 1.0d;
		this.gunHeat = 3.0d; // Robocode default
		if(Utils.isNear(extent, 0)){
			setStandardBlindSpot();
		}
		else{
			setBlindSpot(Math.toRadians(start), Math.toRadians(extent));
		}
		setDefaultHeading();
	}
	
	
	
	
	
	// ======================================================================
	//  Weapon's specific methods.
	// ======================================================================
	
	/**
	 * Set the fire power of the weapon.
	 * @param energy The energy the robot has left.
	 * @param power The fire power of the weapon.
	 */
	public void setFirePower(double energy, double power) {
		this.power = min(energy, min(max(power, NavalRules.MIN_BULLET_POWER), NavalRules.MAX_BULLET_POWER));
	}
	
	/**
	 * Get the fire power of the weapon.
	 * @return The fire power of the weapon.
	 */
	public double getFirePower() {
		return power;
	}
	
	public void updateGunHeat(double gunHeat){
		this.gunHeat += gunHeat;
	}
	
	/**
	 * Fire the weapon.
	 * @param peer The owner of the weapon.
	 */
	@Deprecated
	public void fire(ITransformable peer) {
		gunHeat += NavalRules.getGunHeat(power);
	}
	
	/**
	 * Get the gun heat.
	 * @return The heat value of this weapon.
	 */
	public double getGunHeat() {
		return gunHeat;
	}
	
	/**
	 * Cool down the weapon. This is required to fire once more.
	 * @param coolingRate The rate at which to cool down the weapon.
	 */
	public void coolDown(double coolingRate) {
		gunHeat -= coolingRate;
		if (gunHeat < 0.0d) {
			gunHeat = 0.0d;
		}
	}
	
	/**
	 * Get the angle towards which the bullet gets fired.
	 * @param peer The ship that fired the bullet.
	 * @return The angle towards which the bullet gets fired.
	 */
	public double getFireAngle(ITransformable peer) {
			return PI - getAngle() - peer.getBodyHeading();
	}
	
	public BlindSpot getCopyOfBlindSpot(){
		return new BlindSpot(blindSpot);
	}
	
	public void setBulletColor(Color color){
		bulletColor = color;
	}
	
	public Color getBulletColor(){
		return bulletColor;
	}
	
	// ======================================================================
	//  Blind Arc & Rendering
	// ======================================================================
	
	
	/**
	 * Sets the default heading of the weapon based on its type.
	 */
	private void setDefaultHeading() {
		super.setAngle(getTypeOffset());
	}
	
	/**
	 * Get the angular offset in radians based upon the weapon type.
	 * THOMA_NOTE: Basically makes sure that back cannons are facing south and that front cannons are facing north. (Returns PI/2 for front cannon and 3PI/2 for back cannon)
	 * @return The angular offset in radians based upon the weapon type.
	 */
	public double getTypeOffset() {
		int bitMask = ComponentType.WEAPON_BITMASK.toInt();
		int type = (getType().toInt() - bitMask);
		double angle = 0.0d;
		
		for (int i = (ComponentType.WEAPON_STARBOARD.toInt() - bitMask); type >= i; i++) {
			angle += (PI / 2);
		}
		
		return angle;
	}
	
	/**
	 * Sets the blind arch based upon the type of the weapon.
	 * <p/>
	 * The following types are supported:
	 * <ul>
	 *     <li>{@link robocode.naval.ComponentType#WEAPON_PROW WEAPON_PROW}</li>
	 *     <li>{@link robocode.naval.ComponentType#WEAPON_STARBOARD WEAPON_STARBOARD}</li>
	 *     <li>{@link robocode.naval.ComponentType#WEAPON_STERN WEAPON_STERN}</li>
	 *     <li>{@link robocode.naval.ComponentType#WEAPON_PORT WEAPON_PORT}</li>
	 * </ul>
	 * 
	 */
	private void setStandardBlindSpot() {
		double start = 0;
		double extent = 0;
		if (getType().fromSeries(ComponentType.WEAPON_BITMASK)) {
			double offset = getTypeOffset();
			start = offset - HALF_ANGULAR_OPENING;
			extent = ANGULAR_OPENING;
		}		
		this.blindSpot = new BlindSpot(start, extent);
	}
	
	/**
	 * Sets the BlindSpot for the WeaponComponent
	 * @param start The starting angle of the BlindSpot
	 * @param extent The extent of the BlindSpot
	 */
	private void setBlindSpot(double start, double extent){
		blindSpot = new BlindSpot(start, extent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAngle(double turnRemaining) {
		if (!blindSpot.inBlindSpot(angle)){
			angle += turnRemaining;
		}		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAngleDegrees(double angle) {
		//checks performed in setAngle();
		if(angle > 360){
			angle %= 360;
		}
	}
	
	public boolean getAtBlindSpot(){
		return Utils.isNear(angle, blindSpot.getFarRight()) || Utils.isNear(angle, blindSpot.getFarLeft()) ;
	}
	
	@Override
	public double turnRadians(double turnRemaining){
		double turnRate = NavalRules.GUN_TURN_RATE_RADIANS;
		double trueTurnRemaining = turnRemaining;
		
		double farRight = blindSpot.getFarRight();
		double farLeft = blindSpot.getFarLeft();
		
		if(turnRemaining == Double.POSITIVE_INFINITY){
			trueTurnRemaining = farRight - Utils.normalAbsoluteAngle(angle);
		}
		else if(turnRemaining == Double.NEGATIVE_INFINITY){
			trueTurnRemaining = farLeft - Utils.normalAbsoluteAngle(angle);
		}
		
		//Turn Right
		if (trueTurnRemaining > 0) {
			if (trueTurnRemaining < turnRate) {
				if(blindSpot.inBlindSpot(angle + trueTurnRemaining)){
//					atBlindSpot = true;
					double oldAngle = angle;
					angle = farRight;
					return oldAngle + trueTurnRemaining - farRight;			//Go to the edge of the BlindSpot, but tell them there's still some turning to be done.
				}
				else{
//					atBlindSpot = false;
					angle += trueTurnRemaining; 
					return 0;
				}
			} else {
				if(blindSpot.inBlindSpot(angle + turnRate)){
					double oldAngle = angle;
					angle = farRight;
//					atBlindSpot = true;
					return oldAngle + trueTurnRemaining - farRight;

				}
				else{
//					atBlindSpot = false;
					angle += turnRate; 
					return trueTurnRemaining - turnRate;
				}
			}
		} 
		
		//Turn Left
		else if (trueTurnRemaining < 0) {
			if (turnRemaining > -turnRate) {
				if(blindSpot.inBlindSpot(angle + trueTurnRemaining)){
					double oldAngle = angle;
					angle = farLeft;
					return (oldAngle + trueTurnRemaining - farLeft)%(2*Math.PI);
				}
				else{
					angle += trueTurnRemaining; 
					return 0;
				}
			} else {
				if(blindSpot.inBlindSpot(angle - turnRate)){
					double oldAngle = angle;
					angle = farLeft;
					return (oldAngle + trueTurnRemaining - farLeft)%(2*Math.PI);
				}
				else{
					angle += -turnRate; 
					return trueTurnRemaining + turnRate;
				}
			}
		} else{
			return turnRemaining;
		}
		
	}
	
	public byte getSerializeType(){
		return RbSerializer.WeaponComponent_TYPE;
	}
	
	
	
	// ======================================================================
	// Serialize for this component.
	// ======================================================================

	/**
	 * Creates a hidden weapon component helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden weapon component helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenComponentHelper createHiddenHelper() {
		return new HiddenWeaponHelper();
	}

	/**
	 * Creates a hidden weapon component helper for accessing hidden methods on this object.
	 *
	 * @return a hidden weapon component helper.
	 */
	// this class is invisible on RobotAPI
	static ISerializableHelper createHiddenSerializer() {
		return new HiddenWeaponHelper();
	}
	
	// this class is invisible on RobotAPI
	private static class HiddenWeaponHelper extends HiddenComponentHelper {

		private ISerializableHelper blindSpotHelper;
		
		/**
		 * {@inheritDoc}
		 */
		public void updateSub(ComponentBase component) {
			blindSpotHelper = BlindSpot.createHiddenSerializer();
		}
		
		/**
		 * {@inheritDoc}
		 */
		protected int getSize(RbSerializer serializer, Object object) {
			int size = 0;
			size += blindSpotHelper.sizeOf(serializer, ((WeaponComponent)(object)).blindSpot);
			size += 4 * RbSerializer.SIZEOF_DOUBLE;
			size += 1 * RbSerializer.SIZEOF_BOOL;
			return size;
		}

		/**
		 * {@inheritDoc}
		 */
		protected void serializeSub(RbSerializer serializer, ByteBuffer buffer, Object object) {
			WeaponComponent obj = (WeaponComponent)object;
			
			// Serialize all the fields.
			blindSpotHelper.serialize(serializer, buffer, object);
			serializer.serialize(buffer, obj.gunHeat);
			serializer.serialize(buffer, obj.power);
		}

		/**
		 * {@inheritDoc}
		 */
		protected void deserializeSub(RbSerializer serializer, ByteBuffer buffer, Object component) {
			WeaponComponent weapon = (WeaponComponent)component;
			
			// De-serialize the fields.
			weapon.blindSpot = (BlindSpot) blindSpotHelper.deserialize(serializer, buffer);
			weapon.gunHeat = serializer.deserializeDouble(buffer);
			weapon.power = serializer.deserializeDouble(buffer);
		}

		/**
		 * {@inheritDoc}
		 */
		protected ComponentBase getObject() {
			return new WeaponComponent();
		}
	}
}
