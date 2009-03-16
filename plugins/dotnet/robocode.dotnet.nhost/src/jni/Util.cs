using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Text;

namespace robocode.dotnet.nhost.jni
{
    public static class Util
    {
        public static void GetDelegateForFunctionPointer<T>(IntPtr ptr, ref T res)
        {
            res = (T)(object)Marshal.GetDelegateForFunctionPointer(ptr, typeof(T));
        }
    }
}
