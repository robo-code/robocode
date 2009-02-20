using System;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace robocode.dotnet.nhost
{
    [Guid("3823a63d-5891-3b4f-A460-DB0FB847075C")]
    [ProgId("Robocode.HelloFromNET2")]
    public class HelloFromNET
    {
        public void Main()
        {
            System.Console.WriteLine("DOTNET!");
        }

        public void Main2()
        {
            MessageBox.Show("DOTNET!");
        }
    }

}
