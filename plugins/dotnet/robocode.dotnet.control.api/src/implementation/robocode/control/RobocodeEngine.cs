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
using System.IO;
using System.Reflection;
using Robocode.Control.Events;
using Robocode.Control.Snapshot;
using net.sf.robocode.security;

namespace Robocode.Control
{
    /// <summary>
    /// The RobocodeEngine is the interface provided for external applications
    /// in order to let these applications run battles within the Robocode application,
    /// and to get the results from these battles.
    /// <p/>
    /// This class in the main entry class of the <see cref="Robocode.Control"/> namespace.
    /// <p/>
    /// The RobocodeEngine is used by e.g. RoboRumble@Home client, which is integrated in
    /// Robocode. In addition, the RobocodeEngine is also used by the test units for
    /// testing the Robocode application itself.
    /// </summary>
    public class RobocodeEngine : IRobocodeEngine
    {
        /// <inheritdoc/>
        public event BattleStartedEventHandler BattleStarted;
        /// <inheritdoc/>
        public event BattleFinishedEventHandler BattleFinished;
        /// <inheritdoc/>
        public event BattleCompletedEventHandler BattleCompleted;
        /// <inheritdoc/>
        public event BattlePausedEventHandler BattlePaused;
        /// <inheritdoc/>
        public event BattleResumedEventHandler BattleResumed;
        /// <inheritdoc/>
        public event RoundStartedEventHandler RoundStarted;
        /// <inheritdoc/>
        public event RoundEndedEventHandler RoundEnded;
        /// <inheritdoc/>
        public event TurnStartedEventHandler TurnStarted;
        /// <inheritdoc/>
        public event TurnEndedEventHandler TurnEnded;
        /// <inheritdoc/>
        public event BattleMessageEventHandler BattleMessage;
        /// <inheritdoc/>
        public event BattleErrorEventHandler BattleError;

        // The internal hidden engine that has been generated based on the Java version of the engine
        private robocode.control.RobocodeEngine engine;
        // The internal robocode.control.events.IBattleListener
        private BattleObserver battleObserver;
        // Flag specifying if the Robocode window is currently visible
        private bool isVisible;

        #region Public API

        /// <summary>
        /// Creates a new RobocodeEngine for controlling Robocode.
        /// In order for this constructor to work, the current working directory must be the
        /// home directory directory of Robocode, e.g. C:\Robocode
        /// </summary>
        /// <seealso cref="Close()"/>
        public RobocodeEngine() :
            this(AppDomain.CurrentDomain.BaseDirectory)
        {
        }

        /// <summary>
        /// Creates a new RobocodeEngine for controlling Robocode, where the home directory
        /// of Robocode is specified.
        /// </summary>
        /// <param name="robocodeHome">The root directory of Robocode, e.g. "C:\Robocode".</param>
        /// <seealso cref="Close()"/>
        public RobocodeEngine(string robocodeHome)
        {
            robocode.control.RobocodeEngine.Init(robocodeHome);
            engine = new robocode.control.RobocodeEngine(new java.io.File(robocodeHome));
            battleObserver = new BattleObserver(this);
            engine.addBattleListener(battleObserver);
        }

        ~RobocodeEngine()
        {
            // Make sure Close() is called to prevent memory leaks
            Close();
        }

        /// <inheritdoc/>
        public void Close()
        {
            engine.close();
        }

        /// <inheritdoc/>
        public string Version
        {
            get { return engine.getVersion(); }
        }

        /// <summary>
        /// Returns the current working directory for Robocode.
        /// </summary>
        /// <returns>
        /// The name of the current working directory.
        /// </returns>
        public static string CurrentWorkingDir
        {
            get { return robocode.control.RobocodeEngine.getCurrentWorkingDir().getAbsolutePath(); }
        }

        /// <summary>
        /// Returns the directory containing the robots.
        /// </summary>
        /// <returns>
        /// The name of the robot directory containing all robots.
        /// </returns>
        public static string RobotsDir
        {
            get { return robocode.control.RobocodeEngine.getRobotsDir().getAbsolutePath(); }
        }

        /// <inheritdoc/>
        public bool Visible
        {
            set
            {
                isVisible = value;
                engine.setVisible(value);
            }
            get
            {
                return isVisible;
            }
        }

        /// <inheritdoc/>
        public RobotSpecification[] GetLocalRepository()
        {
            return MapRobotSpecifications(engine.getLocalRepository());
        }

        /// <inheritdoc/>
        public RobotSpecification[] GetLocalRepository(string selectedRobots)
        {
            return MapRobotSpecifications(engine.getLocalRepository(selectedRobots));
        }

        /// <inheritdoc/>
        public void RunBattle(BattleSpecification battleSpecification)
        {
            RunBattle(battleSpecification, null, false);
        }

        /// <inheritdoc/>
        public void RunBattle(BattleSpecification battleSpecification, bool waitTillOver)
        {
            RunBattle(battleSpecification, "", waitTillOver);
        }

        /// <inheritdoc/>
        public void RunBattle(BattleSpecification battleSpecification, string initialPositions, bool waitTillOver)
        {
            engine.runBattle(MapBattleSpecification(battleSpecification), initialPositions, waitTillOver);
        }

        /// <inheritdoc/>
        public void WaitTillBattleOver()
        {
            engine.waitTillBattleOver();
        }

        /// <inheritdoc/>
        public void AbortCurrentBattle()
        {
            engine.abortCurrentBattle();
        }

        /// <summary>
        /// Prints out all running threads to standard system out.
        /// </summary>
        public static void PrintRunningThreads()
        {
            robocode.control.RobocodeEngine.printRunningThreads();
        }

        /// <summary>
        /// Enables or disables messages and warnings logged to Console.Out.
        /// </summary>
        /// <value>
        /// <em>true</em> means that log messages must be enabled, and
        /// <em>false</em> means that log messages must be disabled.
        /// </value>
        /// <seealso cref="LogErrorsEnabled"/>
        public static bool LogMessagesEnabled
        {
            set
            {
                robocode.control.RobocodeEngine.setLogMessagesEnabled(value);
            }
        }

        /// <summary>
        /// Enables or disables errors logged to Console.Error.
        /// </summary>
        /// <value>
        /// <em>true</em> means that log errors must be enabled, and
        /// <em>false</em> means that log errors must be disabled.
        /// </value>
        /// <seealso cref="LogMessagesEnabled"/>
        public static bool LogErrorsEnabled
        {
            set
            {
                robocode.control.RobocodeEngine.setLogErrorsEnabled(value);
            }
        }

        #endregion

        #region Event dispatchers

        private void OnBattleStarted(BattleStartedEvent e)
        {
            if (BattleStarted != null)
                BattleStarted(e);
        }

        private void OnBattleFinished(BattleFinishedEvent e)
        {
            if (BattleFinished != null)
                BattleFinished(e);
        }

        private void OnBattleCompleted(BattleCompletedEvent e)
        {
            if (BattleCompleted != null)
                BattleCompleted(e);
        }

        private void OnBattlePaused(BattlePausedEvent e)
        {
            if (BattlePaused != null)
                BattlePaused(e);
        }

        private void OnBattleResumed(BattleResumedEvent e)
        {
            if (BattleResumed != null)
                BattleResumed(e);
        }

        private void OnRoundStarted(RoundStartedEvent e)
        {
            if (RoundStarted != null)
                RoundStarted(e);
        }

        private void OnRoundEnded(Robocode.Control.Events.RoundEndedEvent e)
        {
            if (RoundEnded != null)
                RoundEnded(e);
        }

        private void OnTurnStarted(TurnStartedEvent e)
        {
            if (TurnStarted != null)
                TurnStarted(e);
        }

        private void OnTurnEnded(TurnEndedEvent e)
        {
            if (TurnEnded != null)
                TurnEnded(e);
        }

        private void OnBattleMessage(BattleMessageEvent e)
        {
            if (BattleMessage != null)
                BattleMessage(e);
        }

        private void OnBattleError(BattleErrorEvent e)
        {
            if (BattleError != null)
                BattleError(e);
        }

        #endregion

        #region Specification mappers

        private RobotSpecification[] MapRobotSpecifications(robocode.control.RobotSpecification[] specifications)
        {
            if (specifications == null)
            {
                return null;
            }

            RobotSpecification[] mappedSpecifications = new RobotSpecification[specifications.Length];

            for (int i = 0; i < specifications.Length; i++)
            {
                mappedSpecifications[i] = MapRobotSpecification(specifications[i]);
            }
            return mappedSpecifications;
        }

        private RobotSpecification MapRobotSpecification(robocode.control.RobotSpecification spec)
        {
            return new RobotSpecification(spec);
        }

        private robocode.control.RobotSpecification[] MapRobotSpecifications(RobotSpecification[] specifications)
        {
            if (specifications == null)
            {
                return null;
            }

            robocode.control.RobotSpecification[] mappedSpecifications = new robocode.control.RobotSpecification[specifications.Length];

            for (int i = 0; i < specifications.Length; i++)
            {
                mappedSpecifications[i] = MapRobotSpecification(specifications[i]);
            }
            return mappedSpecifications;
        }

        private robocode.control.RobotSpecification MapRobotSpecification(RobotSpecification spec)
        {
            return spec.robotSpecification;
        }

        private robocode.control.BattleSpecification MapBattleSpecification(BattleSpecification spec)
        {
            robocode.control.BattlefieldSpecification battlefieldSpec = new robocode.control.BattlefieldSpecification(spec.Battlefield.Width, spec.Battlefield.Height);
            robocode.control.RobotSpecification[] robotSpecs = MapRobotSpecifications(spec.Robots);
            robocode.control.RobotSetup[] initialSetups = MapInitialSetups(spec.InitialSetups);

            return new robocode.control.BattleSpecification(battlefieldSpec, spec.NumRounds, spec.InactivityTime,
                spec.GunCoolingRate, spec.SentryBorderSize, spec.HideEnemyNames, robotSpecs, initialSetups);
        }

        private robocode.control.RobotSetup[] MapInitialSetups(RobotSetup[] setups)
        {
            if (setups == null)
            {
                return null;
            }

            robocode.control.RobotSetup[] mappedSetups = new robocode.control.RobotSetup[setups.Length];

            for (int i = 0; i < setups.Length; i++)
            {
                mappedSetups[i] = MapRobotSetup(setups[i]);
            }
            return mappedSetups;
        }

        private robocode.control.RobotSetup MapRobotSetup(RobotSetup setup)
        {
            return new robocode.control.RobotSetup(setup.X, setup.Y, setup.Heading);
        }
        #endregion

        #region Battle observer (internal engine events -> Robocode.Control.Events)

        private class BattleObserver : robocode.control.events.IBattleListener
        {
            RobocodeEngine engine;

            public BattleObserver(RobocodeEngine engine)
            {
                this.engine = engine;
            }

            public void onBattleStarted(robocode.control.events.BattleStartedEvent evnt)
            {
                engine.OnBattleStarted(MapEvent(evnt));
            }

            public void onBattleFinished(robocode.control.events.BattleFinishedEvent evnt)
            {
                engine.OnBattleFinished(MapEvent(evnt));
            }

            public void onBattleCompleted(robocode.control.events.BattleCompletedEvent evnt)
            {
                engine.OnBattleCompleted(MapEvent(evnt));
            }

            public void onBattlePaused(robocode.control.events.BattlePausedEvent evnt)
            {
                engine.OnBattlePaused(new BattlePausedEvent());
            }

            public void onBattleResumed(robocode.control.events.BattleResumedEvent evnt)
            {
                engine.OnBattleResumed(new BattleResumedEvent());
            }

            public void onRoundStarted(robocode.control.events.RoundStartedEvent evnt)
            {
                engine.OnRoundStarted(MapEvent(evnt));
            }

            public void onRoundEnded(robocode.control.events.RoundEndedEvent evnt)
            {
                engine.OnRoundEnded(MapEvent(evnt));
            }

            public void onTurnStarted(robocode.control.events.TurnStartedEvent evnt)
            {
                engine.OnTurnStarted(new TurnStartedEvent());
            }

            public void onTurnEnded(robocode.control.events.TurnEndedEvent evnt)
            {
                engine.OnTurnEnded(MapEvent(evnt));
            }

            public void onBattleMessage(robocode.control.events.BattleMessageEvent evnt)
            {
                engine.OnBattleMessage(MapEvent(evnt));
            }

            public void onBattleError(robocode.control.events.BattleErrorEvent evnt)
            {
                engine.OnBattleError(MapEvent(evnt));
            }
        }

        #endregion

        #region Event mappers (generated events -> Robocode.Control.Events)

        private static BattleStartedEvent MapEvent(robocode.control.events.BattleStartedEvent evnt)
        {
            return new BattleStartedEvent(MapBattleRules(evnt.getBattleRules()), evnt.getRobotsCount(), evnt.isReplay());
        }

        private static BattleFinishedEvent MapEvent(robocode.control.events.BattleFinishedEvent evnt)
        {
            return new BattleFinishedEvent(evnt.isAborted());
        }

        private static BattleCompletedEvent MapEvent(robocode.control.events.BattleCompletedEvent evnt)
        {
            return new BattleCompletedEvent(MapBattleRules(evnt.getBattleRules()), MapBattleResults(evnt.getIndexedResults()));
        }

        private static BattleMessageEvent MapEvent(robocode.control.events.BattleMessageEvent evnt)
        {
            return new BattleMessageEvent(evnt.getMessage());
        }

        private static BattleErrorEvent MapEvent(robocode.control.events.BattleErrorEvent evnt)
        {
            return new BattleErrorEvent(evnt.getError());
        }

        private static RoundStartedEvent MapEvent(robocode.control.events.RoundStartedEvent evnt)
        {
            return new RoundStartedEvent(MapTurnSnapshot(evnt.getStartSnapshot()), evnt.getRound());
        }

        private static Robocode.Control.Events.RoundEndedEvent MapEvent(robocode.control.events.RoundEndedEvent evnt)
        {
            return new Robocode.Control.Events.RoundEndedEvent(evnt.getRound(), evnt.getTurns(), evnt.getTotalTurns());
        }

        private static TurnEndedEvent MapEvent(robocode.control.events.TurnEndedEvent evnt)
        {
            return new TurnEndedEvent(MapTurnSnapshot(evnt.getTurnSnapshot()));
        }

        private static BattleCompletedEvent[] MapEvents(robocode.control.events.BattleCompletedEvent[] events)
        {
            if (events == null)
            {
                return null;
            }

            BattleCompletedEvent[] mappedEvents = new BattleCompletedEvent[events.Length];

            for (int i = 0; i < events.Length; i++)
            {
                mappedEvents[i] = MapEvent(events[i]);
            }
            return mappedEvents;
        }

        private static BattleResults[] MapBattleResults(java.lang.Object[] results)
        {
            if (results == null)
            {
                return null;
            }

            BattleResults[] mappedResults = new BattleResults[results.Length];

            for (int i = 0; i < results.Length; i++)
            {
                mappedResults[i] = MapBattleResults(results[i]);
            }
            return mappedResults;
        }

        private static BattleRules MapBattleRules(java.lang.Object obj)
        {
            int battlefieldWidth = obj.Invoke<int>("getBattlefieldWidth", "()I", new object[] { });
            int battlefieldHeight = obj.Invoke<int>("getBattlefieldHeight", "()I", new object[] { });
            int numRounds = obj.Invoke<int>("getNumRounds", "()I", new object[] { });
            double gunCoolingRate = obj.Invoke<double>("getGunCoolingRate", "()D", new object[] { });
            long inactivityTime = obj.Invoke<long>("getInactivityTime", "()J", new object[] { });
            bool hideEnemyNames = obj.Invoke<bool>("getHideEnemyNames", "()Z", new object[] { });
            int sentryBorderSize = obj.Invoke<int>("getSentryBorderSize", "()I", new object[] { });

            MethodInfo method = typeof(BattleRules).GetMethod("createHiddenHelper", BindingFlags.Static | BindingFlags.NonPublic);
            IHiddenRulesHelper rulesHelper = (IHiddenRulesHelper)method.Invoke(null, null);

            return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime, hideEnemyNames, sentryBorderSize);
        }

        private static BattleResults MapBattleResults(java.lang.Object obj)
        {
            string teamLeaderName = obj.Invoke<java.lang.String>("getTeamLeaderName", "()Ljava/lang/String;", new object[] { });
            int rank = obj.Invoke<int>("getRank", "()I", new object[] { });
            int score = obj.Invoke<int>("getScore", "()I", new object[] { });
            int survival = obj.Invoke<int>("getSurvival", "()I", new object[] { });
            int lastSurvivorBonus = obj.Invoke<int>("getLastSurvivorBonus", "()I", new object[] { });
            int bulletDamage = obj.Invoke<int>("getBulletDamage", "()I", new object[] { });
            int bulletDamageBonus = obj.Invoke<int>("getBulletDamageBonus", "()I", new object[] { });
            int ramDamage = obj.Invoke<int>("getRamDamage", "()I", new object[] { });
            int ramDamageBonus = obj.Invoke<int>("getRamDamageBonus", "()I", new object[] { });
            int firsts = obj.Invoke<int>("getFirsts", "()I", new object[] { });
            int seconds = obj.Invoke<int>("getSeconds", "()I", new object[] { });
            int thirds = obj.Invoke<int>("getThirds", "()I", new object[] { });

            return new BattleResults(teamLeaderName, rank, score, survival, lastSurvivorBonus,
                bulletDamage, bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, thirds);
        }

        #endregion

        #region Snapshot mappers (generated snapshots -> Robocode.Control.Snapshot)

        private static ITurnSnapshot MapTurnSnapshot(robocode.control.snapshot.ITurnSnapshot turnSnapshot)
        {
            return new TurnSnapshot(turnSnapshot);
        }

        private static IRobotSnapshot[] MapRobotSnapshots(robocode.control.snapshot.IRobotSnapshot[] robotSnapshots)
        {
            if (robotSnapshots == null)
            {
                return null;
            }

            IRobotSnapshot[] mappedSnapshots = new IRobotSnapshot[robotSnapshots.Length];

            for (int i = 0; i < robotSnapshots.Length; i++)
            {
                mappedSnapshots[i] = MapRobotSnapshot(robotSnapshots[i]);
            }
            return mappedSnapshots;
        }

        private static IRobotSnapshot MapRobotSnapshot(robocode.control.snapshot.IRobotSnapshot robotSnapshot)
        {
            return new RobotSnapshot(robotSnapshot);
        }

        private static IScoreSnapshot[] MapScoreSnapshots(robocode.control.snapshot.IScoreSnapshot[] bulletSnapshots)
        {
            if (bulletSnapshots == null)
            {
                return null;
            }

            IScoreSnapshot[] mappedSnapshots = new IScoreSnapshot[bulletSnapshots.Length];

            for (int i = 0; i < bulletSnapshots.Length; i++)
            {
                mappedSnapshots[i] = MapScoreSnapshot(bulletSnapshots[i]);
            }
            return mappedSnapshots;
        }

        private static IScoreSnapshot MapScoreSnapshot(robocode.control.snapshot.IScoreSnapshot scoreSnapshot)
        {
            return new ScoreSnapshot(scoreSnapshot);
        }

        private static IBulletSnapshot[] MapBulletSnapshots(robocode.control.snapshot.IBulletSnapshot[] bulletSnapshots)
        {
            if (bulletSnapshots == null)
            {
                return null;
            }

            IBulletSnapshot[] mappedSnapshots = new IBulletSnapshot[bulletSnapshots.Length];

            for (int i = 0; i < bulletSnapshots.Length; i++)
            {
                mappedSnapshots[i] = MapBulletSnapshot(bulletSnapshots[i]);
            }
            return mappedSnapshots;
        }

        private static IBulletSnapshot MapBulletSnapshot(robocode.control.snapshot.IBulletSnapshot bulletSnapshot)
        {
            return new BulletSnapshot(bulletSnapshot);
        }

        private static IDebugProperty[] MapDebugProperties(robocode.control.snapshot.IDebugProperty[] debugProperties)
        {
            if (debugProperties == null)
            {
                return null;
            }

            IDebugProperty[] mappedProperties = new IDebugProperty[debugProperties.Length];

            for (int i = 0; i < debugProperties.Length; i++)
            {
                mappedProperties[i] = MapDebugProperty(debugProperties[i]);
            }
            return mappedProperties;
        }

        private static IDebugProperty MapDebugProperty(robocode.control.snapshot.IDebugProperty debugProperty)
        {
            return new DebugProperty(debugProperty.getKey(), debugProperty.getValue());
        }

        private static RobotState MapRobotState(robocode.control.snapshot.RobotState robotState)
        {
            switch (robotState.getValue())
            {
                case 0:
                    return RobotState.Active;
                case 1:
                    return RobotState.HitWall;
                case 2:
                    return RobotState.HitRobot;
                case 3:
                default:
                    return RobotState.Dead;
            }
        }

        private static BulletState MapBulletState(robocode.control.snapshot.BulletState bulletState)
        {
            switch (bulletState.getValue())
            {
                case 0:
                    return BulletState.Fired;
                case 1:
                    return BulletState.Moving;
                case 2:
                    return BulletState.HitVictim;
                case 3:
                    return BulletState.HitBullet;
                case 4:
                    return BulletState.HitWall;
                case 5:
                    return BulletState.Exploded;
                case 6:
                default:
                    return BulletState.Inactive;
            }
        }

        #endregion

        #region Robocode.Control.Snapshot implementations

        private class TurnSnapshot : ITurnSnapshot
        {
            // The real snapshot
            private readonly robocode.control.snapshot.ITurnSnapshot turnSnapshot;

            // Cached values created, when needed
            private IRobotSnapshot[] robots;
            private IBulletSnapshot[] bullets;
            private int? tps;
            private int? round;
            private int? turn;
            private IScoreSnapshot[] sortedTeamScores;
            private IScoreSnapshot[] indexedTeamScores;

            public TurnSnapshot(robocode.control.snapshot.ITurnSnapshot turnSnapshot)
            {
                this.turnSnapshot = turnSnapshot;
            }

            public IRobotSnapshot[] Robots
            {
                get
                {
                    if (robots == null)
                    {
                        robots = MapRobotSnapshots(turnSnapshot.getRobots());
                    }
                    return robots;
                }
            }

            public IBulletSnapshot[] Bullets
            {
                get
                {
                    if (bullets == null)
                    {
                        bullets = MapBulletSnapshots(turnSnapshot.getBullets());
                    }
                    return bullets;
                }
            }

            public int TPS
            {
                get
                {
                    if (tps == null)
                    {
                        tps = turnSnapshot.getTPS();
                    }
                    return tps.Value;
                }
            }

            public int Round
            {
                get
                {
                    if (round == null)
                    {
                        round = turnSnapshot.getRound();
                    }
                    return round.Value;
                }
            }

            public int Turn
            {
                get
                {
                    if (turn == null)
                    {
                        turn = turnSnapshot.getTurn();
                    }
                    return turn.Value;
                }
            }

            public IScoreSnapshot[] SortedTeamScores
            {
                get
                {
                    if (sortedTeamScores == null)
                    {
                        sortedTeamScores = MapScoreSnapshots(turnSnapshot.getSortedTeamScores());
                    }
                    return sortedTeamScores;
                }
            }

            public IScoreSnapshot[] IndexedTeamScores
            {
                get
                {
                    if (indexedTeamScores == null)
                    {
                        indexedTeamScores = MapScoreSnapshots(turnSnapshot.getIndexedTeamScores());
                    }
                    return indexedTeamScores;
                }
            }
        }

        private class RobotSnapshot : IRobotSnapshot
        {
            // The real snapshot
            private readonly robocode.control.snapshot.IRobotSnapshot robotSnapshot;

            // Cached values created, when needed
            private string name;
            private string shortName;
            private string veryShortName;
            private string teamName;
            private int? robotIndex;
            private int? teamIndex;
            private RobotState? state;
            private double? energy;
            private double? velocity;
            private double? bodyHeading;
            private double? gunHeading;
            private double? radarHeading;
            private double? gunHeat;
            private double? x;
            private double? y;
            private int? bodyColor;
            private int? gunColor;
            private int? radarColor;
            private int? scanColor;
            private bool? isDroid;
            private bool? isPaintRobot;
            private bool? isPaintEnabled;
            private bool? isSGPaintEnabled;
            private IDebugProperty[] debugProperties;
            private string outputStreamSnapshot;
            private IScoreSnapshot scoreSnapshot;

            public RobotSnapshot(robocode.control.snapshot.IRobotSnapshot robotSnapshot)
            {
                this.robotSnapshot = robotSnapshot;
            }

            public string Name
            {
                get
                {
                    if (name == null)
                    {
                        name = robotSnapshot.getName();
                    }
                    return name;
                }
            }

            public string ShortName
            {
                get
                {
                    if (shortName == null)
                    {
                        shortName = robotSnapshot.getShortName();
                    }
                    return shortName;
                }
            }

            public string VeryShortName
            {
                get
                {
                    if (veryShortName == null)
                    {
                        veryShortName = robotSnapshot.getVeryShortName();
                    }
                    return veryShortName;
                }
            }

            public string TeamName
            {
                get
                {
                    if (teamName == null)
                    {
                        teamName = robotSnapshot.getTeamName();
                    }
                    return teamName;
                }
            }

            public int RobotIndex
            {
                get
                {
                    if (robotIndex == null)
                    {
                        robotIndex = robotSnapshot.getRobotIndex();
                    }
                    return robotIndex.Value;
                }
            }

            public int TeamIndex
            {
                get
                {
                    if (teamIndex == null)
                    {
                        teamIndex = robotSnapshot.getTeamIndex();
                    }
                    return teamIndex.Value;
                }
            }

            public int ContestantIndex
            {
                get
                {
                    return TeamIndex >= 0 ? TeamIndex : RobotIndex;
                }
            }

            public RobotState State
            {
                get
                {
                    if (state == null)
                    {
                        state = MapRobotState(robotSnapshot.getState());
                    }
                    return state.Value;
                }
            }

            public double Energy
            {
                get
                {
                    if (energy == null)
                    {
                        energy = robotSnapshot.getEnergy();
                    }
                    return energy.Value;
                }
            }

            public double Velocity
            {
                get
                {
                    if (velocity == null)
                    {
                        velocity = robotSnapshot.getVelocity();
                    }
                    return velocity.Value;
                }
            }

            public double BodyHeading
            {
                get
                {
                    if (bodyHeading == null)
                    {
                        bodyHeading = robotSnapshot.getBodyHeading();
                    }
                    return bodyHeading.Value;
                }
            }

            public double GunHeading
            {
                get
                {
                    if (gunHeading == null)
                    {
                        gunHeading = robotSnapshot.getGunHeading();
                    }
                    return gunHeading.Value;
                }
            }

            public double RadarHeading
            {
                get
                {
                    if (radarHeading == null)
                    {
                        radarHeading = robotSnapshot.getRadarHeading();
                    }
                    return radarHeading.Value;
                }
            }

            public double GunHeat
            {
                get
                {
                    if (gunHeat == null)
                    {
                        gunHeat = robotSnapshot.getGunHeat();
                    }
                    return gunHeat.Value;
                }
            }

            public double X
            {
                get
                {
                    if (x == null)
                    {
                        x = robotSnapshot.getX();
                    }
                    return x.Value;
                }
            }

            public double Y
            {
                get
                {
                    if (y == null)
                    {
                        y = robotSnapshot.getY();
                    }
                    return y.Value;
                }
            }

            public int BodyColor
            {
                get
                {
                    if (bodyColor == null)
                    {
                        bodyColor = robotSnapshot.getBodyColor();
                    }
                    return bodyColor.Value;
                }
            }

            public int GunColor
            {
                get
                {
                    if (gunColor == null)
                    {
                        gunColor = robotSnapshot.getGunColor();
                    }
                    return gunColor.Value;
                }
            }

            public int RadarColor
            {
                get
                {
                    if (radarColor == null)
                    {
                        radarColor = robotSnapshot.getRadarColor();
                    }
                    return radarColor.Value;
                }
            }

            public int ScanColor
            {
                get
                {
                    if (scanColor == null)
                    {
                        scanColor = robotSnapshot.getScanColor();
                    }
                    return scanColor.Value;
                }
            }

            public bool IsDroid
            {
                get
                {
                    if (isDroid == null)
                    {
                        isDroid = robotSnapshot.isDroid();
                    }
                    return isDroid.Value;
                }
            }

            public bool IsPaintRobot
            {
                get
                {
                    if (isPaintRobot == null)
                    {
                        isPaintRobot = robotSnapshot.isPaintRobot();
                    }
                    return isPaintRobot.Value;
                }
            }

            public bool IsPaintEnabled
            {
                get
                {
                    if (isPaintEnabled == null)
                    {
                        isPaintEnabled = robotSnapshot.isPaintEnabled();
                    }
                    return isPaintEnabled.Value;
                }
            }

            public bool IsSGPaintEnabled
            {
                get
                {
                    if (isSGPaintEnabled == null)
                    {
                        isSGPaintEnabled = robotSnapshot.isSGPaintEnabled();
                    }
                    return isSGPaintEnabled.Value;
                }
            }

            public IDebugProperty[] DebugProperties
            {
                get
                {
                    if (debugProperties == null)
                    {
                        debugProperties = MapDebugProperties(robotSnapshot.getDebugProperties());
                    }
                    return debugProperties;
                }
            }

            public string OutputStreamSnapshot
            {
                get
                {
                    if (outputStreamSnapshot == null)
                    {
                        outputStreamSnapshot = robotSnapshot.getOutputStreamSnapshot();
                    }
                    return outputStreamSnapshot;
                }
            }

            public IScoreSnapshot ScoreSnapshot
            {
                get
                {
                    if (scoreSnapshot == null)
                    {
                        scoreSnapshot = MapScoreSnapshot(robotSnapshot.getScoreSnapshot());
                    }
                    return scoreSnapshot;
                }
            }
        }

        private class BulletSnapshot : IBulletSnapshot
        {
            // The real snapshot
            private readonly robocode.control.snapshot.IBulletSnapshot bulletSnapshot;

            // Cached values created, when needed
            private BulletState? state;
            private double? power;
            private double? x;
            private double? y;
            private double? paintX;
            private double? paintY;
            private int? color;
            private int? frame;
            private bool? isExplosion;
            private int? explosionImageIndex;
            private int? bulletId;

            public BulletSnapshot(robocode.control.snapshot.IBulletSnapshot bulletSnapshot)
            {
                this.bulletSnapshot = bulletSnapshot;
            }

            public BulletState State
            {
                get
                {
                    if (state == null)
                    {
                        state = MapBulletState(bulletSnapshot.getState());
                    }
                    return state.Value;
                }
            }

            public double Power
            {
                get
                {
                    if (power == null)
                    {
                        power = bulletSnapshot.getPower();
                    }
                    return power.Value;
                }
            }

            public double X
            {
                get
                {
                    if (x == null)
                    {
                        x = bulletSnapshot.getX();
                    }
                    return x.Value;
                }
            }

            public double Y
            {
                get
                {
                    if (y == null)
                    {
                        y = bulletSnapshot.getY();
                    }
                    return y.Value;
                }
            }

            public double PaintX
            {
                get
                {
                    if (paintX == null)
                    {
                        paintX = bulletSnapshot.getPaintX();
                    }
                    return paintX.Value;
                }
            }

            public double PaintY
            {
                get
                {
                    if (paintY == null)
                    {
                        paintY = bulletSnapshot.getPaintY();
                    }
                    return paintY.Value;
                }
            }

            public int Color
            {
                get
                {
                    if (color == null)
                    {
                        color = bulletSnapshot.getColor();
                    }
                    return color.Value;
                }
            }

            public int Frame
            {
                get
                {
                    if (frame == null)
                    {
                        frame = bulletSnapshot.getFrame();
                    }
                    return frame.Value;
                }
            }

            public bool IsExplosion
            {
                get
                {
                    if (isExplosion == null)
                    {
                        isExplosion = bulletSnapshot.isExplosion();
                    }
                    return isExplosion.Value;
                }
            }

            public int ExplosionImageIndex
            {
                get
                {
                    if (explosionImageIndex == null)
                    {
                        explosionImageIndex = bulletSnapshot.getExplosionImageIndex();
                    }
                    return explosionImageIndex.Value;
                }
            }

            public int BulletId
            {
                get
                {
                    if (bulletId == null)
                    {
                        bulletId = bulletSnapshot.getBulletId();
                    }
                    return bulletId.Value;
                }
            }
        }

        private class ScoreSnapshot : IScoreSnapshot
        {
            // The real snapshot
            private readonly robocode.control.snapshot.IScoreSnapshot scoreSnapshot;

            // Cached values created, when needed
            private string name;
            private double? totalScore;
            private double? totalSurvivalScore;
            private double? totalLastSurvivorBonus;
            private double? totalBulletDamageScore;
            private double? totalBulletKillBonus;
            private double? totalRammingDamageScore;
            private double? totalRammingKillBonus;
            private int? totalFirsts;
            private int? totalSeconds;
            private int? totalThirds;
            private double? currentScore;
            private double? currentSurvivalScore;
            private double? currentSurvivalBonus;
            private double? currentBulletDamageScore;
            private double? currentBulletKillBonus;
            private double? currentRammingDamageScore;
            private double? currentRammingKillBonus;

            public ScoreSnapshot(robocode.control.snapshot.IScoreSnapshot scoreSnapshot)
            {
                this.scoreSnapshot = scoreSnapshot;
            }

            public int CompareTo(IScoreSnapshot snapshot)
            {
                double myScore = TotalScore + CurrentScore;
                double hisScore = snapshot.TotalScore + snapshot.CurrentScore;

                if (myScore < hisScore)
                {
                    return -1;
                }
                if (myScore > hisScore)
                {
                    return 1;
                }
                return 0;
            }

            public string Name
            {
                get
                {
                    if (name == null)
                    {
                        name = scoreSnapshot.getName();
                    }
                    return name;
                }
            }

            public double TotalScore
            {
                get
                {
                    if (totalScore == null)
                    {
                        totalScore = scoreSnapshot.getTotalScore();
                    }
                    return totalScore.Value;
                }
            }

            public double TotalSurvivalScore
            {
                get
                {
                    if (totalSurvivalScore == null)
                    {
                        totalSurvivalScore = scoreSnapshot.getTotalSurvivalScore();
                    }
                    return totalSurvivalScore.Value;
                }
            }

            public double TotalLastSurvivorBonus
            {
                get
                {
                    if (totalLastSurvivorBonus == null)
                    {
                        totalLastSurvivorBonus = scoreSnapshot.getTotalLastSurvivorBonus();
                    }
                    return totalLastSurvivorBonus.Value;
                }
            }

            public double TotalBulletDamageScore
            {
                get
                {
                    if (totalBulletDamageScore == null)
                    {
                        totalBulletDamageScore = scoreSnapshot.getTotalBulletDamageScore();
                    }
                    return totalBulletDamageScore.Value;
                }
            }

            public double TotalBulletKillBonus
            {
                get
                {
                    if (totalBulletKillBonus == null)
                    {
                        totalBulletKillBonus = scoreSnapshot.getTotalBulletKillBonus();
                    }
                    return totalBulletKillBonus.Value;
                }
            }

            public double TotalRammingDamageScore
            {
                get
                {
                    if (totalRammingDamageScore == null)
                    {
                        totalRammingDamageScore = scoreSnapshot.getTotalRammingDamageScore();
                    }
                    return totalRammingDamageScore.Value;
                }
            }

            public double TotalRammingKillBonus
            {
                get
                {
                    if (totalRammingKillBonus == null)
                    {
                        totalRammingKillBonus = scoreSnapshot.getTotalRammingKillBonus();
                    }
                    return totalRammingKillBonus.Value;
                }
            }

            public int TotalFirsts
            {
                get
                {
                    if (totalFirsts == null)
                    {
                        totalFirsts = scoreSnapshot.getTotalFirsts();
                    }
                    return totalFirsts.Value;
                }
            }

            public int TotalSeconds
            {
                get
                {
                    if (totalSeconds == null)
                    {
                        totalSeconds = scoreSnapshot.getTotalSeconds();
                    }
                    return totalSeconds.Value;
                }
            }

            public int TotalThirds
            {
                get
                {
                    if (totalThirds == null)
                    {
                        totalThirds = scoreSnapshot.getTotalThirds();
                    }
                    return totalThirds.Value;
                }
            }

            public double CurrentScore
            {
                get
                {
                    if (currentScore == null)
                    {
                        currentScore = scoreSnapshot.getCurrentScore();
                    }
                    return currentScore.Value;
                }
            }

            public double CurrentSurvivalScore
            {
                get
                {
                    if (currentSurvivalScore == null)
                    {
                        currentSurvivalScore = scoreSnapshot.getCurrentSurvivalScore();
                    }
                    return currentSurvivalScore.Value;
                }
            }

            public double CurrentSurvivalBonus
            {
                get
                {
                    if (currentSurvivalBonus == null)
                    {
                        currentSurvivalBonus = scoreSnapshot.getCurrentSurvivalBonus();
                    }
                    return currentSurvivalBonus.Value;
                }
            }

            public double CurrentBulletDamageScore
            {
                get
                {
                    if (currentBulletDamageScore == null)
                    {
                        currentBulletDamageScore = scoreSnapshot.getCurrentBulletDamageScore();
                    }
                    return currentBulletDamageScore.Value;
                }
            }

            public double CurrentBulletKillBonus
            {
                get
                {
                    if (currentBulletKillBonus == null)
                    {
                        currentBulletKillBonus = scoreSnapshot.getCurrentBulletKillBonus();
                    }
                    return currentBulletKillBonus.Value;
                }
            }

            public double CurrentRammingDamageScore
            {
                get
                {
                    if (currentRammingDamageScore == null)
                    {
                        currentRammingDamageScore = scoreSnapshot.getCurrentBulletKillBonus();
                    }
                    return currentRammingDamageScore.Value;
                }
            }

            public double CurrentRammingKillBonus
            {
                get
                {
                    if (currentRammingKillBonus == null)
                    {
                        currentRammingKillBonus = scoreSnapshot.getCurrentRammingKillBonus();
                    }
                    return currentRammingKillBonus.Value;
                }
            }
        }

        private class DebugProperty : IDebugProperty
        {
            private readonly string key;
            private readonly string value;

            public DebugProperty(string key, string value)
            {
                this.key = key;
                this.value = value;
            }

            public string Key
            {
                get { return key; }
            }

            public string Value
            {
                get { return value; }
            }
        }

        #endregion

        #region robocode.control.RobotSpecification wrapper

        private class RobotSpecificationWrapper : robocode.control.RobotSpecification
        {
            private readonly string name;
            private readonly string author;
            private readonly string webpage;
            private readonly string version;
            private readonly string robocodeVersion;
            private readonly string archiveFilePath;
            private readonly string fullClassName;
            private readonly string description;
            private readonly string teamId;

            public RobotSpecificationWrapper(string name, string author, string webpage, string version,
                string robocodeVersion, string archiveFilePath, string fullClassName, string description, string teamId)
                : base(null)
            {
                this.name = name;
                this.author = author;
                this.webpage = webpage;
                this.version = version;
                this.robocodeVersion = robocodeVersion;
                this.archiveFilePath = archiveFilePath;
                this.fullClassName = fullClassName;
                this.description = description;
                this.teamId = teamId;
            }

            public override java.lang.String getName()
            {
                return name;
            }

            public override java.lang.String getVersion()
            {
                return version;
            }

            public override java.lang.String getNameAndVersion()
            {
                string nameAndVersion = getName();
                string version = getVersion();

                if (version != null && version.Trim().Length > 0)
                {
                    nameAndVersion += ' ' + version.Trim();
                }
                return nameAndVersion;
            }

            public override java.lang.String getClassName()
            {
                return fullClassName;
            }

            public override java.io.File getJarFile()
            {
                return new java.io.File(archiveFilePath);
            }

            public override java.lang.String getDescription()
            {
                return description;
            }

            public override java.lang.String getRobocodeVersion()
            {
                return robocodeVersion;
            }

            public override java.lang.String getWebpage()
            {
                return webpage;
            }

            public override java.lang.String getAuthorName()
            {
                return author;
            }

            public override java.lang.String getTeamId()
            {
                return teamId;
            }
        }

        #endregion
    }
}
