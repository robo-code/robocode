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
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    ///<summary>
    ///  This event is sent to <see cref="Robot.OnBulletHit(BulletHitEvent)"/>
    ///  when one of your bullets has hit another robot.
    ///</summary>
    [Serializable]
    public sealed class BulletHitEvent : Event
    {
        private const int DEFAULT_PRIORITY = 50;

        private readonly string name;
        private readonly double energy;
        private Bullet bullet;

        ///<summary>
        ///  Called by the game to create a new BulletHitEvent.
        ///</summary>
        public BulletHitEvent(string name, double energy, Bullet bullet)
        {
            this.name = name;
            this.energy = energy;
            this.bullet = bullet;
        }

        ///<summary>
        ///  Returns the bullet of yours that hit the robot.
        ///</summary>
        public Bullet Bullet
        {
            get { return bullet; }
        }

        ///<summary>
        ///  Returns the remaining energy of the robot your bullet has hit (after the
        ///  damage done by your bullet).
        ///</summary>
        public double VictimEnergy
        {
            get { return energy; }
        }

        ///<summary>
        ///  Returns the name of the robot your bullet hit.
        ///</summary>
        public string VictimName
        {
            get { return name; }
        }

        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnBulletHit(this);
            }
        }

        // Needed for .NET version
        internal override void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
            // we need to pass same instance
            bullet = bullets[bullet.getBulletId()];
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.BulletHitEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (BulletHitEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_INT + serializer.sizeOf(obj.name)
                       + RbSerializerN.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (BulletHitEvent) objec;

                serializer.serialize(buffer, obj.bullet.getBulletId());
                serializer.serialize(buffer, obj.name);
                serializer.serialize(buffer, obj.energy);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
                string name = serializer.deserializeString(buffer);
                double energy = buffer.getDouble();

                return new BulletHitEvent(name, energy, bullet);
            }
        }
    }
}

//doc
