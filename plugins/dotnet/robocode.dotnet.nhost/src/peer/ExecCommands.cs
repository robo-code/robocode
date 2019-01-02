/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using net.sf.robocode.nio;
using net.sf.robocode.serialization;
using Robocode;

namespace net.sf.robocode.dotnet.peer
{
    [Serializable]
    public class ExecCommands
    {
        public const int defaultBodyColor = 0; //0xFF29298C;
        public const int defaultGunColor = 0; //0xFF29298C;
        public const int defaultRadarColor = 0; //0xFF29298C;
        public const int defaultScanColor = 0; //0xFF0000FF;
        public const int defaultBulletColor = 0; //0xFFFFFFFF;
        private int bodyColor = defaultBodyColor;

        private double bodyTurnRemaining;
        private int bulletColor = defaultBulletColor;
        private List<BulletCommand> bullets = new List<BulletCommand>();
        private List<DebugProperty> debugProperties = new List<DebugProperty>();
        private double distanceRemaining;
        [NonSerialized] private byte[] graphicsCalls;
        private int gunColor = defaultGunColor;
        private double gunTurnRemaining;

        private bool isAdjustGunForBodyTurn;
        private bool isAdjustRadarForBodyTurn;
        private bool isAdjustRadarForBodyTurnSet;
        private bool isAdjustRadarForGunTurn;
        private bool isIORobot;
        private bool isTryingToPaint;

        private double maxTurnRate;
        private double maxVelocity;

        private bool moved;
        private String outputText;
        private int radarColor = defaultRadarColor;
        private double radarTurnRemaining;
        private bool scan;
        private int scanColor = defaultScanColor;
        private List<TeamMessage> teamMessages = new List<TeamMessage>();

        public ExecCommands()
        {
            setMaxVelocity(Double.MaxValue);
            setMaxTurnRate(Double.MaxValue);
        }

        public ExecCommands(ExecCommands origin, bool fromRobot)
        {
            bodyTurnRemaining = origin.bodyTurnRemaining;
            radarTurnRemaining = origin.radarTurnRemaining;
            gunTurnRemaining = origin.gunTurnRemaining;
            distanceRemaining = origin.distanceRemaining;
            isAdjustGunForBodyTurn = origin.isAdjustGunForBodyTurn;
            isAdjustRadarForGunTurn = origin.isAdjustRadarForGunTurn;
            isAdjustRadarForBodyTurn = origin.isAdjustRadarForBodyTurn;
            isAdjustRadarForBodyTurnSet = origin.isAdjustRadarForBodyTurnSet;
            maxTurnRate = origin.maxTurnRate;
            maxVelocity = origin.maxVelocity;
            copyColors(origin);
            if (fromRobot)
            {
                debugProperties = origin.debugProperties;
                bullets = origin.bullets;
                scan = origin.scan;
                moved = origin.moved;
                graphicsCalls = origin.graphicsCalls;
                outputText = origin.outputText;
                teamMessages = origin.teamMessages;
                isTryingToPaint = origin.isTryingToPaint;
            }
        }

        public void copyColors(ExecCommands origin)
        {
            if (origin != null)
            {
                bodyColor = origin.bodyColor;
                gunColor = origin.gunColor;
                radarColor = origin.radarColor;
                bulletColor = origin.bulletColor;
                scanColor = origin.scanColor;
            }
        }

        public int getBodyColor()
        {
            return bodyColor;
        }

        public int getRadarColor()
        {
            return radarColor;
        }

        public int getGunColor()
        {
            return gunColor;
        }

        public int getBulletColor()
        {
            return bulletColor;
        }

        public int getScanColor()
        {
            return scanColor;
        }

        public void setBodyColor(int color)
        {
            bodyColor = color;
        }

        public void setRadarColor(int color)
        {
            radarColor = color;
        }

        public void setGunColor(int color)
        {
            gunColor = color;
        }

        public void setBulletColor(int color)
        {
            bulletColor = color;
        }

        public void setScanColor(int color)
        {
            scanColor = color;
        }

        public double getBodyTurnRemaining()
        {
            return bodyTurnRemaining;
        }

        public void setBodyTurnRemaining(double bodyTurnRemaining)
        {
            this.bodyTurnRemaining = bodyTurnRemaining;
        }

        public double getRadarTurnRemaining()
        {
            return radarTurnRemaining;
        }

        public void setRadarTurnRemaining(double radarTurnRemaining)
        {
            this.radarTurnRemaining = radarTurnRemaining;
        }

        public double getGunTurnRemaining()
        {
            return gunTurnRemaining;
        }

        public void setGunTurnRemaining(double gunTurnRemaining)
        {
            this.gunTurnRemaining = gunTurnRemaining;
        }

        public double getDistanceRemaining()
        {
            return distanceRemaining;
        }

        public void setDistanceRemaining(double distanceRemaining)
        {
            this.distanceRemaining = distanceRemaining;
        }

        public bool IsAdjustGunForBodyTurn()
        {
            return isAdjustGunForBodyTurn;
        }

        public void setAdjustGunForBodyTurn(bool adjustGunForBodyTurn)
        {
            isAdjustGunForBodyTurn = adjustGunForBodyTurn;
        }

        public bool IsAdjustRadarForGunTurn()
        {
            return isAdjustRadarForGunTurn;
        }

        public void setAdjustRadarForGunTurn(bool adjustRadarForGunTurn)
        {
            isAdjustRadarForGunTurn = adjustRadarForGunTurn;
        }

        public bool IsAdjustRadarForBodyTurn()
        {
            return isAdjustRadarForBodyTurn;
        }

        public void setAdjustRadarForBodyTurn(bool adjustRadarForBodyTurn)
        {
            isAdjustRadarForBodyTurn = adjustRadarForBodyTurn;
        }

        public bool IsAdjustRadarForBodyTurnSet()
        {
            return isAdjustRadarForBodyTurnSet;
        }

        public void setAdjustRadarForBodyTurnSet(bool adjustRadarForBodyTurnSet)
        {
            isAdjustRadarForBodyTurnSet = adjustRadarForBodyTurnSet;
        }

        public double getMaxTurnRate()
        {
            return maxTurnRate;
        }

        public void setMaxTurnRate(double maxTurnRate)
        {
            this.maxTurnRate = Math.Min(Math.Abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);
        }

        public double getMaxVelocity()
        {
            return maxVelocity;
        }

        public void setMaxVelocity(double maxVelocity)
        {
            this.maxVelocity = Math.Min(Math.Abs(maxVelocity), Rules.MAX_VELOCITY);
        }

        public bool isMoved()
        {
            return moved;
        }

        public void setMoved(bool moved)
        {
            this.moved = moved;
        }

        public bool isScan()
        {
            return scan;
        }

        public void setScan(bool scan)
        {
            this.scan = scan;
        }

        public List<BulletCommand> getBullets()
        {
            return bullets;
        }

        public Object getGraphicsCalls()
        {
            return graphicsCalls;
        }

        public List<DebugProperty> getDebugProperties()
        {
            return debugProperties;
        }

        public void setGraphicsCalls(byte[] graphicsCalls)
        {
            this.graphicsCalls = graphicsCalls;
        }

        public string getOutputText()
        {
            return outputText;
        }

        public void setOutputText(string output)
        {
            outputText = string.IsNullOrEmpty(output) ? null : output;
        }

        public List<TeamMessage> getTeamMessages()
        {
            return teamMessages;
        }

        public bool IsIORobot()
        {
            return isIORobot;
        }

        public void setIORobot()
        {
            isIORobot = true;
        }

        public void setDebugProperty(String key, String value)
        {
            debugProperties.Add(new DebugProperty(key, value));
        }

        public bool IsTryingToPaint()
        {
            return isTryingToPaint;
        }

        public void setTryingToPaint(bool tryingToPaint)
        {
            isTryingToPaint = tryingToPaint;
        }

        // ReSharper disable UnusedMember.Local
        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelperN
        {
            #region ISerializableHelperN Members

            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                var obj = (ExecCommands) obje;
                int size = RbSerializerN.SIZEOF_TYPEINFO + 4*RbSerializerN.SIZEOF_DOUBLE;

                size += 4*RbSerializerN.SIZEOF_BOOL;
                size += 5*RbSerializerN.SIZEOF_INT;
                size += 2*RbSerializerN.SIZEOF_DOUBLE;
                size += 4*RbSerializerN.SIZEOF_BOOL;
                size += serializer.sizeOf(obj.outputText);

                size += serializer.sizeOf(obj.graphicsCalls);

                // bullets
                size += obj.bullets.Count*serializer.sizeOf(RbSerializerN.BulletCommand_TYPE, null);
                size += 1;

                // messages
                foreach (TeamMessage m in obj.teamMessages)
                {
                    size += serializer.sizeOf(RbSerializerN.TeamMessage_TYPE, m);
                }
                size += 1;

                // properties
                foreach (DebugProperty d in obj.debugProperties)
                {
                    size += serializer.sizeOf(RbSerializerN.DebugProperty_TYPE, d);
                }
                size += 1;

                return size;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                var obj = (ExecCommands) obje;

                serializer.serialize(buffer, obj.bodyTurnRemaining);
                serializer.serialize(buffer, obj.radarTurnRemaining);
                serializer.serialize(buffer, obj.gunTurnRemaining);
                serializer.serialize(buffer, obj.distanceRemaining);

                serializer.serialize(buffer, obj.isAdjustGunForBodyTurn);
                serializer.serialize(buffer, obj.isAdjustRadarForGunTurn);
                serializer.serialize(buffer, obj.isAdjustRadarForBodyTurn);
                serializer.serialize(buffer, obj.isAdjustRadarForBodyTurnSet);

                serializer.serialize(buffer, obj.bodyColor);
                serializer.serialize(buffer, obj.gunColor);
                serializer.serialize(buffer, obj.radarColor);
                serializer.serialize(buffer, obj.scanColor);
                serializer.serialize(buffer, obj.bulletColor);

                serializer.serialize(buffer, obj.maxTurnRate);
                serializer.serialize(buffer, obj.maxVelocity);

                serializer.serialize(buffer, obj.moved);
                serializer.serialize(buffer, obj.scan);
                serializer.serialize(buffer, obj.isIORobot);
                serializer.serialize(buffer, obj.isTryingToPaint);

                serializer.serialize(buffer, obj.outputText);

                serializer.serialize(buffer, obj.graphicsCalls);

                foreach (BulletCommand bullet in obj.bullets)
                {
                    serializer.serialize(buffer, RbSerializerN.BulletCommand_TYPE, bullet);
                }
                buffer.put(RbSerializerN.TERMINATOR_TYPE);
                foreach (TeamMessage message in obj.teamMessages)
                {
                    serializer.serialize(buffer, RbSerializerN.TeamMessage_TYPE, message);
                }
                buffer.put(RbSerializerN.TERMINATOR_TYPE);
                foreach (DebugProperty prop in obj.debugProperties)
                {
                    serializer.serialize(buffer, RbSerializerN.DebugProperty_TYPE, prop);
                }
                buffer.put(RbSerializerN.TERMINATOR_TYPE);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                var res = new ExecCommands();

                res.bodyTurnRemaining = buffer.getDouble();
                res.radarTurnRemaining = buffer.getDouble();
                res.gunTurnRemaining = buffer.getDouble();
                res.distanceRemaining = buffer.getDouble();

                res.isAdjustGunForBodyTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForGunTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForBodyTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForBodyTurnSet = serializer.deserializeBoolean(buffer);

                res.bodyColor = buffer.getInt();
                res.gunColor = buffer.getInt();
                res.radarColor = buffer.getInt();
                res.scanColor = buffer.getInt();
                res.bulletColor = buffer.getInt();

                res.maxTurnRate = buffer.getDouble();
                res.maxVelocity = buffer.getDouble();

                res.moved = serializer.deserializeBoolean(buffer);
                res.scan = serializer.deserializeBoolean(buffer);
                res.isIORobot = serializer.deserializeBoolean(buffer);
                res.isTryingToPaint = serializer.deserializeBoolean(buffer);

                res.outputText = serializer.deserializeString(buffer);

                res.graphicsCalls = serializer.deserializeBytes(buffer);

                Object item = serializer.deserializeAny(buffer);

                while (item != null)
                {
                    if (item is BulletCommand)
                    {
                        res.bullets.Add((BulletCommand) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                item = serializer.deserializeAny(buffer);
                while (item != null)
                {
                    if (item is TeamMessage)
                    {
                        res.teamMessages.Add((TeamMessage) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                item = serializer.deserializeAny(buffer);
                while (item != null)
                {
                    if (item is DebugProperty)
                    {
                        res.debugProperties.Add((DebugProperty) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                return res;
            }

            #endregion
        }

        #endregion
    }
}