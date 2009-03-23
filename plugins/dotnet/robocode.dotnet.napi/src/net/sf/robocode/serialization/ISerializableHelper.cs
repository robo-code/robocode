
using robocode.net.sf.robocode.serialization;

namespace net.sf.robocode.serialization
{
    public interface ISerializableHelper
    {
        object deserialize(RbSerializer rs, ByteBuffer bb);
        void serialize(RbSerializer rs, ByteBuffer bb, object obj);
        int sizeOf(RbSerializer rs, object obj);
    }
}