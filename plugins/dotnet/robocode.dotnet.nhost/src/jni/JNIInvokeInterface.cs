using System;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    [StructLayout(LayoutKind.Sequential), NativeCppClass]
    public struct JNIInvokeInterface
    {
        public IntPtr reserved0;
        public IntPtr reserved1;
        public IntPtr reserved2;

        public IntPtr DestroyJavaVM;
        public IntPtr AttachCurrentThread;
        public IntPtr DetachCurrentThread;
        public IntPtr GetEnv;
        public IntPtr AttachCurrentThreadAsDaemon;
    }
}