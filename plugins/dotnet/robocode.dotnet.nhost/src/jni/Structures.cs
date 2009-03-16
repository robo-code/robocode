using System;

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

    public struct JavaVMOption
    {
        public IntPtr optionString;//char*
        public IntPtr extraInfo;//void*
    }

    public unsafe struct JavaVMInitArgs
    {
        public int version;
        public int nOptions;
        public JavaVMOption* options;
        public bool ignoreUnrecognized;
    }

    public unsafe struct JavaVMAttachArgs
    {
        public int version;
        public IntPtr name;//char*
        public _jobject group;
    }
}
