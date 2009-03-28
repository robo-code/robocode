using System;
using System.Collections.Generic;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.ncore.peer
{
    public class ExecCommands
    {
        public static int defaultBodyColor; //0xFF29298C;
        public static int defaultBulletColor; //0xFFFFFFFF;
        public static int defaultGunColor; //0xFF29298C;
        public static int defaultRadarColor; //0xFF29298C;
        public static int defaultScanColor; //0xFF0000FF;
        private readonly IList<BulletCommand> bullets = new List<BulletCommand>();
        private readonly IList<DebugProperty> debugProperties = new List<DebugProperty>();
        private readonly IList<TeamMessage> teamMessages = new List<TeamMessage>();
        private int bodyColor = defaultBodyColor;

        private double bodyTurnRemaining;
        private int bulletColor = defaultBulletColor;
        private double distanceRemaining;
        private Object graphicsCalls;
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

        public IList<BulletCommand> getBullets()
        {
            return bullets;
        }

        public Object getGraphicsCalls()
        {
            return graphicsCalls;
        }

        public IList<DebugProperty> getDebugProperties()
        {
            return debugProperties;
        }

        public void setGraphicsCalls(Object graphicsCalls)
        {
            this.graphicsCalls = graphicsCalls;
        }

        public String getOutputText()
        {
            String output = outputText;

            outputText = "";
            return output;
        }

        public void setOutputText(String output)
        {
            outputText = output;
        }

        public IList<TeamMessage> getTeamMessages()
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelper
        {
            #region ISerializableHelper Members

            public int sizeOf(RbSerializer serializer, Object object1)
            {
                var obj = (ExecCommands) object1;
                int size = RbSerializer.SIZEOF_TYPEINFO + 4*RbSerializer.SIZEOF_DOUBLE;

                size += 4*RbSerializer.SIZEOF_BOOL;
                size += 5*RbSerializer.SIZEOF_INT;
                size += 2*RbSerializer.SIZEOF_DOUBLE;
                size += 4*RbSerializer.SIZEOF_BOOL;
                size += serializer.sizeOf(obj.outputText);

                size += serializer.sizeOf((byte[]) obj.graphicsCalls);

                // bullets
                size += obj.bullets.Count*serializer.sizeOf(RbSerializer.BulletCommand_TYPE, null);
                size += 1;

                // messages
                foreach (TeamMessage m in obj.teamMessages)
                {
                    size += serializer.sizeOf(RbSerializer.TeamMessage_TYPE, m);
                }
                size += 1;

                // properties
                foreach (DebugProperty d in obj.debugProperties)
                {
                    size += serializer.sizeOf(RbSerializer.DebugProperty_TYPE, d);
                }
                size += 1;

                return size;
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object object1)
            {
                var obj = (ExecCommands) object1;

                serializer.serialize(bw, obj.bodyTurnRemaining);
                serializer.serialize(bw, obj.radarTurnRemaining);
                serializer.serialize(bw, obj.gunTurnRemaining);
                serializer.serialize(bw, obj.distanceRemaining);

                serializer.serialize(bw, obj.isAdjustGunForBodyTurn);
                serializer.serialize(bw, obj.isAdjustRadarForGunTurn);
                serializer.serialize(bw, obj.isAdjustRadarForBodyTurn);
                serializer.serialize(bw, obj.isAdjustRadarForBodyTurnSet);

                serializer.serialize(bw, obj.bodyColor);
                serializer.serialize(bw, obj.gunColor);
                serializer.serialize(bw, obj.radarColor);
                serializer.serialize(bw, obj.scanColor);
                serializer.serialize(bw, obj.bulletColor);

                serializer.serialize(bw, obj.maxTurnRate);
                serializer.serialize(bw, obj.maxVelocity);

                serializer.serialize(bw, obj.moved);
                serializer.serialize(bw, obj.scan);
                serializer.serialize(bw, obj.isIORobot);
                serializer.serialize(bw, obj.isTryingToPaint);

                serializer.serialize(bw, obj.outputText);

                serializer.serialize(bw, (byte[]) obj.graphicsCalls);

                foreach (BulletCommand bullet in obj.bullets)
                {
                    serializer.serialize(bw, RbSerializer.BulletCommand_TYPE, bullet);
                }
                bw.Write(RbSerializer.TERMINATOR_TYPE);
                foreach (TeamMessage message in obj.teamMessages)
                {
                    serializer.serialize(bw, RbSerializer.TeamMessage_TYPE, message);
                }
                bw.Write(RbSerializer.TERMINATOR_TYPE);
                foreach (DebugProperty prop in obj.debugProperties)
                {
                    serializer.serialize(bw, RbSerializer.DebugProperty_TYPE, prop);
                }
                bw.Write(RbSerializer.TERMINATOR_TYPE);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader buffer)
            {
                var res = new ExecCommands();

                res.bodyTurnRemaining = buffer.ReadDouble();
                res.radarTurnRemaining = buffer.ReadDouble();
                res.gunTurnRemaining = buffer.ReadDouble();
                res.distanceRemaining = buffer.ReadDouble();

                res.isAdjustGunForBodyTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForGunTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForBodyTurn = serializer.deserializeBoolean(buffer);
                res.isAdjustRadarForBodyTurnSet = serializer.deserializeBoolean(buffer);

                res.bodyColor = buffer.ReadInt32();
                res.gunColor = buffer.ReadInt32();
                res.radarColor = buffer.ReadInt32();
                res.scanColor = buffer.ReadInt32();
                res.bulletColor = buffer.ReadInt32();

                res.maxTurnRate = buffer.ReadDouble();
                res.maxVelocity = buffer.ReadDouble();

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