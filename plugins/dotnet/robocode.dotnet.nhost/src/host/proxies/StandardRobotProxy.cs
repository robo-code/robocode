/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

        public StandardRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }


        // blocking actions

        #region IStandardRobotPeer Members

        public void Stop(bool overwrite)
        {
            setStopImpl(overwrite);
            Execute();
        }

        public void Resume()
        {
            setResumeImpl();
            Execute();
        }

        public void TurnRadar(double radians)
        {
            setTurnRadarImpl(radians);
            do
            {
                Execute(); // Always tick at least once
            } while (GetRadarTurnRemaining() != 0);
        }

        // fast setters
        public void SetAdjustGunForBodyTurn(bool newAdjustGunForBodyTurn)
        {
            SetCall();
            commands.setAdjustGunForBodyTurn(newAdjustGunForBodyTurn);
        }

        public void SetAdjustRadarForGunTurn(bool newAdjustRadarForGunTurn)
        {
            SetCall();
            commands.setAdjustRadarForGunTurn(newAdjustRadarForGunTurn);
            if (!commands.IsAdjustRadarForBodyTurnSet())
            {
                commands.setAdjustRadarForBodyTurn(newAdjustRadarForGunTurn);
            }
        }

        public void SetAdjustRadarForBodyTurn(bool newAdjustRadarForBodyTurn)
        {
            SetCall();
            commands.setAdjustRadarForBodyTurn(newAdjustRadarForBodyTurn);
            commands.setAdjustRadarForBodyTurnSet(true);
        }

        public bool IsAdjustGunForBodyTurn()
        {
            GetCall();
            return commands.IsAdjustGunForBodyTurn();
        }

        public bool IsAdjustRadarForGunTurn()
        {
            GetCall();
            return commands.IsAdjustRadarForGunTurn();
        }

        public bool IsAdjustRadarForBodyTurn()
        {
            GetCall();
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
                saveDistanceToGo = GetDistanceRemaining();
                saveAngleToTurn = GetBodyTurnRemaining();
                saveGunAngleToTurn = GetGunTurnRemaining();
                saveRadarAngleToTurn = GetRadarTurnRemaining();
            }
            isStopped = true;

            commands.setDistanceRemaining(0);
            commands.setBodyTurnRemaining(0);
            commands.setGunTurnRemaining(0);
            commands.setRadarTurnRemaining(0);
        }
    }
}