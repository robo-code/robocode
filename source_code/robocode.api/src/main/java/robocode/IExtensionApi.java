package robocode;

import java.util.List;


public interface IExtensionApi {
	
	//NOTE: If speed is an issue, we could create static and dynamic versions of this function
	//so that unchanging information isn't sent over and over again
	public void UpdateBattlefieldState(List<String> state);
}
