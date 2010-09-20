//
// TODO:
// =====
// - Results must be sorted. Some .NET Comparator must be implemented for sorting an array of BattleResult instances.
//


#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using Robocode.Control.Events;
using Robocode.Control.Snapshot;
using net.sf.robocode.security;

namespace Robocode.Control
{
    /**
     * The RobocodeEngine is the interface provided for external applications
     * in order to let these applications run battles within the Robocode application,
     * and to get the results from these battles.
     * </p>
     * This class in the main class of the {@code robocode.control} package, and the
     * reason for having this control package.
     * </p>
     * The RobocodeEngine is used by RoboRumble@Home, which is integrated in
     * Robocode, but also RoboLeague and RobocodeJGAP. In addition, the
     * RobocodeEngine is also used by the test units for testing the Robocode
     * application itself.
     *
     * @author Mathew A. Nelson (original)
     * @author Flemming N. Larsen (contributor)
     * @author Robert D. Maupin (contributor)
     * @author Nathaniel Troutman (contributor)
     * @author Joachim Hofer (contributor)
     * @author Pavel Savara (contributor)
     */
    public class RobocodeEngine : IRobocodeEngine
    {
        public event BattleStartedEventHandler BattleStarted;
        public event BattleFinishedEventHandler BattleFinished;
        public event BattleCompletedEventHandler BattleCompleted;
        public event BattlePausedEventHandler BattlePaused;
        public event BattleResumedEventHandler BattleResumed;
        public event RoundStartedEventHandler RoundStarted;
        public event RoundEndedEventHandler RoundEnded;
        public event TurnStartedEventHandler TurnStarted;
        public event TurnEndedEventHandler TurnEnded;
        public event BattleMessageEventHandler BattleMessage;
        public event BattleErrorEventHandler BattleError;

        private robocode.control.RobocodeEngine engine;
        private BattleObserver battleObserver;
        private bool isVisible;

        #region Public API

        /**
	     * Creates a new RobocodeEngine for controlling Robocode. The JAR file of
	     * Robocode is used to determine the root directory of Robocode.
	     *
	     * @see #RobocodeEngine(File)
	     * @see #close()
	     * @since 1.6.2
	     */
	    public RobocodeEngine() :
            this(AppDomain.CurrentDomain.BaseDirectory)
        {
	    }

        /**
         * Creates a new RobocodeEngine for controlling Robocode.
         *
         * @param robocodeHome the root directory of Robocode, e.g. C:\Robocode.
         * @see #RobocodeEngine()
         * @see #close()
         * @since 1.6.2
         */
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

        /**
         * Closes the RobocodeEngine and releases any allocated resources.
         * You should call this when you have finished using the RobocodeEngine.
         * This method automatically disposes the Robocode window if it open.
         */
        public void Close()
        {
            engine.close();
        }

        /**
         * Returns the installed version of Robocode.
         *
         * @return the installed version of Robocode.
         */
        public string Version
        {
            get { return engine.getVersion(); }
        }

        /**
         * Returns the current working directory.
         *
         * @return a File for the current working directory.
         *
         * @since 1.7.1
         */
        public static string CurrentWorkingDir
        {
            get { return robocode.control.RobocodeEngine.getCurrentWorkingDir().getAbsolutePath(); }
        }

        /**
         * Returns the directory containing the robots.
         *
         * @return a File that is the directory containing the robots.
         *
         * @since 1.7.1
         */
        public static string RobotsDir
        {
            get { return robocode.control.RobocodeEngine.getRobotsDir().getAbsolutePath(); }
        }

        /**
         * Shows or hides the Robocode window.
         *
         * @param visible {@code true} if the Robocode window must be set visible;
         *                {@code false} otherwise.
         */
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

        /**
         * Returns all robots available from the local robot repository of Robocode.
         * These robots must exists in the /robocode/robots directory, and must be
         * compiled in advance.
         *
         * @return an array of all available robots from the local robot repository.
         * @see RobotSpecification
         * @see #getLocalRepository(String)
         */
        public RobotSpecification[] GetLocalRepository()
        {
            return MapRobotSpecifications(engine.getLocalRepository());
        }

        /**
         * Returns a selection of robots available from the local robot repository
         * of Robocode. These robots must exists in the /robocode/robots directory,
         * and must be compiled in advance.
         * </p>
         * Notice: If a specified robot cannot be found in the repository, it will
         * not be returned in the array of robots returned by this method.
         *
         * @param selectedRobots a comma or space separated list of robots to
         *                          return. The full class name must be used for
         *                          specifying the individual robot, e.g.
         *                          "sample.Corners, sample.Crazy"
         * @return an array containing the available robots from the local robot
         *         repository based on the selected robots specified with the
         *         {@code selectedRobotList} parameter.
         * @see RobotSpecification
         * @see #getLocalRepository()
         * @since 1.6.2
         */
        public RobotSpecification[] GetLocalRepository(string selectedRobots)
        {
            return MapRobotSpecifications(engine.getLocalRepository(selectedRobots));
        }

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification the specification of the battle to play including the
         *                            participation robots.
         * @see #runBattle(BattleSpecification, boolean)
         * @see BattleSpecification
         * @see #getLocalRepository()
         */
        public void RunBattle(BattleSpecification battleSpecification)
        {
            RunBattle(battleSpecification, null, false);
        }

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification	   the specification of the battle to run including the
         *                     participating robots.
         * @param waitTillOver will block caller till end of battle if set
         * @see #runBattle(BattleSpecification)
         * @see BattleSpecification
         * @see #getLocalRepository()
         * @since 1.6.2
         */
        public void RunBattle(BattleSpecification battleSpecification, bool waitTillOver)
        {
            RunBattle(battleSpecification, "", waitTillOver);
        }

        /**
         * Runs the specified battle.
         *
         * @param battleSpecification the specification of the battle to run including the
         *                     participating robots.
         * @param initialPositions a comma or space separated list like: x1,y1,heading1,
         *        x2,y2,heading2, which are the coordinates and heading of robot #1 and #2.
         *        So e.g. (0,0,180), (50,80,270) means that robot #1 has position (0,0) and
         *        heading 180, and robot #2 has position (50,80) and heading 270.
         * @param waitTillOver will block caller till end of battle if set
         * @see #runBattle(BattleSpecification)
         * @see BattleSpecification
         * @see #getLocalRepository()
         * @since 1.7.1.2
         */
        public void RunBattle(BattleSpecification battleSpecification, string initialPositions, bool waitTillOver)
        {
            engine.runBattle(MapBattleSpecification(battleSpecification), initialPositions, waitTillOver);
        }

        /**
         * Will block caller until current battle is over 
         * @see #runBattle(BattleSpecification)
         * @see #runBattle(BattleSpecification, boolean)
         * @since 1.6.2
         */
        public void WaitTillBattleOver()
        {
            engine.waitTillBattleOver();
        }

        /**
         * Aborts the current battle if it is running.
         *
         * @see #runBattle(BattleSpecification)
         */
        public void AbortCurrentBattle()
        {
            engine.abortCurrentBattle();
        }

        /**
         * Print out all running threads to standard system out.
         *
         * @since 1.6.2
         */
	    public static void PrintRunningThreads()
        {
            robocode.control.RobocodeEngine.printRunningThreads();
        }

        #endregion

        #region Event dispatchers

        protected void OnBattleStarted(BattleStartedEvent e)
        {
            if (BattleStarted != null)
                BattleStarted(e);
        }

        protected void OnBattleFinished(BattleFinishedEvent e)
        {
            if (BattleFinished != null)
                BattleFinished(e);
        }

        protected void OnBattleCompleted(BattleCompletedEvent e)
        {
            if (BattleCompleted != null)
                BattleCompleted(e);
        }

        protected void OnBattlePaused(BattlePausedEvent e)
        {
            if (BattlePaused != null)
                BattlePaused(e);
        }

        protected void OnBattleResumed(BattleResumedEvent e)
        {
            if (BattleResumed != null)
                BattleResumed(e);
        }

        protected void OnRoundStarted(RoundStartedEvent e)
        {
            if (RoundStarted != null)
                RoundStarted(e);
        }

        protected void OnRoundEnded(Robocode.Control.Events.RoundEndedEvent e)
        {
            if (RoundEnded != null)
                RoundEnded(e);
        }

        protected void OnTurnStarted(TurnStartedEvent e)
        {
            if (TurnStarted != null)
                TurnStarted(e);
        }

        protected void OnTurnEnded(TurnEndedEvent e)
        {
            if (TurnEnded != null)
                TurnEnded(e);
        }

        protected void OnBattleMessage(BattleMessageEvent e)
        {
            if (BattleMessage != null)
                BattleMessage(e);
        }

        protected void OnBattleError(BattleErrorEvent e)
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

        private robocode.control.BattleSpecification MapBattleSpecification(Robocode.Control.BattleSpecification spec)
        {
            robocode.control.BattlefieldSpecification battleField = new robocode.control.BattlefieldSpecification(spec.Battlefield.Width, spec.Battlefield.Height);
            robocode.control.RobotSpecification[] robots = MapRobotSpecifications(spec.Robots);

            return new robocode.control.BattleSpecification(spec.NumRounds, spec.InactivityTime, spec.GunCoolingRate, battleField, robots);
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

            MethodInfo method = typeof(BattleRules).GetMethod("createHiddenHelper", BindingFlags.Static | BindingFlags.NonPublic);
            IHiddenRulesHelper rulesHelper = (IHiddenRulesHelper)method.Invoke(null, null);

            return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime);
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
            private int? contestantIndex;
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

            public int ContestantIndex
            {
                get
                {
                    if (contestantIndex == null)
                    {
                        contestantIndex = robotSnapshot.getContestantIndex();
                    }
                    return contestantIndex.Value;
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
