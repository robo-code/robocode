using System;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public enum JNIResult
    {
        JNI_OK = 0, /* success */
        JNI_ERR = (-1), /* unknown error */
        JNI_EDETACHED = (-2), /* thread detached from the VM */
        JNI_EVERSION = (-3), /* JNI version error */
        JNI_ENOMEM = (-4), /* not enough memory */
        JNI_EEXIST = (-5), /* VM already created */
        JNI_EINVAL = (-6), /* invalid arguments */
    }

    [StructLayout(LayoutKind.Sequential), NativeCppClass]
    public struct JavaVMOption
    {
        public IntPtr optionString; //char*
        public IntPtr extraInfo; //void*
    }

    [StructLayout(LayoutKind.Sequential), NativeCppClass]
    public unsafe struct JavaVMInitArgs
    {
        public int version;
        public int nOptions;
        public JavaVMOption* options;
        public bool ignoreUnrecognized;
    }

    [StructLayout(LayoutKind.Sequential), NativeCppClass]
    public unsafe struct JavaVMAttachArgs
    {
        public int version;
        public IntPtr name; //char*
        public jobject* group;
    }

    [StructLayout(LayoutKind.Explicit, Size = 8), NativeCppClass]
    public unsafe struct jvalue
    {
        public jvalue(int i)
        {
            _bool = false;
            _byte = 0;
            _char = '\0';
            _short = 0;
            _int = 0;
            _long = 0;
            _float = 0;
            _double = 0;
            _object = null;

            _int = i;
        }

        [FieldOffset(0)] public bool _bool;
        [FieldOffset(0)] public byte _byte;
        [FieldOffset(0)] public char _char;
        [FieldOffset(0)] public short _short;
        [FieldOffset(0)] public int _int;
        [FieldOffset(0)] public long _long;
        [FieldOffset(0)] public float _float;
        [FieldOffset(0)] public double _double;
        [FieldOffset(0)] public void* _object;
    }
}