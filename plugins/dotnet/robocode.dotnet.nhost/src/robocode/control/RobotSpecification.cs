using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using net.sf.jni4net.jni;

namespace robocode.control
{
    [Serializable]
    partial class RobotSpecification : ISerializable
    {
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("handle", ((IJvmProxy) this).JvmHandle);
        }
    }
}
