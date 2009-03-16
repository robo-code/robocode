using System;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

namespace robocode.dotnet.nhost.jni
{
    public unsafe class JavaVM
    {
        public Real* real;
        public static JavaVMInitArgs vm_args;
        public static JavaVMOption options;
        Real.AttachCurrentThread attachCurrentThread;

        static JavaVM()
        {

            vm_args = new JavaVMInitArgs();
            options = new JavaVMOption();
            options.optionString = Marshal.StringToHGlobalAnsi("-Drobocode=true");
            vm_args.version = JNI.JNI_VERSION_1_6;
            vm_args.nOptions = 1;
            fixed (JavaVMOption* o = &options)
            {
                vm_args.options = o;
            }
            vm_args.ignoreUnrecognized = false;
        }

        [StructLayout(LayoutKind.Sequential, Size = 4), NativeCppClass]
        public struct Real
        {
            public JNIInvokeInterface* functions;

            [UnmanagedFunctionPointer(CallingConvention.StdCall)]
            public delegate JNIResult AttachCurrentThread(Real* thiz, out JNIEnv.Real* penv, ref JavaVMInitArgs args);

        }

        public JavaVM(Real* real)
        {
            this.real = real;
            attachCurrentThread = (Real.AttachCurrentThread)Marshal.GetDelegateForFunctionPointer((*((*real).functions)).AttachCurrentThread, typeof(Real.AttachCurrentThread));
        }

        public JNIResult AttachCurrentThread(out JNIEnv penv)
        {
            JNIEnv.Real* env;

            JNIResult result = attachCurrentThread.Invoke(real, out env, ref vm_args);
            penv = new JNIEnv(env);
            return result;
        }



        /*
      
            jint DestroyJavaVM() {
            jint DestroyJavaVM() {
                return functions->DestroyJavaVM(this);
            }
            jint AttachCurrentThread(void **penv, void *args) {
                return functions->AttachCurrentThread(this, penv, args);
            }
            jint DetachCurrentThread() {
                return functions->DetachCurrentThread(this);
            }

            jint GetEnv(void **penv, jint version) {
                return functions->GetEnv(this, penv, version);
            }
            jint AttachCurrentThreadAsDaemon(void **penv, void *args) {
                return functions->AttachCurrentThreadAsDaemon(this, penv, args);
            }
      
              */
    }
}
