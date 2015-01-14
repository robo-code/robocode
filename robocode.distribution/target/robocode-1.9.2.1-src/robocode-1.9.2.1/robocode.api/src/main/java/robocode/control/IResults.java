package robocode.control;

/**
 * An interface for the classes that determine the results of a robot/ship
 * Made to avoid multiple inheritance problems. 
 * @author Thales B.V./ Thomas Hakkers
 *
 */
public interface IResults {
	/**
	 * Returns the robot these results are meant for.
	 *
	 * @return the robot these results are meant for.
	 */
	RobotSpecification getRobot();

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}
