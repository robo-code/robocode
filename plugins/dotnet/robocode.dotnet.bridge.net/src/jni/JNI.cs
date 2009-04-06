using System;
using System.IO;
using System.Runtime.InteropServices;

namespace robocode.dotnet.bridge.net.jni
{
    public static unsafe class JNI
    {
        public const int JNI_VERSION_1_1 = 0x00010001;
        public const int JNI_VERSION_1_2 = 0x00010002;
        public const int JNI_VERSION_1_4 = 0x00010004;
        public const int JNI_VERSION_1_6 = 0x00010006;

        private static string jvmDir = @"c:\Program Files\Java\jre1.6.0_07\bin\client\";
        private static bool init;

        static JNI()
        {
            Init();
        }

        private static void Init()
        {
            if (!init)
            {
                string oldDirectory = Directory.GetCurrentDirectory();
                try
                {
                    Directory.SetCurrentDirectory(jvmDir);
                    JavaVMInitArgs args=new JavaVMInitArgs();
                    JNIResult res = Dll.JNI_GetDefaultJavaVMInitArgs(&args);
                    init = true;
                }
                finally
                {
                    Directory.SetCurrentDirectory(oldDirectory);
                }
            }
        }

        public static JNIResult JNI_CreateJavaVM(out JavaVM pvm, out JNIEnv penv)
        {
            Init();
            JavaVM.Native* vm;
            JNIEnv.Native* env;
            JavaVMInitArgs args;
            args.version = JNI_VERSION_1_6;

            JNIResult result = Dll.JNI_CreateJavaVM(out vm, out env, &args);
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
            public static extern JNIResult JNI_CreateJavaVM(out JavaVM.Native* pvm, out JNIEnv.Native* penv,
                                                            JavaVMInitArgs* args);

            [DllImport("jvm.dll", CallingConvention = CallingConvention.StdCall)]
            public static extern JNIResult JNI_GetCreatedJavaVMs(JavaVM.Native** vm, int size, int* size2);

            [DllImport("jvm.dll", CallingConvention = CallingConvention.StdCall)]
            public static extern JNIResult JNI_GetDefaultJavaVMInitArgs(JavaVMInitArgs* args);
        }

        #endregion
    }
}