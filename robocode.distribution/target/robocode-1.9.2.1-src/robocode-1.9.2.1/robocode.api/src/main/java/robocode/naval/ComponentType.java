package robocode.naval;

/**
 * Describes what the component represents. It assigns a type value.
 * <p/>
 * Note: When you subtract the bitmask from the same series of that
 * of the component from its type, you will end with an index
 * starting from the number 1.
 * @author Thales B.V. / Jiri Waning
 * @version 0.1
 * @since 1.8.3.0 Alpha 1
 */
public enum ComponentType {
	/**
	 * Default; components use these by default.
	 */
	
	/** The initial state of a component. **/
	UNDEFINED(0xFFFFFFFF),	// -1
	
	/** The type of the component is not know to us. **/
	UNKNOWN(0xFFFFFFFE),	// -2

	
	
	/**
	 * Weapon; these are used for a variety of weapons.
	 */
	
	/** The bitmask of the weapon series. **/
	WEAPON_BITMASK(0x10000000),
	
	/** The weapon on the <b>front</b> end of the ship. **/
	WEAPON_PROW(0x10000001),		// Front
	
	/** The weapon on the <b>right</b> side of the ship. **/
	WEAPON_STARBOARD(0x10000002),	// Right
	
	/** The weapon on the <b>back</b> end of the ship. **/
	WEAPON_STERN(0x10000003),		// Back
	
	/** The weapon on the <b>left</b> side of the ship. **/
	WEAPON_PORT(0x10000004),		// Left
	
	
	
	/**
	 * Radar; these are used for different radars.
	 */
	
	/** The bitmask for the radar series. **/
	RADAR_BITMASK(0x20000000),
	
	/** The <b>long</b> range radar <b>fixed</b> towards a certain angle. **/
	RADAR_LONG_STATIC(0x20000001),
	
	/** The <b>long</b> range radar that can <b>rotate</b> around its own Z-axe. **/
	RADAR_LONG_DYNAMIC(0x20000002),
	
	/** The <b>short</b> range radar <b>fixed</b> towards a certain angle. **/
	RADAR_SHORT_STATIC(0x20000003),
	
	/** The <b>short</b> range radar that can <b>rotate</b> around its own Z-axe. **/
	RADAR_SHORT_DYNAMIC(0x20000004),
	
	
	MINE_BITMASK(0x30000000),
	
	MINE_STANDARD(0x3000001),
	
	
	
	/**
	 * Special; these are special cases.
	 */
	
	/** The bitmask for the combination series. **/
	COMBI_BITMASK(0x40000000),
	
	/** This component represents a <b>combination</b> between a <b>radar</b> and a <b>weapon</b>. **/
	COMBI_RADAR_WEAPON(0x70000001);
	 
	
	
	 /**
	  * This is the component type.
	  */
	 private int type; 
	 
	 private ComponentType(int type) {
		 this.type = type;
	 }
	 
	 /**
	  * Get the CI value matching the given integer value.
	  * @param value The integer value of the component.
	  * @return The {@code CI} of the component.
	  */
	 public static ComponentType getValue(int value) {
		 for (ComponentType type: ComponentType.values()) {
			 if (type.toInt() == value) {
				 return type;
			 }
		 }
		 
		 return ComponentType.UNDEFINED;
	 }
	 
	 /**
	  * Get the integer value of the component type.
	  * @return The integer value of the component type.
	  */
	 public int toInt() {
		 return type;
	 }
	 
	 /**
	  * Determines if the type matches a certain bitmask series.
	  * @param bitMask The series to whom it has to belong.
	  * @return {@code true} if this {@code ComponentType} is a
	  * member of the given series; {@code false} otherwise.
	  */
	 public boolean fromSeries(ComponentType bitMask) {
		 return ((this.type & bitMask.type) > 0);
	 }
}
