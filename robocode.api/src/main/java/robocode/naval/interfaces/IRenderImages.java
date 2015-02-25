package robocode.naval.interfaces;

/**
 * Describes methods used to get the images for several components.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public interface IRenderImages {
	/**
	 * Get the image of the gun when the given color has been applied.
	 * @param color The color mask that has to be applied.
	 * @return The colored image.
	 */
	IPaint getColoredGunRenderImage(Integer color);
	IPaint getColoredGunRenderNavalImage(Integer color);
	
	/**
	 * Get the image of the radar when the given color has been applied.
	 * @param color The color mask that has to be applied.
	 * @return The colored image.
	 */
	IPaint getColoredRadarRenderImage(Integer color);
	IPaint getColoredRadarRenderNavalImage(Integer color);
	
	/**
	 * Get the image of the mineComponent when the given color has been applied.
	 * @param color The color mask that has to be applied.
	 * @return The colored image.
	 */
	IPaint getColoredMineComponentRenderNavalImage(Integer color);
	IPaint getColoredMineRenderNavalImage(Integer color);
}
