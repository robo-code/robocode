using System;
using System.Runtime.CompilerServices;
using ikvm.@internal;
using IKVM.Attributes;
using java.lang;
using Enum=java.lang.Enum;

namespace robocode.control.snapshot
{
    [Modifiers(Modifiers.Enum | Modifiers.Public | Modifiers.Synchronized | Modifiers.Final),
     Signature("Ljava/lang/Enum<Lrobocode/control/snapshot/RobotState;>;")]
    public sealed class RobotState : Enum
    {
        #region __Enum enum

        [Serializable, HideFromJava]
        public enum __Enum
        {
            ACTIVE,
            HIT_WALL,
            HIT_ROBOT,
            DEAD
        }

        #endregion

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static RobotState
            ACTIVE = new RobotState("ACTIVE", 0, 0);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static RobotState
            DEAD = new RobotState("DEAD", 3, 3);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static RobotState
            HIT_ROBOT = new RobotState("HIT_ROBOT", 2, 2);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static RobotState
            HIT_WALL = new RobotState("HIT_WALL", 1, 1);

        [Modifiers(Modifiers.Synthetic | Modifiers.Static | Modifiers.Private | Modifiers.Final)] private static
            RobotState[] VALUES = new[] {ACTIVE, HIT_WALL, HIT_ROBOT, DEAD};

        [Modifiers(Modifiers.Private | Modifiers.Final)] private int value;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 180, 0x6a, 0x67})]
        private RobotState(string text1, int num1, int num2) : base(text1, num1)
        {
            value = num2;
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        public static void __<clinit>()
        {
        }

        public int getValue()
        {
            return value;
        }

        public bool isAlive()
        {
            return (this != DEAD);
        }

        public bool isDead()
        {
            return (this == DEAD);
        }

        public bool isHitRobot()
        {
            return (this == HIT_ROBOT);
        }

        public bool isHitWall()
        {
            return (this == HIT_WALL);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {15, 0x9b, 0xa6, 0xa6, 0xa6, 0xa6})]
        public static RobotState toState(int value)
        {
            switch (value)
            {
                case 0:
                    return ACTIVE;

                case 1:
                    return HIT_WALL;

                case 2:
                    return HIT_ROBOT;

                case 3:
                    return DEAD;
            }
            throw new IllegalArgumentException("unknown value");
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x16)]
        public static RobotState valueOf(string name)
        {
            return (RobotState) valueOf(ClassLiteral<RobotState>.Value, name);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x16)]
        public static RobotState[] values()
        {
            return (RobotState[]) VALUES.Clone();
        }
    }
}