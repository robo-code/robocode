/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Threading;
using net.sf.jni4net.nio;
using net.sf.robocode.dotnet.host.events;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using Robocode;
using robocode.exception;
using Robocode.Exception;
using Robocode.RobotInterfaces.Peer;
using Robocode.Util;
using ByteBuffer = net.sf.robocode.nio.ByteBuffer;
using Thread = System.Threading.Thread;

namespace net.sf.robocode.dotnet.host.proxies
{
    public class BasicRobotProxy : HostingRobotProxy, IBasicRobotPeer
    {
        private const long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

        private GraphicsProxy graphicsProxy;

        private RobotStatus status;
        private bool isDisabled;
        protected ExecCommands commands;
        private ExecResults execResults;

        private readonly Dictionary<int, Bullet> bullets = new Dictionary<int, Bullet>();
        private int bulletCounter;

        private int setCallCount;
        private int getCallCount;

        protected Condition waitCondition;
        private bool testingCondition;
        private double firedEnergy;
        private double firedHeat;

        private readonly DirectByteBuffer execJavaBuffer;
        private readonly ByteBuffer execNetBuffer;
        private readonly RbSerializerN rbSerializerN;

        public BasicRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
            eventManager = new EventManager(this);

            graphicsProxy = new GraphicsProxy();

            // dummy
            execResults = new ExecResults(null, null, null, null, null, false, false, false);

            setSetCallCount(0);
            setGetCallCount(0);

            var sharedBuffer = new byte[10*1024*100];
            execJavaBuffer = new DirectByteBuffer(sharedBuffer);
            execNetBuffer = ByteBuffer.wrap(sharedBuffer);
            rbSerializerN = new RbSerializerN();
            this.peer.setupBuffer(execJavaBuffer);
        }

        // asynchronous actions

        #region IBasicRobotPeer Members

        public Bullet SetFire(double power)
        {
            SetCall();
            return setFireImpl(power);
        }

        // blocking actions
        public void Execute()
        {
            executeImpl();
        }

        public void Move(double distance)
        {
            setMoveImpl(distance);
            do
            {
                Execute(); // Always tick at least once
            } while (GetDistanceRemaining() != 0);
        }

        public void TurnBody(double radians)
        {
            setTurnBodyImpl(radians);
            do
            {
                Execute(); // Always tick at least once
            } while (GetBodyTurnRemaining() != 0);
        }

        public void TurnGun(double radians)
        {
            setTurnGunImpl(radians);
            do
            {
                Execute(); // Always tick at least once
            } while (GetGunTurnRemaining() != 0);
        }

        public Bullet Fire(double power)
        {
            Bullet bullet = SetFire(power);

            Execute();
            return bullet;
        }

        // fast setters
        public void SetBodyColor(Color color)
        {
            SetCall();
            commands.setBodyColor(color.ToArgb());
        }

        public Color GetBodyColor()
        {
            GetCall();
            return Color.FromArgb(commands.getBodyColor());
        }

        public void SetGunColor(Color color)
        {
            SetCall();
            commands.setGunColor(color.ToArgb());
        }

        public Color GetGunColor()
        {
            GetCall();
            return Color.FromArgb(commands.getGunColor());
        }

        public void SetRadarColor(Color color)
        {
            SetCall();
            commands.setRadarColor(color.ToArgb());
        }

        public Color GetRadarColor()
        {
            GetCall();
            return Color.FromArgb(commands.getRadarColor());
        }

        public void SetBulletColor(Color color)
        {
            SetCall();
            commands.setBulletColor(color.ToArgb());
        }

        public Color GetBulletColor()
        {
            GetCall();
            return Color.FromArgb(commands.getBulletColor());
        }

        public void SetScanColor(Color color)
        {
            SetCall();
            commands.setScanColor(color.ToArgb());
        }

        public Color GetScanColor()
        {
            GetCall();
            return Color.FromArgb(commands.getScanColor());
        }

        // counters
        public void SetCall()
        {
            if (!isDisabled)
            {
                int res = Interlocked.Increment(ref setCallCount);

                if (res >= MAX_SET_CALL_COUNT)
                {
                    isDisabled = true;
                    println("SYSTEM: You have made " + res + " calls to setXX methods without calling Execute()");
                    throw new DisabledException("Too many calls to setXX methods");
                }
            }
        }

        public void GetCall()
        {
            if (!isDisabled)
            {
                int res = Interlocked.Increment(ref getCallCount);

                if (res >= MAX_GET_CALL_COUNT)
                {
                    isDisabled = true;
                    println("SYSTEM: You have made " + res + " calls to getXX methods without calling Execute()");
                    throw new DisabledException("Too many calls to getXX methods");
                }
            }
        }

        public double GetDistanceRemaining()
        {
            GetCall();
            return commands.getDistanceRemaining();
        }

        public double GetRadarTurnRemaining()
        {
            GetCall();
            return commands.getRadarTurnRemaining();
        }

        public double GetBodyTurnRemaining()
        {
            GetCall();
            return commands.getBodyTurnRemaining();
        }

        public double GetGunTurnRemaining()
        {
            GetCall();
            return commands.getGunTurnRemaining();
        }

        public double GetVelocity()
        {
            GetCall();
            return status.Velocity;
        }

        public double GetGunCoolingRate()
        {
            GetCall();
            return statics.getBattleRules().GunCoolingRate;
        }

        public String GetName()
        {
            GetCall();
            return statics.getName();
        }

        public long GetTime()
        {
            GetCall();
            return getTimeImpl();
        }

        public double GetBodyHeading()
        {
            GetCall();
            return status.HeadingRadians;
        }

        public double GetGunHeading()
        {
            GetCall();
            return status.GunHeadingRadians;
        }

        public double GetRadarHeading()
        {
            GetCall();
            return status.RadarHeadingRadians;
        }

        public double GetEnergy()
        {
            GetCall();
            return getEnergyImpl();
        }

        public double GetGunHeat()
        {
            GetCall();
            return getGunHeatImpl();
        }

        public double GetX()
        {
            GetCall();
            return status.X;
        }

        public double GetY()
        {
            GetCall();
            return status.Y;
        }

        public int GetOthers()
        {
            GetCall();
            return status.Others;
        }

        public int GetNumSentries()
        {
            GetCall();
            return status.NumSentries;
        }

        public double GetBattleFieldHeight()
        {
            GetCall();
            return statics.getBattleRules().BattlefieldHeight;
        }

        public double GetBattleFieldWidth()
        {
            GetCall();
            return statics.getBattleRules().BattlefieldWidth;
        }

        public int GetNumRounds()
        {
            GetCall();
            return statics.getBattleRules().NumRounds;
        }

        public int GetRoundNum()
        {
            GetCall();
            return status.RoundNum;
        }

        public int GetSentryBorderSize()
        {
            GetCall();
            return statics.getBattleRules().SentryBorderSize;
        }

        public IGraphics GetGraphics()
        {
            GetCall();
            commands.setTryingToPaint(true);
            return getGraphicsImpl();
        }

        public void SetDebugProperty(String key, String value)
        {
            SetCall();
            commands.setDebugProperty(key, value);
        }

        public void Rescan()
        {
            bool reset = false;
            bool origInterruptableValue = false;

            if (eventManager.CurrentTopEventPriority == eventManager.ScannedRobotEventPriority)
            {
                reset = true;
                origInterruptableValue = eventManager.IsInterruptible(eventManager.ScannedRobotEventPriority);
                eventManager.SetInterruptible(eventManager.ScannedRobotEventPriority, true);
            }

            commands.setScan(true);
            executeImpl();

            if (reset)
            {
                eventManager.SetInterruptible(eventManager.ScannedRobotEventPriority, origInterruptableValue);
            }
        }

        #endregion

        internal override void initializeRound(ExecCommands commands, RobotStatus status)
        {
            updateStatus(commands, status);
            eventManager.Reset();
            var start = new StatusEvent(status);

            eventManager.Add(start);
            setSetCallCount(0);
            setGetCallCount(0);
        }

        public override void cleanup()
        {
            base.cleanup();

            // Cleanup and remove current wait condition
            waitCondition = null;

            // Cleanup and remove the event manager
            if (eventManager != null)
            {
                eventManager.Cleanup();
                eventManager = null;
            }

            graphicsProxy = null;
            execResults = null;
            status = null;
            commands = null;
        }

        // -----------
        // implementations
        // -----------

        public long getTimeImpl()
        {
            return status.Time;
        }

        public IGraphics getGraphicsImpl()
        {
            return graphicsProxy;
        }

        protected override sealed void executeImpl()
        {
            if (execResults == null)
            {
                // this is to slow down undead robot after cleanup, from fast exception-loop
                Thread.Sleep(1000);
            }

            // Entering tick
            if (testingCondition)
            {
                throw new RobotException(
                    "You cannot take action inside Condition.Test().  You should handle OnCustomEvent instead.");
            }

            setSetCallCount(0);
            setGetCallCount(0);

            // This stops autoscan from scanning...
            if (waitCondition != null && waitCondition.Test())
            {
                waitCondition = null;
                commands.setScan(true);
            }

            commands.setOutputText(GetOutTextAndReset());
            commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

            // call server
            SerializeCommands();
            peer.executeImplSerial();
            DeserializeResults();

            if (execResults == null)
            {
                throw new InvalidOperationException();
            }

            updateStatus(execResults.getCommands(), execResults.getStatus());

            graphicsProxy.setPaintingEnabled(execResults.isPaintEnabled());
            firedEnergy = 0;
            firedHeat = 0;

            // add new events
            eventManager.Add(new StatusEvent(execResults.getStatus()));
            if (statics.IsPaintRobot() && execResults.isPaintEnabled())
            {
                // Add paint event, if robot is a paint robot and its painting is enabled
                eventManager.Add(new PaintEvent());
            }

            // add other events
            if (execResults.getEvents() != null)
            {
                foreach (Event evnt in execResults.getEvents())
                {
                    eventManager.Add(evnt);
                    HiddenAccessN.UpdateBullets(evnt, bullets);
                }
            }

            if (execResults.getBulletUpdates() != null)
            {
                foreach (BulletStatus bulletStatus in execResults.getBulletUpdates())
                {
                    Bullet bullet;
                    if (bullets.TryGetValue(bulletStatus.bulletId, out bullet))
                    {
                        HiddenAccessN.Update(bullet, bulletStatus.x, bulletStatus.y, bulletStatus.victimName, bulletStatus.isActive);
                        if (!bulletStatus.isActive)
                        {
                            bullets.Remove(bulletStatus.bulletId);
                        }
                    }
                }
            }

            // add new team messages
            loadTeamMessages(execResults.getTeamMessages());

            eventManager.ProcessEvents();
        }

        private string GetOutTextAndReset()
        {
            string res;
            lock (output)
            {
                output.Flush();
                res = outputSb.ToString();
                outputSb.Length = 0;
            }
            return res;
        }


        private void SerializeCommands()
        {
            execNetBuffer.clear();
            rbSerializerN.serializeToBuffer(execNetBuffer, RbSerializerN.ExecCommands_TYPE, commands);
            execJavaBuffer.Position(0);
            execJavaBuffer.Limit(execNetBuffer.limit());
        }

        private void DeserializeResults()
        {
            execNetBuffer.position(0);
            execNetBuffer.limit(execJavaBuffer.limit());
            execResults = (ExecResults) rbSerializerN.deserialize(execNetBuffer);
        }

        protected override sealed void waitForBattleEndImpl()
        {
            eventManager.ClearAllEvents(false);
            graphicsProxy.setPaintingEnabled(false);
            do
            {
                commands.setOutputText(GetOutTextAndReset());
                commands.setGraphicsCalls(graphicsProxy.readoutQueuedCalls());

                // call server
                try
                {
                    SerializeCommands();
                    peer.waitForBattleEndImplSerial();
                    DeserializeResults();
                }
                catch (Exception ex)
                {
                    LoggerN.logMessage(statics.getName() + ": Exception: " + ex); // without stack here
                    return;
                }

                updateStatus(execResults.getCommands(), execResults.getStatus());

                // add new events
                if (execResults.getEvents() != null)
                {
                    foreach (Event evnt in execResults.getEvents())
                    {
                        if (evnt is BattleEndedEvent)
                        {
                            eventManager.Add(evnt);
                        }
                    }
                }
                eventManager.ResetCustomEvents();

                eventManager.ProcessEvents();

            } while (!execResults.isHalt() && execResults.isShouldWait());
        }

        private void updateStatus(ExecCommands commands, RobotStatus status)
        {
            this.status = status;
            this.commands = commands;
        }

        protected virtual void loadTeamMessages(List<TeamMessage> teamMessages)
        {
        }

        protected double getEnergyImpl()
        {
            return status.Energy - firedEnergy;
        }

        protected double getGunHeatImpl()
        {
            return status.GunHeat + firedHeat;
        }

        protected void setMoveImpl(double distance)
        {
            if (getEnergyImpl() == 0)
            {
                return;
            }
            commands.setDistanceRemaining(distance);
            commands.setMoved(true);
        }

        protected Bullet setFireImpl(double power)
        {
            if (Double.IsNaN(power))
            {
                println("SYSTEM: You cannot call Fire(NaN)");
                return null;
            }
            if (getGunHeatImpl() > 0 || getEnergyImpl() == 0)
            {
                return null;
            }

            power = Math.Min(getEnergyImpl(), Math.Min(Math.Max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

            Bullet bullet;
            BulletCommand wrapper;
            Event currentTopEvent = eventManager.CurrentTopEvent;

            bulletCounter++;

            if (currentTopEvent != null && currentTopEvent.Time == status.Time && !statics.IsAdvancedRobot()
                && status.GunHeadingRadians == status.RadarHeadingRadians
                && typeof (ScannedRobotEvent).IsAssignableFrom(currentTopEvent.GetType()))
            {
                // this is angle assisted bullet
                var e = (ScannedRobotEvent) currentTopEvent;
                double fireAssistAngle = Utils.NormalAbsoluteAngle(status.HeadingRadians + e.BearingRadians);

                bullet = new Bullet(fireAssistAngle, GetX(), GetY(), power, statics.getName(), null, true, bulletCounter);
                wrapper = new BulletCommand(power, true, fireAssistAngle, bulletCounter);
            }
            else
            {
                // this is normal bullet
                bullet = new Bullet(status.GunHeadingRadians, GetX(), GetY(), power, statics.getName(), null, true,
                                    bulletCounter);
                wrapper = new BulletCommand(power, false, 0, bulletCounter);
            }

            firedEnergy += power;
            firedHeat += Rules.GetGunHeat(power);

            commands.getBullets().Add(wrapper);

            bullets.Add(bulletCounter, bullet);

            return bullet;
        }

        protected void setTurnGunImpl(double radians)
        {
            commands.setGunTurnRemaining(radians);
        }

        protected void setTurnBodyImpl(double radians)
        {
            if (getEnergyImpl() > 0)
            {
                commands.setBodyTurnRemaining(radians);
            }
        }

        protected void setTurnRadarImpl(double radians)
        {
            commands.setRadarTurnRemaining(radians);
        }

        // -----------
        // battle driven methods
        // -----------

        private void setSetCallCount(int setCallCount)
        {
            this.setCallCount = setCallCount;
        }

        private void setGetCallCount(int getCallCount)
        {
            this.getCallCount = getCallCount;
        }

        // -----------
        // for robot thread
        // -----------

        public void setTestingCondition(bool testingCondition)
        {
            this.testingCondition = testingCondition;
        }


        public override String ToString()
        {
            return statics.getShortName() + "(" + (int) status.Energy + ") X" + (int) status.X + " Y"
                   + (int) status.Y;
        }
    }
}
