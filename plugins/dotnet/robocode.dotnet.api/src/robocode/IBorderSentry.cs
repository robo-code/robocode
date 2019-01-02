/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
namespace Robocode
{
    /// <summary>
    /// A robot that implement IBorderSentry is a robot type used for keeping other robots away from the borders,
    /// i.e. guarding the borders in order to prevent "wall crawlers".<br/>
    /// Robots that implement IBorderSentry have 400 extra life/energy (500 in total), but is placed at the border
    /// of the battlefield when the game is started.<br/>
    /// Border sentry robots cannot move away from the border area, and they can only make damage to robots that
    /// are moving into the border area. The size of the border area is determined by the
    /// <see cref="BattleRules.SentryBorderSize">battle rules</see>.<br/>
    /// This type of robot is intended for use in battles where robots should be forced away from the borders in
	/// order to prevent "wall crawlers".<br/>
    /// Border sentry robots does not get scores, and will not occur in the battle results or rankings.
    /// </summary>
    /// <seealso cref="BattleRules.SentryBorderSize"/>
    /// <seealso cref="JuniorRobot"/>
    /// <seealso cref="Robot"/>
    /// <seealso cref="AdvancedRobot"/>
    /// <seealso cref="TeamRobot"/>
    /// <seealso cref="RateControlRobot"/>
    /// <seealso cref="IDroid"/>
    public interface IBorderSentry
    {
    }
}