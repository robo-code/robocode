package robocode.control.snapshot;

/**
 * Interface of a mine snapshot at a specific time in a battle.
 * Basically a copy-paste of IBulletSnapshot,
 * which is why I added Flemming and Pavel in the authorlist.
 * 
 * @author Thales B.V. / Thomas Hakkers (original)
 * @author Pavel Savara (contributor)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.9.2.2
 */

public interface IMineSnapshot {
	/**
	 * Returns the mine state.
	 *
	 * @return the mine state.
	 */
	MineState getState();

	/**
	 * Returns the mine power.
	 *
	 * @return the mine power.
	 */
	double getPower();

	/**
	 * Returns the X position of the mine.
	 *
	 * @return the X position of the mine.
	 */
	double getX();

	/**
	 * Returns the Y position of the mine.
	 *
	 * @return the Y position of the mine.
	 */
	double getY();

	/**
	 * Returns the color of the mine.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getColor();

	/**
	 * Returns the current frame number to display, i.e. when the mine explodes.
	 *
	 * @return the current frame number.
	 * 
	 * @see #isExplosion()
	 * @see #getExplosionImageIndex()
	 */
	int getFrame();

	/**
	 * Checks if the mine has become an explosion, i.e. when a robot or mine has been hit.
	 *
	 * @return {@code true} if the mine is an explosion; {@code false} otherwise.
	 * 
	 * @see #getFrame()
	 * @see #getExplosionImageIndex()
	 */
	boolean isExplosion();

	/**
	 * Returns the explosion image index, which is different depending on the type of explosion.
	 * E.g. if it is a small explosion on a robot that has been hit by this mine,
	 * or a big explosion when a robot dies.
	 *
	 * @return the explosion image index.
	 * 
	 * @see #isExplosion()
	 * @see #getExplosionImageIndex()
	 */
	int getExplosionImageIndex();

	/**
	 * Returns the ID of the mine used for identifying the mine in a collection of mines.
	 *
	 * @return the ID of the mine.
	 */
	int getMineId();

	/**
	 * @return contestantIndex of the victim, or -1 if still in floating in the water
	 */
	int getVictimIndex();

	/**
	 * @return contestantIndex of the owner
	 */
	int getOwnerIndex();
}
