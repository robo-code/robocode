using System.Runtime.CompilerServices;
using IKVM.Attributes;
using IKVM.Runtime;
using java.util;

namespace robocode.control.events
{
    public class BattleCompletedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private BattleRules battleRules;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private BattleResults[] results;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {1, 0x68, 0x67, 0x67})]
        public BattleCompletedEvent(BattleRules battleRules, BattleResults[] results)
        {
            this.battleRules = battleRules;
            this.results = results;
        }

        public virtual BattleRules getBattleRules()
        {
            return battleRules;
        }

        [LineNumberTable(new byte[] {0x22, 0x8d, 0x75})]
        public virtual BattleResults[] getIndexedResults()
        {
            var dest = new BattleResults[results.Length];
            ByteCodeHelper.arraycopy(results, 0, dest, 0, results.Length);
            return dest;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x15, 0x91, 0x66, 0x66})]
        public virtual BattleResults[] getSortedResults()
        {
            var list = new ArrayList(Arrays.asList(results));
            Collections.sort(list);
            Collections.reverse(list);
            return (BattleResults[]) list.toArray(new BattleResults[list.size()]);
        }
    }
}