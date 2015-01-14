package net.sf.robocode.test.naval;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static robocode.util.Utils.isNear;

import org.junit.Test;

import robocode.naval.BlindSpot;

/**
 * Tests whether the blindspot works properly.
 * @author Thales B.V. / Jiri Waning  (Documented by Thomas)
 */
public class BlindSpotTest {
	private BlindSpot ba;
	private double ret = 0.0d;
	private boolean retVal = false;
	
	
	public BlindSpotTest() {
		//Initialize the BlindSpot
		ba = new BlindSpot(toRadians(320.0d), toRadians(90.0d)); // [-40; 50]
	}
	
	
	/**
	 * Tests whether the specified angle is within the BlindSpot
	 * and prints the result.
	 * @param angle The angle you want to test for in degrees.
	 * @return true if the given angle is within the blindspot.
	 */
	private boolean getIBA(double angle) {
		retVal = ba.inBlindSpot(toRadians(angle));
		out.println("i: " + angle + " == " + retVal);
		return retVal;
	}
	
	/**
	 * Uses various variables to test the getIBA() function.
	 */
	@Test public void test_ia() { assertEquals("test_ia", getIBA(100.0d), false); }
	@Test public void test_ib() { assertEquals("test_ib", getIBA(-60.0d), false); }
	@Test public void test_ic() { assertEquals("test_ic", getIBA(49.0d), true); }
	@Test public void test_id() { assertEquals("test_id", getIBA(2.5d), true); }
	
	
	/**
	 * Tests the getBestAngle function and prints the results
	 * This function is now deprecated though.
	 * @param angle The you want your weapon to turn in degrees.
	 * @return The angle where your weapon can go to.
	 */
	@SuppressWarnings("deprecation")
	private double getGBA(double angle) {
		ret = toDegrees(ba.getBestAngle(toRadians(angle)));
		out.println("b: " + angle + " ~~> " + ret);
		return ret;
	}

	/**
	 * Uses various variables to test the getGBA() function.
	 */
	@Test public void test_ba() { assertEquals("test_ba", isNear(getGBA(100.0d), 100.0d), true); }
	@Test public void test_bb() { assertEquals("test_bb", isNear(getGBA(-60.0d), -60.0d), true); }
	@Test public void test_bc() { assertEquals("test_bc", isNear(getGBA(49.0d), 50.0d), true); }
	@Test public void test_bd() { assertEquals("test_bd", isNear(getGBA(2.5d), -40.0d), true); }
	
	
	/**
	 * Tests the isCrossingArch() function and prints the
	 * results. 
	 * @param start	The angle of your weapon, currently, in degrees
	 * @param extent How much you want to turn your weapon in degrees
	 * @return true if the turn will cross the boundries.
	 */
	@SuppressWarnings("deprecation")
	private boolean getICA(double start, double extent) {
		retVal = ba.isCrossingArch(toRadians(start), toRadians(extent));
		out.println("c: retVal == " + retVal);
		return retVal;
	}

	/**
	 * Uses various variables to test the getICA() function.
	 */
	@Test public void test_ca() { assertEquals("test_ca", getICA(0, 90.0d), true); }
	@Test public void test_cb() { assertEquals("test_cb", getICA(0, -90.0d), true); }
	@Test public void test_cc() { assertEquals("test_cc", getICA(170, 20.0d), false); }
	@Test public void test_cd() { assertEquals("test_cd", getICA(50, 90.0d), false); }
	@Test public void test_ce() { assertEquals("test_ce", getICA(45, -80.0d), true); }
	@Test public void test_cf() { assertEquals("test_cf", getICA(55, -100.0d), true); }
	
	/**
	 * Tests the getExtent function and prints its results.
	 * All angles are in Degrees.
	 * @param curAngle The starting angle of the BlindSpot
	 * @param destAngle The end point of your BlindSpot
	 * @return The distance in between these two angles.
	 */
	private double getExt(double curAngle, double destAngle) {
		ret = toDegrees(ba.getExtent(toRadians(curAngle), toRadians(destAngle)));
		out.println("x: ret ~~> " + ret);
		return ret;
	}
	
	@Test public void test_xa() { assertEquals("test_xa", isNear(getExt(0.0d, 0.0d), 0.0d), true); }
	@Test public void test_xb() { assertEquals("test_xb", isNear(getExt(300.0d, 0.0d), 60.0d), true); }
	@Test public void test_xc() { assertEquals("test_xc", isNear(getExt(190.0d, 10.0d), 180.0d), true); }
	@Test public void test_xd() { assertEquals("test_xd", isNear(getExt(180.0d, -10.0d), 170.0d), true); }
	@Test public void test_xe() { assertEquals("test_xe", isNear(getExt(180.0d, 10.0d), -170.0d), true); }

	/**
	 * Returns how the weapon needs to turn.
	 * @param curAngle	Current angle of the weapon in degrees.
	 * @param destAngle	Destination angle of the weapon in degrees.
	 * @return	A double which is positive or negative depending on where it needs to turn to.
	 */
	@SuppressWarnings("deprecation")
	private double getGRD(double curAngle, double destAngle) {
		ret = ba.getRotateDirection(toRadians(curAngle), toRadians(destAngle));
		out.println("r: " + curAngle + " == " + ret + " (-1 left, 0 none, 1 right)");
		return ret;
	}
	
	/**
	 * Uses various variables to test the getGRD() function.
	 */
	@Test public void test_ra() { assertEquals("test_ra", getGRD(52.5d, 50.0d) == -1.0d, true); }
	@Test public void test_rb() { assertEquals("test_rb", getGRD(52.5d, 55.0d) == 1.0d, true); }
	@Test public void test_rc() { assertEquals("test_rc", getGRD(52.5d, 52.5d) == 0.0d, true); }
	@Test public void test_rd() { assertEquals("test_rd", getGRD(52.5d, -20.0d) == 1.0d, true); }
	@Test public void test_re() { assertEquals("test_re", getGRD(275.5d, 20.0d) == -1.0d, true); }
}
