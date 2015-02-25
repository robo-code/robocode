package net.sf.robocode.test.naval;

import org.junit.Test;

import static org.junit.Assert.*;
import robocode.naval.NavalRules;
import robocode.naval.RadarComponent;

public class RadarComponentTest {
	private RadarComponent radarComponent;
	
	public RadarComponentTest(){
		radarComponent = new RadarComponent(0,0);
	}
	
	public double turnRadiansTest(double turnRemaining, String testName, boolean log){
		if(log)
			System.out.print(testName + ":  Angle Before: " + radarComponent.getAngle());
		double temp = radarComponent.turnRadians(turnRemaining);
		if(log)
			System.out.println(" ---> Angle After: " + radarComponent.getAngle() + " After trying to turn: " + turnRemaining);
		assertTrue(radarComponent.getAngle() <= NavalRules.RADAR_TURN_RATE_RADIANS);
		return temp;
	}
	
	//Testing whether the radar always turns less than NavalRules.RADAR_TURN_RATE_RADIANS
	@Test public void test1a(){turnRadiansTest(50, "test1a", true);}
	@Test public void test1b(){turnRadiansTest(-60, "test1b", true);}
	@Test public void test1c(){turnRadiansTest(634.1, "test1c", true);}
	@Test public void test1d(){turnRadiansTest(-93.4, "test1d", true);}
	
	@Test public void test2a(){assertTrue(turnRadiansTest(Math.toRadians(NavalRules.RADAR_TURN_RATE - 1), "test2a", true) == 0.0);}	
	@Test public void test2b(){assertTrue(turnRadiansTest(Math.toRadians(-NavalRules.RADAR_TURN_RATE), "test2b", true) == 0.0);}	
	@Test public void test2c(){assertTrue(turnRadiansTest(Math.toRadians(-NavalRules.RADAR_TURN_RATE-0.1), "test2c", true) != 0.0);}
	@Test public void test2d(){assertTrue(turnRadiansTest(Math.toRadians(0.1), "test2d", true) == 0.0);}

}
