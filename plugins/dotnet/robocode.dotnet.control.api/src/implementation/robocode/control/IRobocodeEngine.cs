/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Collections.Generic;
using System.Text;
using Robocode.Control.Events; // for the XML documentation

namespace Robocode.Control
{
    /// <summary>
    /// Event handler for the <see cref="BattleStartedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleStartedEvent"/> event.</param>
    public delegate void BattleStartedEventHandler(Robocode.Control.Events.BattleStartedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattleFinishedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleFinishedEvent"/> event.</param>
    public delegate void BattleFinishedEventHandler(Robocode.Control.Events.BattleFinishedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattleCompletedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleCompletedEvent"/> event.</param>
    public delegate void BattleCompletedEventHandler(Robocode.Control.Events.BattleCompletedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattlePausedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattlePausedEvent"/> event.</param>
    public delegate void BattlePausedEventHandler(Robocode.Control.Events.BattlePausedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattleResumedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleResumedEvent"/> event.</param>
    public delegate void BattleResumedEventHandler(Robocode.Control.Events.BattleResumedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="RoundStartedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="RoundStartedEvent"/> event.</param>
    public delegate void RoundStartedEventHandler(Robocode.Control.Events.RoundStartedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="Robocode.Control.Events.RoundEndedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="RoundEndedEvent"/> event.</param>
    public delegate void RoundEndedEventHandler(Robocode.Control.Events.RoundEndedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="TurnStartedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="TurnStartedEvent"/> event.</param>
    public delegate void TurnStartedEventHandler(Robocode.Control.Events.TurnStartedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="TurnEndedEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="TurnEndedEvent"/> event.</param>
    public delegate void TurnEndedEventHandler(Robocode.Control.Events.TurnEndedEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattleMessageEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleMessageEvent"/> event.</param>
    public delegate void BattleMessageEventHandler(Robocode.Control.Events.BattleMessageEvent evnt);

    /// <summary>
    /// Event handler for the <see cref="BattleErrorEvent"/>.
    /// </summary>
    /// <param name="evnt">The <see cref="BattleErrorEvent"/> event.</param>
    public delegate void BattleErrorEventHandler(Robocode.Control.Events.BattleErrorEvent evnt);

    /// <summary>
    /// Interface for a RobocodeEngine.
    /// </summary>
    public interface IRobocodeEngine
    {
        /// <summary>
        /// Occurs when a new battle is started.
        /// </summary>
        event BattleStartedEventHandler BattleStarted;

        /// <summary>
        /// Occurs when the battle is finished, where the battle can be either completed or aborted.
        /// </summary>
        event BattleFinishedEventHandler BattleFinished;

        /// <summary>
        /// Occurs when the battle is completed successfully and results are available.
        /// </summary>
        event BattleCompletedEventHandler BattleCompleted;

        /// <summary>
        /// Occurs when the battle is paused.
        /// </summary>
        event BattlePausedEventHandler BattlePaused;

        /// <summary>
        /// Occurs when the battle is resumed after having been paused.
        /// </summary>
        event BattleResumedEventHandler BattleResumed;

        /// <summary>
        /// Occurs when a new round is started.
        /// </summary>
        event RoundStartedEventHandler RoundStarted;

        /// <summary>
        /// Occurs when a round has ended.
        /// </summary>
        event RoundEndedEventHandler RoundEnded;

        /// <summary>
        /// Occurs when a new turn is started.
        /// </summary>
        event TurnStartedEventHandler TurnStarted;

        /// <summary>
        /// Occurs when a new turn is ended.
        /// </summary>
        event TurnEndedEventHandler TurnEnded;

        /// <summary>
        /// Occurs when a message from the battle is sent from the game.
        /// </summary>
        event BattleMessageEventHandler BattleMessage;

        /// <summary>
        /// Occurs when an error message from the battle is sent from the game.
        /// </summary>
        event BattleErrorEventHandler BattleError;

        /// <summary>
        /// Closes the RobocodeEngine and releases any allocated resources it holds.
        /// You should call this when you have finished using the RobocodeEngine.
        /// This method automatically disposes the Robocode window if it open.
        /// </summary>
        void Close();

        /// <summary>
        /// Contains the installed version of Robocode controlled by this RobocodeEngine.
        /// </summary>
        /// <value>
        /// The installed version of Robocode controlled by this RobocodeEngine.
        /// </value>
        string Version { get; }

        /// <summary>
        /// Contains the visible state of the Robocode window.
        /// </summary>
        /// <value>
        /// Sets/gets the visible state of the Robocode window, where
        /// <em>true</em> means that the window is visible, and
        /// <em>false</em> means that the window is hidden.
        /// </value>
        bool Visible { get; set; }

        /// <summary>
        /// Returns all robots available from the local robot repository of Robocode.
        /// These robots must exists in the \robocode\robots directory, and must be
        /// compiled in advance, before these robot are returned with this method.
        /// </summary>
        /// <returns>
        /// An array of all available robots from the local robot repository.
        /// </returns>
        RobotSpecification[] GetLocalRepository();

        /// <summary>
        /// Returns a selection of robots available from the local robot repository
        /// of Robocode. These robots must exists in the \robocode\robots directory,
        /// and must be compiled in advance, before these robot are returned with this method.
        /// <p/>
        /// Notice: If a specified robot cannot be found in the repository, it will
        /// not be returned in the array of robots returned by this method.
        /// </summary>
        /// <param name="selectedRobotList">A comma or space separated list of robots to return.
        /// The full class name must be used for specifying the individual robot, e.g.
        /// "sample.Corners, sample.Crazy".</param>
        /// <returns>
        /// An array containing the available robots from the local robot repository based on
        /// the selected robots specified with the <em>selectedRobotList</em> parameter.
        /// </returns>
        RobotSpecification[] GetLocalRepository(String selectedRobotList);

        /// <summary>
        /// Runs the specified battle.
        /// </summary>
        /// <param name="battleSpecification">The specification of the battle to run
        /// including the participation robots.</param>
        /// <seealso cref="GetLocalRepository()"/>
        void RunBattle(BattleSpecification battleSpecification);

        /// <summary>
        /// Runs the specified battle.
        /// </summary>
        /// <param name="battleSpecification">The specification of the battle to run
        /// including the participating robots.</param>
        /// <param name="waitTillOver">Will block caller till end of battle if set.</param>
        /// <seealso cref="GetLocalRepository()"/>
        void RunBattle(BattleSpecification battleSpecification, bool waitTillOver);

        /// <summary>
        /// Runs the specified battle.
        /// </summary>
        /// <param name="battleSpecification">The specification of the battle to run
        /// including the participating robots.</param>
        /// <param name="initialPositions">A comma or space separated list like:
        /// x1,y1,heading1, x2,y2,heading2, which are the coordinates and heading of
        /// robot #1 and #2. So e.g. 0,0,180, 50,80,270 means that robot #1 has position
        /// (0,0) and heading 180, and robot #2 has position (50,80) and heading 270.</param>
        /// <param name="waitTillOver">Will block caller till end of battle if set.</param>
        /// <seealso cref="GetLocalRepository()"/>
        void RunBattle(BattleSpecification battleSpecification, string initialPositions, bool waitTillOver);

        /// <summary>
        /// Will block caller until current battle is over.
        /// </summary>
        /// <seealso cref="RunBattle(BattleSpecification)"/>
        void WaitTillBattleOver();

        /// <summary>
        /// Aborts the current battle if it is running.
        /// </summary>
        /// <seealso cref="RunBattle(BattleSpecification)"/>
        void AbortCurrentBattle();
    }
}
