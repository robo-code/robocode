using System;
using robocode.control;
using robocode.control.events;

namespace BattleRunnner
{
    public class Listener : BattleAdaptor
    {
        public override void onBattleMessage(BattleMessageEvent par0)
        {
            Console.WriteLine(par0.getMessage());
        }
    }

    internal class Program
    {
        private static void Main(string[] args)
        {
            try
            {
                RobocodeEngine.Init(@"C:\Robocode");
                RobocodeEngine engine = new RobocodeEngine();
                engine.addBattleListener(new Listener());

                RobotSpecification[] robotsList = engine.getLocalRepository("sample.Crazy,SampleCs.Target");

                BattlefieldSpecification field = new BattlefieldSpecification(800, 600);
                BattleSpecification battle = new BattleSpecification(1, field, (robotsList));
                engine.runBattle(battle, true);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }
    }
}
