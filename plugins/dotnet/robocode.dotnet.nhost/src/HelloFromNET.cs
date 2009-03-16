using System;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace robocode.dotnet.nhost
{
    [Guid("32C3FEAE-65FC-4001-853A-E116F84C024A")]
    [ProgId("Robocode.HelloFromNET2")]
    public class HelloFromNET
    {

        public void Main3()
        {
            Console.WriteLine("DOTNET!");
        }

        public void Main2()
        {
            MessageBox.Show("DOTNET!");
        }
    }
}
