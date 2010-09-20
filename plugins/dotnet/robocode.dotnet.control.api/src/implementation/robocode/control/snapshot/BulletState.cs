using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Defines a bullet state, where the robot can be: just fired, moving somewhere, hitting a victim,
     * hitting another bullet, hitting the wall, exploded, or inactive when it done an inactivated.
     *
     * @author Flemming N. Larsen (original)
     * @since 1.6.2
     */
    public enum BulletState
    {
	    /** The bullet has just been fired this second and hence just been created. This state only last one turn. */
	    Fired = 0,

	    /** The bullet is now moving across the battlefield, but has not hit anything so far. */
	    Moving = 1,

	    /** The bullet has hit a robot victim. */
        HitVictim = 2,

	    /** The bullet has hit another bullet. */
        HitBullet = 3,

	    /** The bullet has the wall, i.e. one of the four borders. */
        HitWall = 4,

	    /** The bullet currently represents a robot explosion, i.e. a robot death. */
        Exploded = 5,

	    /** The bullet is currently inactive. Hence, it is not active or visible on the battlefield. */
        Inactive = 6
    }
}
