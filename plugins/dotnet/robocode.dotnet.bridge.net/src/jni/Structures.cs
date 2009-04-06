using System;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.bridge.net.jni
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
        public byte ignoreUnrecognized;
    }

    [StructLayout(LayoutKind.Sequential), NativeCppClass]
    public unsafe struct JavaVMAttachArgs
    {
        public int version;
        public IntPtr name; //char*
        public JObject.Native* group;
    }

    [StructLayout(LayoutKind.Explicit, Size = 8), NativeCppClass]
    public unsafe struct jvalue
    {
        public jvalue(object o,JNIEnv env)
        {
            _bool = 0;
            _byte = 0;
            _char = 0;
            _short = 0;
            _int = 0;
            _long = 0;
            _float = 0;
            _double = 0;
            _object = null;
            if (o is int)
            {
                _int = (int)o;
            }
            else if(o is bool)
            {
                _bool = ((bool) o) ? (byte) 1 : (byte) 0;
            }
            else if (o is byte)
            {
                _bool = ((byte) o);
            }
            else if (o is char)
            {
                _char = (short)((char)o);
            }
            else if (o is short)
            {
                _short = ((short)o);
            }
            else if (o is long)
            {
                _long = ((long)o);
            }
            else if (o is float)
            {
                _float = ((float)o);
            }
            else if (o is double)
            {
                _double = ((double)o);
            }
            else if (o is jvalue)
            {
                _double = ((jvalue) o)._double;
            }
            else if (o is JObject)
            {
                _object = ((JObject)o).native;
            }
            else if (o is string)
            {
                _object = env.NewString(((string) o));
            } else
            {
                throw new ArgumentException("Conversion to java is not supported");
            }
            
        }

        public jvalue(int i)
        {
            _bool = 0;
            _byte = 0;
            _char = 0;
            _short = 0;
            _int = 0;
            _long = 0;
            _float = 0;
            _double = 0;
            _object = null;

            _int = i;
        }

        [FieldOffset(0)] public byte _bool;
        [FieldOffset(0)] public byte _byte;
        [FieldOffset(0)] public short _char;
        [FieldOffset(0)] public short _short;
        [FieldOffset(0)] public int _int;
        [FieldOffset(0)] public long _long;
        [FieldOffset(0)] public float _float;
        [FieldOffset(0)] public double _double;
        [FieldOffset(0)] public void* _object;
    }
}