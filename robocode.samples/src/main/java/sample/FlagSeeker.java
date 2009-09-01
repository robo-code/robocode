// package sample;
//
// //this will be in a different package and the dependency won't be on robocode.battle
// import java.awt.geom.Point2D;
//
// import net.sf.robocode.battle.BasePeer;
// import net.sf.robocode.battle.CaptureTheFlagExtensionApi;
// import net.sf.robocode.battle.FlagPeer;
//
// import extensions.Base;
// import extensions.Flag;
// import robocode.AdvancedRobot;
// import robocode.HitObjectEvent;
// import robocode.HitObstacleEvent;
// import robocode.robotinterfaces.IObjectEvents;
// import robocode.robotinterfaces.IObjectRobot;
//
// public class FlagSeeker extends AdvancedRobot implements IObjectEvents, IObjectRobot{
//
// CaptureTheFlagExtensionApi capApi;
// int myTeam;
// int enemyTeam;
// FlagPeer enemyFlag;
// BasePeer homeBase;
//
// Point2D destination;
//
// public void run() {
// capApi = (CaptureTheFlagExtensionApi)getExtensionApi();
//
// myTeam = capApi.getTeamNumber(this);
// enemyTeam = (myTeam == 1 ? 2 : 1);
// homeBase = capApi.getBase(myTeam);
// enemyFlag = capApi.getFlag(enemyTeam);
//
// destination = new Point2D.Double(enemyFlag.getX(), enemyFlag.getY());
//
// while (true)
// {
// goTo(destination);
// execute();
// }
// }
//
// public void goTo(Point2D point)	{
// double dx = point.getX() - getX();
// double dy = point.getY() - getY();
//
// double bearing = Math.atan(dy/dx);
//
// setTurnRight(bearing);
// setAhead(100);
// }
//
// public void onHitObject(HitObjectEvent event) {
// if (event.getType() == "flag")
// {
// destination = new Point2D.Double(homeBase.getX(), homeBase.getY());
// }
// }
//
// public void onHitObstacle(HitObstacleEvent event) {
// }
//
// public IObjectEvents getObjectEventListener() {
// return this;
// }
//
// }
