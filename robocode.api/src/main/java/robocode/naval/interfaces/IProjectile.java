package robocode.naval.interfaces;

/**
 * An interface for a Projectile. 
 * Thomas: I don't have much time left and I just realised that this class is completely unnecessary.
 * ... Straight up deleting this class will cause too many problems for me to fix at the moment.
 * The game will still function great with this class included. 
 * It's just that I prefer to use the least code possible to complete this assignment.
 * @author Thales B.V. / Jiri Waning
 *
 */
public interface IProjectile extends ICoordinate {
	double getX();
	double getY();
	double getHeading();
	double getPower();
}
