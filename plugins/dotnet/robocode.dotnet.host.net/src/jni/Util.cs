using System;
using System.Runtime.InteropServices;

namespace robocode.dotnet.host.net.jni
{
    public static class Util
    {
        public static void GetDelegateForFunctionPointer<T>(IntPtr ptr, ref T res)
        {
            res = (T) (object) Marshal.GetDelegateForFunctionPointer(ptr, typeof (T));
        }
    }
}