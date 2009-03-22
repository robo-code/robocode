using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using net.sf.robocode.security;

namespace robocode.control
{
    [Implements(new[] {"java.io.Serializable"})]
    public class RobotSpecification : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string author;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string description;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private object fileSpecification;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string fullClassName;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string jarFile;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string name;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string robocodeVersion;
        private string teamName;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string version;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string webpage;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 0xbf, 0x68, 0x67, 0x67, 0x67, 0x68, 0x68, 0x68, 0x68, 0x68, 0x68})]
        private RobotSpecification(object obj1, string text1, string text2, string text3, string text4, string text5,
                                   string text6, string text7, string text8)
        {
            fileSpecification = obj1;
            name = text1;
            author = text2;
            webpage = text3;
            version = text4;
            robocodeVersion = text5;
            jarFile = text6;
            fullClassName = text7;
            description = text8;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic)]
        internal RobotSpecification(object obj1, string text1, string text2, string text3, string text4, string text5,
                                    string text6, string text7, string text8, a1 a1)
            : this(obj1, text1, text2, text3, text4, text5, text6, text7, text8)
        {
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static object access200(RobotSpecification specification1)
        {
            return specification1.fileSpecification;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static string access300(RobotSpecification specification1)
        {
            return specification1.teamName;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static string access302(RobotSpecification specification1, string text1)
        {
            string str = text1;
            RobotSpecification specification = specification1;
            specification.teamName = str;
            return str;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xa5)]
        internal static IHiddenSpecificationHelper createHiddenHelper()
        {
            return new HiddenHelper(null);
        }

        public virtual string getAuthorName()
        {
            return author;
        }

        public virtual string getClassName()
        {
            return fullClassName;
        }

        public virtual string getDescription()
        {
            return description;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x76)]
        public virtual File getJarFile()
        {
            return new File(jarFile);
        }

        public virtual string getName()
        {
            return name;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2a, 0x67, 0x87, 0x77, 0x9e})]
        public virtual string getNameAndVersion()
        {
            string str = getName();
            string @this = getVersion();
            if ((@this != null) && (String.instancehelper_length(String.instancehelper_trim(@this)) > 0))
            {
                str = new StringBuilder().append(str).append(' ').append(@this).toString();
            }
            return str;
        }

        public virtual string getRobocodeVersion()
        {
            return robocodeVersion;
        }

        public virtual string getTeamId()
        {
            return teamName;
        }

        public virtual string getVersion()
        {
            return version;
        }

        public virtual string getWebpage()
        {
            return webpage;
        }

        #region Nested type: a1

        [Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("RobotSpecification.java"),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         EnclosingMethod("robocode.control.RobotSpecification", null, null)]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: HiddenHelper

        [InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("RobotSpecification.java"),
         Implements(new[] {"net.sf.robocode.security.IHiddenSpecificationHelper"})]
        internal sealed class HiddenHelper : Object, IHiddenSpecificationHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xa8)]
            private HiddenHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xa8), Modifiers(Modifiers.Synthetic)]
            internal HiddenHelper(RobotSpecification a1) : this()
            {
            }

            #region IHiddenSpecificationHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xab)]
            public RobotSpecification createSpecification(object obj1, string text1, string text2, string text3,
                                                          string text4, string text5, string text6, string text7,
                                                          string text8)
            {
                return new RobotSpecification(obj1, text1, text2, text3, text4, text5, text6, text7, text8, null);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xb0)]
            public object getFileSpecification(RobotSpecification specification1)
            {
                return access200(specification1);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xb8)]
            public string getTeamName(RobotSpecification specification1)
            {
                return access300(specification1);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x42, 0x68})]
            public void setTeamName(RobotSpecification specification1, string text1)
            {
                access302(specification1, text1);
            }

            #endregion
        }

        #endregion
    }
}