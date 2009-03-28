using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;

namespace robocode.control
{
    [Implements(new[] {"java.io.Serializable"})]
    public class BattlefieldSpecification : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        private readonly int height;
        private readonly int width;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb5, 0x70})]
        public BattlefieldSpecification() : this(800, 600)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0, 0x68, 0x76, 0x90, 0x76, 0xb0, 0x67, 0x67})]
        public BattlefieldSpecification(int width, int height)
        {
            if ((width < 400) || (width > 0x1388))
            {
                throw new IllegalArgumentException("width must be: 400 >= width < 5000");
            }
            if ((height < 400) || (height > 0x1388))
            {
                throw new IllegalArgumentException("height must be: 400 >= height < 5000");
            }
            this.width = width;
            this.height = height;
        }

        public virtual int getHeight()
        {
            return height;
        }

        public virtual int getWidth()
        {
            return width;
        }
    }
}