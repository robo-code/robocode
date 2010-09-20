using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control
{
    /**
     * Defines the size of a battlefield, which is a part of the
     * {@link BattleSpecification}.
     *
     * @author Mathew A. Nelson (original)
     * @author Flemming N. Larsen (contributor)
     * @see BattleSpecification#BattleSpecification(int, BattlefieldSpecification, RobotSpecification[])
     * @see BattleSpecification#BattleSpecification(int, long, double, BattlefieldSpecification, RobotSpecification[])
     * @see BattleSpecification#getBattlefield()
     */
    [Serializable]
    public class BattlefieldSpecification
    {
	    private readonly int width;
        private readonly int height;

	    /**
	     * Creates a standard 800 x 600 battlefield.
	     */
	    public BattlefieldSpecification() :
            this(800, 600) {
	    }

	    /**
	     * Creates a battlefield of the specified width and height.
	     *
	     * @param width  the width of the battlefield, where 400 >= width < 5000.
	     * @param height the height of the battlefield, where 400 >= height < 5000.
	     * @throws IllegalArgumentException if the width or height is less than 400
	     *                                  or greater than 5000
	     */
	    public BattlefieldSpecification(int width, int height) {
		    if (width < 400 || width > 5000) {
			    throw new ArgumentException("width must be: 400 >= width < 5000");
		    }
		    if (height < 400 || height > 5000) {
			    throw new ArgumentException("height must be: 400 >= height < 5000");
		    }

		    this.width = width;
		    this.height = height;
	    }

	    /**
	     * Returns the width of this battlefield.
	     *
	     * @return the width of this battlefield.
	     */
	    public int Width {
            get { return width; }
	    }

	    /**
	     * Returns the height of this battlefield.
	     *
	     * @return the height of this battlefield.
	     */
	    public int Height {
		    get { return height; }
	    }
    }
}
