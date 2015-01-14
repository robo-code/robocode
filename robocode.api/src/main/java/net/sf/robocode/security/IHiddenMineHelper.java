package net.sf.robocode.security;

import robocode.Mine;

/**
 * Interface for the HiddenHelper of a mine.
 * @author Thales B.V. / Thomas Hakkers
 */
public interface IHiddenMineHelper {
	void update(Mine mine, String victimName, boolean isActive);

}
