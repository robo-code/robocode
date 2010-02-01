#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

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
        public AdvancedRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }

        // asynchronous actions

        #region IAdvancedRobotPeer Members

        public void setResume()
        {
            setCall();
            setResumeImpl();
        }

        public void setStop(bool overwrite)
        {
            setCall();
            setStopImpl(overwrite);
        }

        public void setMove(double distance)
        {
            setCall();
            setMoveImpl(distance);
        }

        public void setTurnBody(double radians)
        {
            setCall();
            setTurnBodyImpl(radians);
        }

        public void setTurnGun(double radians)
        {
            setCall();
            setTurnGunImpl(radians);
        }

        public void setTurnRadar(double radians)
        {
            setCall();
            setTurnRadarImpl(radians);
        }

        // blocking actions
        public void waitFor(Condition condition)
        {
            waitCondition = condition;
            do
            {
                execute(); // Always tick at least once
            } while (!condition.Test());

            waitCondition = null;
        }

        // fast setters
        public void setMaxTurnRate(double newTurnRate)
        {
            setCall();
            if (Double.IsNaN(newTurnRate))
            {
                println("You cannot setMaxTurnRate to: " + newTurnRate);
                return;
            }
            commands.setMaxTurnRate(newTurnRate);
        }

        public void setMaxVelocity(double newVelocity)
        {
            setCall();
            if (Double.IsNaN(newVelocity))
            {
                println("You cannot setMaxVelocity to: " + newVelocity);
                return;
            }
            commands.setMaxVelocity(newVelocity);
        }

        // events manipulation
        public void setInterruptible(bool interruptable)
        {
            setCall();
            eventManager.setInterruptible(eventManager.getCurrentTopEventPriority(), interruptable);
        }

        public void setEventPriority(String eventClass, int priority)
        {
            setCall();
            eventManager.setEventPriority(eventClass, priority);
        }

        public int getEventPriority(String eventClass)
        {
            getCall();
            return eventManager.getEventPriority(eventClass);
        }

        public void removeCustomEvent(Condition condition)
        {
            setCall();
            eventManager.removeCustomEvent(condition);
        }

        public void addCustomEvent(Condition condition)
        {
            setCall();
            eventManager.addCustomEvent(condition);
        }

        public void clearAllEvents()
        {
            setCall();
            eventManager.clearAllEvents(false);
        }

        public IList<Event> getAllEvents()
        {
            getCall();
            return eventManager.getAllEvents();
        }

        public IList<StatusEvent> getStatusEvents()
        {
            getCall();
            return eventManager.getStatusEvents();
        }

        public IList<BulletMissedEvent> getBulletMissedEvents()
        {
            getCall();
            return eventManager.getBulletMissedEvents();
        }

        public IList<BulletHitBulletEvent> getBulletHitBulletEvents()
        {
            getCall();
            return eventManager.getBulletHitBulletEvents();
        }

        public IList<BulletHitEvent> getBulletHitEvents()
        {
            getCall();
            return eventManager.getBulletHitEvents();
        }

        public IList<HitByBulletEvent> getHitByBulletEvents()
        {
            getCall();
            return eventManager.getHitByBulletEvents();
        }

        public IList<HitRobotEvent> getHitRobotEvents()
        {
            getCall();
            return eventManager.getHitRobotEvents();
        }

        public IList<HitWallEvent> getHitWallEvents()
        {
            getCall();
            return eventManager.getHitWallEvents();
        }

        public IList<RobotDeathEvent> getRobotDeathEvents()
        {
            getCall();
            return eventManager.getRobotDeathEvents();
        }

        public IList<ScannedRobotEvent> getScannedRobotEvents()
        {
            getCall();
            return eventManager.getScannedRobotEvents();
        }

        // data
        public string getDataDirectory()
        {
            getCall();
            commands.setIORobot();
            return robotFileSystemManager.getWritableDirectory();
        }

        public Stream getDataFile(string filename)
        {
            getCall();
            commands.setIORobot();
            if (filename.Contains(".."))
            {
                throw new AccessViolationException("no relative path allowed");
            }

            return robotFileSystemManager.getDataFile(filename);
        }

        public long getDataQuotaAvailable()
        {
            getCall();
            return robotFileSystemManager.getDataQuotaAvailable();
        }

        #endregion
    }
}