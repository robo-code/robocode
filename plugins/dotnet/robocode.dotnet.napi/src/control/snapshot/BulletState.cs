using System;
using System.Runtime.CompilerServices;
using ikvm.@internal;
using IKVM.Attributes;
using java.lang;
using Enum=java.lang.Enum;

namespace robocode.control.snapshot
{
    [Signature("Ljava/lang/Enum<Lrobocode/control/snapshot/BulletState;>;"),
     Modifiers(Modifiers.Enum | Modifiers.Public | Modifiers.Synchronized | Modifiers.Final)]
    public sealed class BulletState : Enum
    {
        #region __Enum enum

        [Serializable, HideFromJava]
        public enum __Enum
        {
            FIRED,
            MOVING,
            HIT_VICTIM,
            HIT_BULLET,
            HIT_WALL,
            EXPLODED,
            INACTIVE
        }

        #endregion

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            EXPLODED = new BulletState("EXPLODED", 5, 5);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            FIRED = new BulletState("FIRED", 0, 0);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            HIT_BULLET = new BulletState("HIT_BULLET", 3, 3);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            HIT_VICTIM = new BulletState("HIT_VICTIM", 2, 2);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            HIT_WALL = new BulletState("HIT_WALL", 4, 4);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            INACTIVE = new BulletState("INACTIVE", 6, 6);

        [Modifiers(Modifiers.Enum | Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static BulletState
            MOVING = new BulletState("MOVING", 1, 1);

        [Modifiers(Modifiers.Synthetic | Modifiers.Static | Modifiers.Private | Modifiers.Final)] private static
            BulletState[] VALUES = new[] {FIRED, MOVING, HIT_VICTIM, HIT_BULLET, HIT_WALL, EXPLODED, INACTIVE};

        [Modifiers(Modifiers.Private | Modifiers.Final)] private int value;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbd, 0x6a, 0x67})]
        private BulletState(string text1, int num1, int num2) : base(text1, num1)
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

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x18, 0x9f, 8, 0xa6, 0xa6, 0xa6, 0xa6, 0xa6, 0xa6, 0xa6})]
        public static BulletState toState(int value)
        {
            switch (value)
            {
                case 0:
                    return FIRED;

                case 1:
                    return MOVING;

                case 2:
                    return HIT_VICTIM;

                case 3:
                    return HIT_BULLET;

                case 4:
                    return HIT_WALL;

                case 5:
                    return EXPLODED;

                case 6:
                    return INACTIVE;
            }
            throw new IllegalArgumentException("unknown value");
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x16)]
        public static BulletState valueOf(string name)
        {
            return (BulletState) valueOf(ClassLiteral<BulletState>.Value, name);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x16)]
        public static BulletState[] values()
        {
            return (BulletState[]) VALUES.Clone();
        }
    }
}