using System.IO;

namespace net.sf.robocode.serialization
{
    public interface ISerializableHelper
    {
        object deserialize(RbSerializer rs, BinaryReader bb);
        void serialize(RbSerializer serializer, BinaryWriter bb, object obj);
        int sizeOf(RbSerializer rs, object obj);
    }
}