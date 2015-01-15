package net.sf.robocode.test.naval;

import static org.junit.Assert.*;

import org.junit.Test;

import robocode.naval.ComponentType;
import robocode.naval.NavalRules;
import robocode.naval.WeaponComponent;
import robocode.util.Utils;

public class WeaponComponentTest {
	private WeaponComponent weaponComponent;
	private double start = 90;
	private double extent = 180;
	private double end = start + extent;
	
	public WeaponComponentTest(){
		weaponComponent = new WeaponComponent(0, 0, ComponentType.WEAPON_PROW, start, extent);
	}
	
	public double testTurnRadians(double turnRemaining, boolean log){
		double output = weaponComponent.turnRadians(turnRemaining);
		if(log)
			System.out.println("Turn Radians Test - TurnRemaining before: " + Math.toDegrees(turnRemaining) + " turnRemaining after: " + Math.toDegrees(output));
		return output;
	}
	
	@Test public void testTurnRadians1(){testTurnRadiansStopAtBlindSpot("testTurnRadians1", -5, false);}
	@Test public void testTurnRadians2(){testTurnRadiansStopAtBlindSpot("testTurnRadians2", 8, false);}
	@Test public void testTurnRadians3(){testTurnRadiansStopAtBlindSpot("testTurnRadians3", -90, false);}
	@Test public void testTurnRadians4(){testTurnRadiansStopAtBlindSpot("testTurnRadians4", 90, false);}
	@Test public void testTurnRadians5(){testTurnRadiansStopAtBlindSpot("testTurnRadians5", 150, false);}
	@Test public void testTurnRadians6(){testTurnRadiansStopAtBlindSpot("testTurnRadians6", -93, false);}
	
	@Test public void testStopAtBlindSpot1(){testStopAtBlindSpot("testStopAtBlindSpot1" , 450, false);}
	@Test public void testStopAtBlindSpot2(){testStopAtBlindSpot("testStopAtBlindSpot2" , 108, false);}
	@Test public void testStopAtBlindSpot3(){testStopAtBlindSpot("testStopAtBlindSpot3" , -110, false);}
	@Test public void testStopAtBlindSpot4(){testStopAtBlindSpot("testStopAtBlindSpot4" , -108, false);}
	
	@Test
	public void testBooleanAtBlindSpot1(){
		String name = "testBooleanAtBlindSpot1";
		boolean log = false;
		testStopAtBlindSpot(name , 120, log);
		assertEquals(name, weaponComponent.getAtBlindSpot(), true);
		testStopAtBlindSpot(name, -20, log);
		assertEquals(name, weaponComponent.getAtBlindSpot(), false);
	}
	
	@Test
	public void testBooleanAtBlindSpot2(){
		String name = "testBooleanAtBlindSpot2";
		boolean log = false;
		testStopAtBlindSpot(name , -340, log);
		assertEquals(name, weaponComponent.getAtBlindSpot(), true);
		testStopAtBlindSpot(name, 50, log);
		assertEquals(name, weaponComponent.getAtBlindSpot(), false);
	}	
	
	/**
	 * Turns by the given turnRemaining. Breaks automatically once the BlindSpot is reached.
	 * Basically tests whether the steps are taken correctly. Gives error when the weapon isn't turned by the NavalRules.GUN_TURN_RATE  for example.
	 * @param testName Name of the test
	 * @param turnRemaining Amount you want to turn in degrees
	 * @param log true for logging. False for no logging.
	 */
	public void testTurnRadiansStopAtBlindSpot(String testName, double turnRemaining, boolean log){
		double degreesToTurn = turnRemaining;
		boolean turnRight = turnRemaining > 0;
		double result;
		
		//Turn until the blindspot or the destination is reached
		while(!weaponComponent.getAtBlindSpot() && !Utils.isNear(degreesToTurn, 0.0)){
			//Turn as much as you can
			result = testTurnRadians(Math.toRadians(degreesToTurn), log);
			//Make sure the testing value isn't 0 and that it won't go over the blindSpot
			if(turnRight){
				degreesToTurn -= NavalRules.GUN_TURN_RATE;
				if(degreesToTurn < 0)
					degreesToTurn = 0;
				if(weaponComponent.getAngle() + NavalRules.GUN_TURN_RATE_RADIANS > weaponComponent.getCopyOfBlindSpot().getFarRight())
					break;
			}
			else{
				degreesToTurn += NavalRules.GUN_TURN_RATE;
				if(degreesToTurn > 0)
					degreesToTurn = 0;
				if(weaponComponent.getAngle() - NavalRules.GUN_TURN_RATE_RADIANS < Utils.normalRelativeAngle(weaponComponent.getCopyOfBlindSpot().getFarLeft()))
					break;
			}
			assertEquals(testName, Utils.isNear(result, Math.toRadians(degreesToTurn)), true);
		}
		if(log){
			System.out.println("*** End of " + testName + " ***");
		}
	}
	
	

	public void testStopAtBlindSpot(String testName, double turnRemaining, boolean log){
		
		double degreesToTurn = Math.toRadians(turnRemaining);
		
		
		degreesToTurn = weaponComponent.turnRadians(degreesToTurn);
		if(Utils.isNear(turnRemaining,50)){
			System.out.println("turnRemaining 50 gives degreesToTurn: " + Math.toDegrees(degreesToTurn));
		}
		while(!weaponComponent.getAtBlindSpot() && !Utils.isNear(degreesToTurn, 0.0)){
			degreesToTurn = weaponComponent.turnRadians(degreesToTurn);
		}
//		System.out.println("getAtBlindSpot() " + weaponComponent.getAtBlindSpot() + " degrees to turn : " + Math.toDegrees(degreesToTurn));
		
		if(turnRemaining == Double.POSITIVE_INFINITY || turnRemaining == Double.NEGATIVE_INFINITY){
			System.out.println("Test@");
		}
		else if(turnRemaining > 0 && !Utils.isNear(degreesToTurn, 0.0)){
			assertEquals(testName, Utils.isNear(degreesToTurn, Math.toRadians(turnRemaining-start)), true);
		}
		else if(turnRemaining < 0 && !Utils.isNear(degreesToTurn, 0.0)){
			assertEquals(testName, Utils.isNear(degreesToTurn, Math.toRadians(360-end + turnRemaining)), true);
		}
		else if (log && Utils.isNear(degreesToTurn, 0.0)){
			System.out.println("The WeaponComponent reached its destination without encountering its blindspot. Please consider using a different value for testing.");
		}
		
		if(log){
			System.out.println("Degrees to turn: " + Math.toDegrees(degreesToTurn) + " Radians to turn: " + degreesToTurn  + " \n*** End of " + testName + " ***");
		}
		
	}
	
}
