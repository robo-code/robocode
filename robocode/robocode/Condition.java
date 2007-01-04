/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import robocode.security.RobocodeSecurityManager;


/**
 * Condition is used to define custom {@link robocode.AdvancedRobot#waitFor waitFor} and
 * custom events for a {@link robocode.AdvancedRobot}.  The code below is taken from
 * the sample robot named Target.  See Target.java for details.
 *  <PRE>
 *	  addCustomEvent(
 *			new Condition("triggerhit") { 
 *			  public boolean test() {
 *				  return (getEnergy() <= trigger);
 *				};
 *			}
 *		);
 *  </PRE>
 * You should note that by extending Condition, you are actually creating an inner class -- so
 * if you distribute your robot, there will be multiple class files.  (i.e. Target$1.class)
 *
 * @see robocode.AdvancedRobot#waitFor
 * @see robocode.AdvancedRobot#addCustomEvent
 * @see robocode.AdvancedRobot#removeCustomEvent
 * @see robocode.AdvancedRobot#onCustomEvent
 * 
 * @author Mathew A. Nelson
 */
public abstract class Condition {

	/**
	 * The priority of this condition.  Defaults to 80.
	 */
	public int priority = 80;

	/**
	 * The name of this condition
	 */
	public String name;

	/**
	 * Creates a new, unnamed Condition with the default priority.
	 */
	public Condition() {}

	/**
	 * Creates a new Condition with the specified name, and default priority.
	 */
	public Condition(String name) {
		this.name = name;
	}

	/**
	 * Creates a new Condition with the specified name and priority.
	 */
	public Condition(String name, int priority) {
		this.name = name;
		if (priority < 0) {
			SecurityManager m = System.getSecurityManager();

			if (m instanceof RobocodeSecurityManager) {
				RobocodeSecurityManager rsm = (RobocodeSecurityManager) m;

				rsm.threadOut("SYSTEM: Priority must be between 0 and 99.");
				rsm.threadOut("SYSTEM: Priority for condition " + name + " will be 0.");
			}
			priority = 0;
		} else if (priority > 99) {
			SecurityManager m = System.getSecurityManager();

			if (m instanceof RobocodeSecurityManager) {
				RobocodeSecurityManager rsm = (RobocodeSecurityManager) m;

				rsm.threadOut("SYSTEM: Priority must be between 0 and 99.");
				rsm.threadOut("SYSTEM: Priority for condition " + name + " will be 99.");
			}
			priority = 99;
		}
		this.priority = priority;
	}

	/**
	 * @return the name of this condition
	 */
	public String getName() {
		if (name != null) {
			return name;
		} else {
			return getClass().getName();
		}
	}

	/**
	 * @return the priority of this condition
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets the name of this condition
	 * @param newName String
	 */
	public void setName(String newName) {
		name = newName;
	}

	/**
	 * Sets the priority of this condition
	 * @param newPriority int
	 */
	public void setPriority(int newPriority) {
		priority = newPriority;
	}

	/**
	 * Overriding the test() method is the point of a Condition.  The game will call your
	 * test() function, and take action if it returns true.  This is valid for both
	 * {@link robocode.AdvancedRobot#waitFor waitFor} and {@link robocode.AdvancedRobot#addCustomEvent addCustomEvent}.
	 * 
	 * <P>You may not take any actions inside of test().
	 * 
	 * @return true if the condition has been met, false otherwise.
	 */
	public abstract boolean test();         
}
