package robocode.naval;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenComponentHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.util.Utils;


/*
 * bobbing - I used this word to describe the effect when
 *           the angle constantly moves over the 180 degrees
 *           and keeps moving the weapon(s) from the left to
 *           the right. (To prevent this I added a part of
 *           ignorance, this will ignore close angles.)
 */

/**
 * A blind spot is used to prevent something from moving to a specified angle.
 * @author Thales B.V. / Jiri Waning
 * @version 0.1
 * @since 1.8.3.0 Alpha 1
 */
public class BlindSpot implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double TWO_PI = (PI * 2);

	/**
	 * Indices for the {@link #getStartEnd(double, double)} method.
	 */
	private static final int IDX_START = 0, IDX_END = 1, IDX_HALF = 2;

	/**
	 * The start and end angle of the blind arch. These angles are absolute angles!
	 */
	private double start, end;

	/**
	 * This is half the angle, in radians, of the blind arch.
	 * @see robocode.naval.WeaponComponent#BLIND_SPOT_ANGLE
	 * @see robocode.naval.WeaponComponent#HALF_BLIND_SPOT_ANGLE
	 */
	private double half;

	private boolean crossZero = false;
	
	/**
	 * Create a new blind spot with empty values. (Serialize)
	 */
	private BlindSpot() {
		this(0.0d, 0.0d);
	}

	/**
	 * Create a new blind spot.
	 * @param start The angle at which to start.
	 * @param extent The angular extent of the blind arch. (Negative means left!)
	 */
	public BlindSpot(double start, double extent) {
		this.start = Utils.normalAbsoluteAngle(start);
		this.end = Utils.normalAbsoluteAngle(this.start + extent);
		this.half = abs(extent)/2;
		if((Utils.normalRelativeAngle(start) < 0 && Utils.normalRelativeAngle(start) + extent > 0)
				||(Utils.normalRelativeAngle(start) > 0 && Utils.normalRelativeAngle(start) + extent < 0)){
			crossZero = true;
		}
	}
	
	/**
	 * Copy constructor for BlindSpot.
	 * (Wasn't even needed really, because there's no setters in this class. But I guess it could be useful in the future)
	 * @param blindSpot The BlindSpot you want to copy
	 */
	public BlindSpot(BlindSpot blindSpot){
		this.start = blindSpot.start;
		this.end = blindSpot.end;
		this.half = blindSpot.half;
		this.crossZero = blindSpot.crossZero;
	}

	/**
	 * Get the start-point and end-point of a fraction of a circle.
	 * @param start The starting angle, in radians, of the circle.
	 * @param extent The size of the fraction.
	 * @return An array containing the absolute starting & ending angle; as well as half the closed blind arch.
	 */
	private double[] getStartEndHalf(double start, double extent) {
		double[] data = new double[3];

		// Force a clockwise blind arc.
		data[IDX_START] = (signum(extent) != -1) ? normalAbsoluteAngle(start) : normalAbsoluteAngle(start + extent);
		data[IDX_END] = (signum(extent) == -1) ? normalAbsoluteAngle(start) : normalAbsoluteAngle(start + extent);

		// We need this multiple times; lets just safe ourselves some time.
		data[IDX_HALF] = (abs(extent) / 2);

		return data;
	}
	
	/**
	 * Returns the start of the BlindSpot in RADIANS
	 * @return the start of the BlindSpot in RADIANS
	 */
	public double getStart(){
		return start;
	}
	
	/**
	 * Returns the end of the BlindSpot in RADIANS
	 * @return the end of the BlindSpot in RADIANS
	 */
	public double getEnd(){
		return end;
	}
	
	/**
	 * This function is mostly used internally to draw the BlindSpots.
	 * Java gets annoyed when you try to draw an arc over the 0-point, unless you make sure the  extend is negative.
	 * @return Whether the Arc cross 0 or not.
	 * @see #BattleView.drawNavalBlindSpots(Graphics2D, IRobotSnapshot) 
	 */
	public boolean getCrossZero(){
		return crossZero;
	}
	
	/**
	 * Returns the far Right point of the BlindSpot. Mostly used by the system.
	 * Returns the furthest you can move to the right (which can either be the end or the start, depending on whether it's crossing zero and whether start is higher than end)
	 * @return the far Right pint of the BlindSpot.
	 */
	public double getFarRight(){
		if(crossZero){
			return end > start ? end : start;
		}
		else{
			return end > start ? start : end;
		}
	}
	
	/**
	 * Returns the far Left point of the BlindSpot. Mostly used by the system.
	 * Returns the furthest you can move to the left (which can either be the end or the start, depending on whether it's crossing zero and whether start is higher than end)
	 * @return the far Left pint of the BlindSpot.
	 */
	public double getFarLeft(){
		if(crossZero){
			return end > start ? start : end;
		}
		else{
			return end > start ? end : start;
		}
	}

	/**
	 * Determines whether or not there is a blind spot set.
	 * @return {@code true} if there is a blind arch; {@code false} otherwise.
	 */
	public boolean hasBlindSpot() {
		return !Utils.isNear(start, end);
	}
	
	/**
	 * Determines if the given angle is within the blind spot.
	 * @param angle The angle, in radians, to determine from if it is inside the blind spot.
	 * @return {@code true} if the angle is within the blind spot; {@code false} otherwise.
	 */
	public boolean inBlindSpot(double angle) {
		if (hasBlindSpot()) {
			angle = normalAbsoluteAngle(angle);
			
			//Fixes a small bug. Function would still return true even if the angle was 0.000000000000004 in the blindSpot
			if(Utils.isNear(angle, start) || Utils.isNear(angle, end)){
				return false;
			}
			
			return crossZero ?
					(angle > start || angle < end) : // spanning the 0 (start: 350; end: 10)
					(angle > start && angle < end);  // otherwise; (start: 10; end 30)
		}

		return false;
	}
	

	/**
	 * Get the extent between two different angles. This is being
	 * measured from the {@code curAngle} to the {@code destAngle}.
	 * @param curAngle The current angle of the component.
	 * @param destAngle The destination angle of the component.
	 * @return The extent between the two angles.
	 */
	public double getExtent(double curAngle, double destAngle) {
		return normalRelativeAngle((normalAbsoluteAngle(destAngle) - normalAbsoluteAngle(curAngle)));
	}
	
	//***	ATTENTION. THE FUNCTIONS UNDERNEATH AREN'T USED. 	***\\
	//These were made by the old developer, but I don't think we need them at all. Feels bad to throw it away though. :( - thoma

	/**
	 * Get the best angle we can use with regard to the blind arch.
	 * <p/>
	 * If it is inside the blind arch, it will return the closest
	 * edge of the blind arch.
	 * <p/>
	 * When the angle is not in the blind arch it will return the angle.
	 * @param angle The angle, in radians, we want to rotate to.
	 * @return The best angle, in radians, to rotate to.
	 *
	 * @see #getRotateDirection(double)
	 */
	@Deprecated
	public double getBestAngle(double angle) { 
		angle = normalAbsoluteAngle(angle);

		// The angle of rotation is inside the blind arc. Determine the best angle to rotate to.
		if (inBlindSpot(angle)) { // <start, end>
			if ((angle >= start && (angle - start) >= half) ||
					(angle <= end && (end - angle) <= half)) {
				return normalRelativeAngle(end);
			} else {
				return normalRelativeAngle(start);
			}
		}
		return normalRelativeAngle(angle);
	}
	

	/**
	 * Determine if it crossed the blind arch or not.
	 * @param angle The starting angle.
	 * @param extent The amount we have to move.
	 * @return {@code true} if it crosses the arch; {@code false} otherwise.
	 */
	@Deprecated
	public boolean isCrossingArch(double angle, double extent) {
		double[] data = getStartEndHalf(angle, extent);
		double start = data[IDX_START];
		double end = data[IDX_END];

		if (start < end) {
			// |start   |this.start   |end
			if (this.start > start && this.start < end) {
				return true;
			}

			// |start   |this.end   |end
			if (this.end > start && this.end < end) {
				return true;
			}
		} else { // start > end
			// |end   |this.start   |start
			if (this.start > end || this.start < start) {
				return true;
			}

			// |end   |this.end   |start
			if (this.end > end || this.end < start) {
				return true;
			}
		}

		if (this.start < this.end)
		{
			// |this.start   |start   |this.end
			if (start > this.start && start < this.end) {
				return true;
			}

			// |this.start   |end   |this.end
			if (end > this.start && end < this.end) {
				return true;
			}
		} else { // this.start > this.end
			// |this.end   |start   |this.start
			if (start > this.start || start < this.end) {
				return true;
			}

			// |this.end   |end   |this.start
			if (end > this.start || end < this.end) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the best direction to rotate to.
	 * @param curAngle The current angle, in radians, of the component.
	 * @param destAngle The angle, in radians, that has to be rotated to.
	 * @return {@code -1} (left), {@code 0} (unknown) or {@code 1} (right)
	 *
	 * @see #getBestAngle(double)
	 */
	@Deprecated
	public double getRotateDirection(double curAngle, double destAngle) {
		final double LEFT = -1.0d, NONE = 0.0d, RIGHT = 1.0d;

		// Return immediately when there is no blind arch.
		if (!hasBlindSpot()) {
			return NONE;
		}

		// Ensure they are [0, TWO_PI>
		curAngle = normalAbsoluteAngle(curAngle);
		destAngle = normalAbsoluteAngle(destAngle);

		if (inBlindSpot(destAngle)) {
			destAngle = getBestAngle(destAngle);
		}
		
		double extent = getExtent(curAngle, destAngle);
		if (isCrossingArch(curAngle, extent)) {
			// Turn around but use the opposite direction!
			//   Due to the blind arch being on the shortest track.
			if (extent > 0) {
				extent -= TWO_PI;
			} else if (extent < 0) {
				extent += TWO_PI;
			}
		}

		return signum(extent) != LEFT ? (signum(extent) != RIGHT ? NONE : RIGHT) : LEFT;
	}

	/**
	 * This gets the only possible angle when going into the blind arch.
	 * It ensures the angle is set on the appropriate side of the blind arch.
	 * When not inside the blind arch it simple returns the input.
	 * @param angle The angle, in radians, we want to rotate to.
	 * @return
	 */
	@Deprecated
	public double getOnlyAngle(double curAngle, double destAngle) {
		if (!hasBlindSpot()) {
			return destAngle;
		}

		double bestAngle = getBestAngle(destAngle);
		double direction = getRotateDirection(curAngle, destAngle);

		if (direction != 0.0d) { // direction != NONE
			return (abs(bestAngle) + abs(curAngle)) * direction;
		}

		return bestAngle;
	}



	// ======================================================================
	// Serialization
	// ======================================================================

	/**
	 * Creates a hidden blind arch helper for accessing hidden methods on this object.
	 * @return A hidden blind arch helper.
	 */
	static IHiddenComponentHelper createHiddenHelper() { // This method is invisible on RobotAPI.
		return new HiddenBlindSpotHelper();
	}

	/**
	 * Creates a hidden blind arch helper for accessing hidden methods on this object.
	 * @return A hidden blind arch helper.
	 */
	static ISerializableHelper createHiddenSerializer() { // This class is invisible on RobotAPI.
		return new HiddenBlindSpotHelper();
	}

	// This class is invisible on RobotAPI.
	private static class HiddenBlindSpotHelper implements ISerializableHelper, IHiddenComponentHelper {
		@Override
		public void update(ComponentBase component) { }

		@Override
		public int sizeOf(RbSerializer serializer, Object object) {
			return 2 * RbSerializer.SIZEOF_DOUBLE;
		}

		@Override
		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BlindSpot obj = (BlindSpot) object;
			serializer.serialize(buffer, obj.start);
			serializer.serialize(buffer, obj.end);
		}

		@Override
		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			BlindSpot obj = new BlindSpot();
			obj.start = serializer.deserializeDouble(buffer);
			obj.end = serializer.deserializeDouble(buffer);
			return obj;
		}
	}
}
