/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.IO;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using Robocode;
using Robocode.RobotInterfaces.Peer;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class AdvancedRobotProxy : StandardRobotProxy, IAdvancedRobotPeer
    {
        public AdvancedRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }

        // asynchronous actions

        #region IAdvancedRobotPeer Members

        public void SetResume()
        {
            SetCall();
            setResumeImpl();
        }

        public void SetStop(bool overwrite)
        {
            SetCall();
            setStopImpl(overwrite);
        }

        public void SetMove(double distance)
        {
            SetCall();
            setMoveImpl(distance);
        }

        public void SetTurnBody(double radians)
        {
            SetCall();
            setTurnBodyImpl(radians);
        }

        public void SetTurnGun(double radians)
        {
            SetCall();
            setTurnGunImpl(radians);
        }

        public void SetTurnRadar(double radians)
        {
            SetCall();
            setTurnRadarImpl(radians);
        }

        // blocking actions
        public void WaitFor(Condition condition)
        {
            waitCondition = condition;
            do
            {
                Execute(); // Always tick at least once
            } while (!condition.Test());

            waitCondition = null;
        }

        // fast setters
        public void SetMaxTurnRate(double newTurnRate)
        {
            SetCall();
            if (Double.IsNaN(newTurnRate))
            {
                println("You cannot SetMaxTurnRate to: " + newTurnRate);
                return;
            }
            commands.setMaxTurnRate(newTurnRate);
        }

        public void SetMaxVelocity(double newVelocity)
        {
            SetCall();
            if (Double.IsNaN(newVelocity))
            {
                println("You cannot SetMaxVelocity to: " + newVelocity);
                return;
            }
            commands.setMaxVelocity(newVelocity);
        }

        // events manipulation
        public void SetInterruptible(bool interruptable)
        {
            SetCall();
            eventManager.SetInterruptible(eventManager.CurrentTopEventPriority, interruptable);
        }

        public void SetEventPriority(String eventClass, int priority)
        {
            SetCall();
            eventManager.SetEventPriority(eventClass, priority);
        }

        public int GetEventPriority(String eventClass)
        {
            GetCall();
            return eventManager.GetEventPriority(eventClass);
        }

        public void RemoveCustomEvent(Condition condition)
        {
            SetCall();
            eventManager.RemoveCustomEvent(condition);
        }

        public void AddCustomEvent(Condition condition)
        {
            SetCall();
            eventManager.AddCustomEvent(condition);
        }

        public void clearAllEvents()
        {
            SetCall();
            eventManager.ClearAllEvents(false);
        }

        public IList<Event> GetAllEvents()
        {
            GetCall();
            return eventManager.GetAllEvents();
        }

        public IList<StatusEvent> GetStatusEvents()
        {
            GetCall();
            return eventManager.GetStatusEvents();
        }

        public IList<BulletMissedEvent> GetBulletMissedEvents()
        {
            GetCall();
            return eventManager.GetBulletMissedEvents();
        }

        public IList<BulletHitBulletEvent> GetBulletHitBulletEvents()
        {
            GetCall();
            return eventManager.GetBulletHitBulletEvents();
        }

        public IList<BulletHitEvent> GetBulletHitEvents()
        {
            GetCall();
            return eventManager.GetBulletHitEvents();
        }

        public IList<HitByBulletEvent> GetHitByBulletEvents()
        {
            GetCall();
            return eventManager.GetHitByBulletEvents();
        }

        public IList<HitRobotEvent> GetHitRobotEvents()
        {
            GetCall();
            return eventManager.GetHitRobotEvents();
        }

        public IList<HitWallEvent> GetHitWallEvents()
        {
            GetCall();
            return eventManager.GetHitWallEvents();
        }

        public IList<RobotDeathEvent> GetRobotDeathEvents()
        {
            GetCall();
            return eventManager.GetRobotDeathEvents();
        }

        public IList<ScannedRobotEvent> GetScannedRobotEvents()
        {
            GetCall();
            return eventManager.GetScannedRobotEvents();
        }

        // data
        public string GetDataDirectory()
        {
            GetCall();
            commands.setIORobot();
            return robotFileSystemManager.getWritableDirectory();
        }

        public Stream GetDataFile(string filename)
        {
            GetCall();
            commands.setIORobot();
            if (filename.Contains(".."))
            {
                throw new AccessViolationException("no relative path allowed");
            }

            return robotFileSystemManager.getDataFile(filename);
        }

        public long GetDataQuotaAvailable()
        {
            GetCall();
            return robotFileSystemManager.getDataQuotaAvailable();
        }

        #endregion
    }
}
