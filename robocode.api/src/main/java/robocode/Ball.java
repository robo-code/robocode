package robocode;

import net.sf.robocode.security.IHiddenBallHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Represents a soccer ball.
 *
 * @author Alfonso Dou Oblanca (original)
 */

public class Ball implements Serializable {
    private static final long serialVersionUID = 1L;

    private double headingRadians;
    private double x;
    private double y;
    private double velocity;

    /**
     * Called by the game to create a new {@code Ball} object
     *
     * @param headingRadians  the heading of the ball, in radians.
     * @param x   the starting X position of the ball.
     * @param y   the starting Y position of the ball.
     * @param velocity  the velocity of the ball.
     */
    public Ball(double headingRadians, double x, double y, double velocity) {
        this.headingRadians = headingRadians;
        this.x = x;
        this.y = y;
        this.velocity = velocity;
    }

    /**
     * Returns the direction the ball is/was heading, in degrees
     * (0 <= getHeading() < 360).
     *
     * @return the direction the ball is/was heading, in degrees
     */
    public double getHeading() {
        return Math.toDegrees(headingRadians);
    }

    /**
     * Returns the direction the ball is/was heading, in radians
     * (0 <= getHeadingRadians() < 2 * Math.PI).
     *
     * @return the direction the ball is/was heading, in radians
     */
    public double getHeadingRadians() {
        return headingRadians;
    }

    /**
     * Returns the velocity of the ball.
     *
     * @return the velocity of the ball
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * Returns the X position of the ball.
     *
     * @return the X position of the ball
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y position of the ball.
     *
     * @return the Y position of the ball
     */
    public double getY() {
        return y;
    }

    /**
     * Updates this bullet based on the specified bullet status.
     *
     * @param headingRadians the new heading of the ball, in radians.
     * @param x the new X position of the ball.
     * @param y the new Y position of the ball.
     * @param velocity  the new velocity of the ball.
     */
    // this method is invisible on RobotAPI
    private void update(double headingRadians, double x, double y, double velocity) {
        this.headingRadians = headingRadians;
        this.x = x;
        this.y = y;
        this.velocity = velocity;
    }

    /**
     * Creates a hidden ball helper for accessing hidden methods on this object.
     *
     * @return a hidden ball helper.
     */
    // this method is invisible on RobotAPI
    static IHiddenBallHelper createHiddenHelper() {
        return new Ball.HiddenBallHelper();
    }

    /**
     * Creates a hidden ball helper for accessing hidden methods on this object.
     *
     * @return a hidden ball helper.
     */
    // this class is invisible on RobotAPI
    static ISerializableHelper createHiddenSerializer() {
        return new Ball.HiddenBallHelper();
    }

    // this class is invisible on RobotAPI
    private static class HiddenBallHelper implements IHiddenBallHelper, ISerializableHelper {

        public void update(Ball ball, double headingRadians, double x, double y, double velocity) {
            ball.update(headingRadians, x, y, velocity);
        }

        public int sizeOf(RbSerializer serializer, Object object) {
            Ball obj = (Ball) object;

            return RbSerializer.SIZEOF_TYPEINFO + 4 * RbSerializer.SIZEOF_DOUBLE;
        }

        public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
            Ball obj = (Ball) object;

            serializer.serialize(buffer, obj.headingRadians);
            serializer.serialize(buffer, obj.x);
            serializer.serialize(buffer, obj.y);
            serializer.serialize(buffer, obj.velocity);
        }

        public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
            double headingRadians = buffer.getDouble();
            double x = buffer.getDouble();
            double y = buffer.getDouble();
            double velocity = buffer.getDouble();

            return new Ball(headingRadians, x, y, velocity);
        }
    }
}
