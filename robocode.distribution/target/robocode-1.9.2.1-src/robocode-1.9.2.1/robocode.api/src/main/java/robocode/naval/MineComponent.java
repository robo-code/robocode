package robocode.naval;

import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenComponentHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * The class that represents the component on the Ship that drops a mine.
 * 
 * @author Thales B.V. / Thomas Hakkers
 */
public class MineComponent extends ComponentBase{
	private static final long serialVersionUID = -3196489261971513847L;

	/**
	 * The current time is takes to recharge.
	 */
	private double mineRecharge = 0;
	
	
	/**
	 * The constructor for the {@code MineComponent}
	 * @param x	The x value relative to the pivot of the ship
	 * @param y The y value relative to the pivot of the ship
	 * @param type The type of the component, see {@link ComponentType}
	 */
	public MineComponent(double x, double y, ComponentType type) {
		this(new Point2D.Double(x,y), type);
	}
	
	/**
	 * The constructor for the {@code MineComponent}
	 * @param pivot The x and y value of the component relative to the pivot of the ship.
	 * @param type The type of the component, see {@link ComponentType}
	 */
	public MineComponent(Point2D pivot, ComponentType type) {
		super(pivot, type, 0.0d);
	}
	
	/**
	 * A constructor that creates a default {@code MineComponent}
	 */
	public MineComponent() {
		this(new Point2D.Double(0,0), ComponentType.UNDEFINED);
	}

	/**
	 * Sets the recharge heat for the MineComponent.
	 * @see {@link MineComponent#rechargeMine(double coolingRate) rechargeMine()}
	 * @param heat
	 */
	public void setMineRecharge(double heat){
		mineRecharge = heat;
	}
	
	/**
	 * Basically the same as coolDown for weapons. It's just that Mines tend to explode once they get hot
	 * @return The current "mine recharge". If it's 0, you can place a mine.
	 */
	public double getMineRecharge(){
		return mineRecharge;
	}
	
	/**
	 * Recharges your MineComponent.
	 * Once the mineRecharge is back to 0, a mine can be placed again.
	 * @param coolingRate The rate at which the mine is being recharged.
	 */
	public void rechargeMine(double coolingRate) {
		mineRecharge -= coolingRate;
		if (mineRecharge < 0.0d) {
			mineRecharge = 0.0d;
		}
	}

	
	@Deprecated
	@Override
	public double turnRadians(double turnRemaining) {
		return 0;
	}
	
	public byte getSerializeType(){
		return RbSerializer.MineComponent_TYPE;
	}
	
	// ======================================================================
	// Serialize for this component.
	// ======================================================================

	/**
	 * Creates a hidden mine component helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden mine component helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenComponentHelper createHiddenHelper() {
		return new HiddenMineComponentHelper();
	}

	/**
	 * Creates a hidden weapon component helper for accessing hidden methods on this object.
	 *
	 * @return a hidden weapon component helper.
	 */
	// this class is invisible on RobotAPI
	static ISerializableHelper createHiddenSerializer() {
		return new HiddenMineComponentHelper();
	}
	
	// this class is invisible on RobotAPI
	private static class HiddenMineComponentHelper extends HiddenComponentHelper {
		
		/**
		 * {@inheritDoc}
		 */
		protected int getSize(RbSerializer serializer, Object object) {
			int size = 0;
			size += 3 * RbSerializer.SIZEOF_DOUBLE;
			size += 1 * RbSerializer.SIZEOF_BOOL;
			return size;
		}

		/**
		 * {@inheritDoc}
		 */
		protected void serializeSub(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MineComponent obj = (MineComponent)object;
			serializer.serialize(buffer, obj.mineRecharge);
		}

		/**
		 * {@inheritDoc}
		 */
		protected void deserializeSub(RbSerializer serializer, ByteBuffer buffer, Object component) {
			MineComponent weapon = (MineComponent)component;
			
			// De-serialize the fields.
			weapon.mineRecharge = serializer.deserializeDouble(buffer);
		}

		/**
		 * {@inheritDoc}
		 */
		protected ComponentBase getObject() {
			return new MineComponent();
		}

		@Override
		public void updateSub(ComponentBase component) {			
		}
	}

}
