using System;
using System.Runtime.Serialization;
using net.sf.jni4net.jni;

namespace robocode.control
{
    /*
    [Serializable]
    partial class RobotSpecification : ISerializable
    {
        protected RobotSpecification(SerializationInfo info, StreamingContext context)
        {
            var handle = (IntPtr)info.GetValue("handle", typeof(IntPtr));
            ((IJvmProxy)this).Init(JNIEnv.ThreadEnv, handle, null);
        }

        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("handle", ((IJvmProxy) this).JvmHandle);
        }
    }*/
}
