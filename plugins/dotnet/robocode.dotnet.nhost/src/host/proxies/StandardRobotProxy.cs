#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using Robocode;
using Robocode.RobotInterfaces.Peer;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class StandardRobotProxy : BasicRobotProxy, IStandardRobotPeer
    {
        private bool isStopped;
        private double saveAngleToTurn;
        private double saveDistanceToGo;
        private double saveGunAngleToTurn;
        private double saveRadarAngleToTurn;

        public StandardRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }


        // blocking actions

        #region IStandardRobotPeer Members

        public void stop(bool overwrite)
        {
            setStopImpl(overwrite);
            execute();
        }

        public void resume()
        {
            setResumeImpl();
            execute();
        }

        public void turnRadar(double radians)
        {
            setTurnRadarImpl(radians);
            do
            {
                execute(); // Always tick at least once
            } while (getRadarTurnRemaining() != 0);
        }

        // fast setters
        public void setAdjustGunForBodyTurn(bool newAdjustGunForBodyTurn)
        {
            setCall();
            commands.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
        }

        public void setAdjustRadarForGunTurn(bool newAdjustRadarForGunTurn)
        {
            setCall();
            commands.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
            if (!commands.IsAdjustRadarForBodyTurnSet())
            {
                commands.setAdjustRadarForBodyTurn(newAdjustRadarForGunTurn);
            }
        }

        public void setAdjustRadarForBodyTurn(bool newAdjustRadarForBodyTurn)
        {
            setCall();
            commands.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
            commands.setAdjustRadarForBodyTurnSet(true);
        }

        public bool isAdjustGunForBodyTurn()
        {
            getCall();
            return commands.IsAdjustGunForBodyTurn();
        }

        public bool isAdjustRadarForGunTurn()
        {
            getCall();
            return commands.IsAdjustRadarForGunTurn();
        }

        public bool isAdjustRadarForBodyTurn()
        {
            getCall();
            return commands.IsAdjustRadarForBodyTurn();
        }

        #endregion

        internal override void initializeRound(ExecCommands commands, RobotStatus status)
        {
            base.initializeRound(commands, status);
            isStopped = true;
        }

        protected void setResumeImpl()
        {
            if (isStopped)
            {
                isStopped = false;
                commands.setDistanceRemaining(saveDistanceToGo);
                commands.setBodyTurnRemaining(saveAngleToTurn);
                commands.setGunTurnRemaining(saveGunAngleToTurn);
                commands.setRadarTurnRemaining(saveRadarAngleToTurn);
            }
        }

        protected void setStopImpl(bool overwrite)
        {
            if (!isStopped || overwrite)
            {
                saveDistanceToGo = getDistanceRemaining();
                saveAngleToTurn = getBodyTurnRemaining();
                saveGunAngleToTurn = getGunTurnRemaining();
                saveRadarAngleToTurn = getRadarTurnRemaining();
            }
            isStopped = true;

            commands.setDistanceRemaining(0);
            commands.setBodyTurnRemaining(0);
            commands.setGunTurnRemaining(0);
            commands.setRadarTurnRemaining(0);
        }
    }
}