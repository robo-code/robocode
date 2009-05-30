package net.sf.robocode.battle;

public class ModuleFactory {

	public static ICustomRules getCustomRules(String moduleName)
	{
		if (moduleName.equals("New"))
		{
			return new CaptureTheFlagRules();
		}
		else
		{
			return new ClassicRules();
		}
	}
	
	public static IBattlefieldSetup getBattleSetup(String moduleName)
	{
		if (moduleName.equals("New"))
		{
			return new CaptureTheFlagSetup();
		}
		else
		{
			return new ClassicSetup();
		}
	}
}
