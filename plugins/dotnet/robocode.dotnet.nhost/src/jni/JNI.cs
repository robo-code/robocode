using System;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public static unsafe class JNI
    {
        public const int JNI_VERSION_1_1 = 0x00010001;
        public const int JNI_VERSION_1_2 = 0x00010002;
        public const int JNI_VERSION_1_4 = 0x00010004;
        public const int JNI_VERSION_1_6 = 0x00010006;

        public static JNIResult JNI_CreateJavaVM(out JavaVM pvm, out JNIEnv penv)
        {
            JavaVM.Real* vm;
            JNIEnv.Real* env;

            JNIResult result = Dll.JNI_CreateJavaVM(out vm, out env, null);
            if (result == JNIResult.JNI_OK)
            {
                pvm = new JavaVM(vm);
                penv = new JNIEnv(env);
                return result;
            }
            throw new ApplicationException();
        }

        #region Nested type: Dll

        private static class Dll
        {
            [DllImport("jvm.dll", CallingConvention = CallingConvention.StdCall)]
            public static extern JNIResult JNI_CreateJavaVM(out JavaVM.Real* pvm, out JNIEnv.Real* penv,
                                                            JavaVMInitArgs* args);

            [DllImport("jvm.dll", CallingConvention = CallingConvention.StdCall)]
            public static extern JNIResult JNI_GetCreatedJavaVMs(JavaVM.Real** vm, int size, int* size2);
        }

        #endregion
    }
}