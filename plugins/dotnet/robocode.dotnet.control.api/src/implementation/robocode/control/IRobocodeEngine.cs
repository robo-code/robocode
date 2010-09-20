#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control
{
    public delegate void BattleStartedEventHandler(Robocode.Control.Events.BattleStartedEvent evnt);
    public delegate void BattleFinishedEventHandler(Robocode.Control.Events.BattleFinishedEvent evnt);
    public delegate void BattleCompletedEventHandler(Robocode.Control.Events.BattleCompletedEvent evnt);
    public delegate void BattlePausedEventHandler(Robocode.Control.Events.BattlePausedEvent evnt);
    public delegate void BattleResumedEventHandler(Robocode.Control.Events.BattleResumedEvent evnt);
    public delegate void RoundStartedEventHandler(Robocode.Control.Events.RoundStartedEvent evnt);
    public delegate void RoundEndedEventHandler(Robocode.Control.Events.RoundEndedEvent evnt);
    public delegate void TurnStartedEventHandler(Robocode.Control.Events.TurnStartedEvent evnt);
    public delegate void TurnEndedEventHandler(Robocode.Control.Events.TurnEndedEvent evnt);
    public delegate void BattleMessageEventHandler(Robocode.Control.Events.BattleMessageEvent evnt);
    public delegate void BattleErrorEventHandler(Robocode.Control.Events.BattleErrorEvent evnt);

    /**
     * Interface for the RobocodeEngine.
     * 
     * @author Pavel Savara (original)
     */
    public interface IRobocodeEngine
    {
        event BattleStartedEventHandler BattleStarted;
        event BattleFinishedEventHandler BattleFinished;
        event BattleCompletedEventHandler BattleCompleted;
        event BattlePausedEventHandler BattlePaused;
        event BattleResumedEventHandler BattleResumed;
        event RoundStartedEventHandler RoundStarted;
        event RoundEndedEventHandler RoundEnded;
        event TurnStartedEventHandler TurnStarted;
        event TurnEndedEventHandler TurnEnded;
        event BattleMessageEventHandler BattleMessage;
        event BattleErrorEventHandler BattleError;

        /**
         * Closes the RobocodeEngine and releases any allocated resources.
         * You should call this when you have finished using the RobocodeEngine.
         * This method automatically disposes the Robocode window if it open.
         */
        void Close();

        /**
         * Returns the installed version of Robocode.
         *
         * @return the installed version of Robocode.
         */
        string Version { get; }

        /**
         * Shows or hides the Robocode window.
         *
         * @param visible {@code true} if the Robocode window must be set visible;
         *                {@code false} otherwise.
         */
        bool Visible { get; set; }

        /**
         * Returns all robots available from the local robot repository of Robocode.
         * These robots must exists in the /robocode/robots directory, and must be
         * compiled in advance.
         *
         * @return an array of all available robots from the local robot repository.
         * @see robocode.control.RobotSpecification
         * @see #getLocalRepository(String)
         */
        RobotSpecification[] GetLocalRepository();

        /**
         * Returns a selection of robots available from the local robot repository
         * of Robocode. These robots must exists in the /robocode/robots directory,
         * and must be compiled in advance.
         * </p>
         * Notice: If a specified robot cannot be found in the repository, it will
         * not be returned in the array of robots returned by this method.
         *
         * @param selectedRobotList a comma or space separated list of robots to
         *                          return. The full class name must be used for
         *                          specifying the individual robot, e.g.
         *                          "sample.Corners, sample.Crazy"
         * @return an array containing the available robots from the local robot
         *         repository based on the selected robots specified with the
         *         {@code selectedRobotList} parameter.
         * @see robocode.control.RobotSpecification
         * @see #getLocalRepository()
         * @since 1.6.2
         */
        RobotSpecification[] GetLocalRepository(String selectedRobotList);

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification the specification of the battle to play including the
         *                            participation robots.
         * @see #runBattle(robocode.control.BattleSpecification, boolean)
         * @see robocode.control.BattleSpecification
         * @see #getLocalRepository()
         */
        void RunBattle(BattleSpecification battleSpecification);

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification	   the specification of the battle to run including the
         *                     participating robots.
         * @param waitTillOver will block caller till end of battle if set
         * @see #runBattle(robocode.control.BattleSpecification)
         * @see robocode.control.BattleSpecification
         * @see #getLocalRepository()
         * @since 1.6.2
         */
        void RunBattle(BattleSpecification battleSpecification, bool waitTillOver);

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification the specification of the battle to run including the
         *                     participating robots.
         * @param initialPositions a comma or space separated list like: x1,y1,heading1,
         *        x2,y2,heading2, which are the coordinates and heading of robot #1 and #2.
         *        So e.g. 0,0,180, 50,80,270 means that robot #1 has position (0,0) and
         *        heading 180, and robot #2 has position (50,80) and heading 270.
         * @param waitTillOver will block caller till end of battle if set
         * @see #runBattle(BattleSpecification)
         * @see BattleSpecification
         * @see #getLocalRepository()
         * @since 1.7.1.2
         */
        void RunBattle(BattleSpecification battleSpecification, string initialPositions, bool waitTillOver);

        /**
         * Will block caller until current battle is over
         * @see #runBattle(robocode.control.BattleSpecification)
         * @see #runBattle(robocode.control.BattleSpecification, boolean)
         * @since 1.6.2
         */
        void WaitTillBattleOver();

        /**
         * Aborts the current battle if it is running.
         *
         * @see #runBattle(robocode.control.BattleSpecification)
         */
        void AbortCurrentBattle();
    }
}
