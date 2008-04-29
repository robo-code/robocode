svn co https://robocode.svn.sourceforge.net/svnroot/robocode/robocode/branches/modularization-v2-workspace/ mtestroot
cd mtestroot
copy /y prepared\pom.xml                                                          pom.xml
copy /y prepared\robocode\pom.xml                                                 robocode\pom.xml

rem ********************************************************************************************
svn mkdir robotapi
rem ********************************************************************************************
svn mkdir                                                                         robotapi\src\main\java\robocode\util
svn mv prepared\robotapi\pom.xml                                                  robotapi
svn mv robocode\src\main\java\robocode\AdvancedRobot.java                         robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Bullet.java                                robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\BulletHitBulletEvent.java                  robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\BulletHitEvent.java                        robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\BulletMissedEvent.java                     robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Condition.java                             robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\CustomEvent.java                           robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\DeathEvent.java                            robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Droid.java                                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Event.java                                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\GunTurnCompleteCondition.java              robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\HitByBulletEvent.java                      robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\HitRobotEvent.java                         robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\HitWallEvent.java                          robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\JuniorRobot.java                           robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\MessageEvent.java                          robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\MoveCompleteCondition.java                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\PaintEvent.java                            robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\RadarTurnCompleteCondition.java            robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Robocode.java                              robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\RobocodeFileOutputStream.java              robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\RobocodeFileWriter.java                    robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Robot.java                                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\RobotDeathEvent.java                       robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\RobotStatus.java                           robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\Rules.java                                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\ScannedRobotEvent.java                     robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\SkippedTurnEvent.java                      robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\StatusEvent.java                           robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\TeamRobot.java                             robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\TurnCompleteCondition.java                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\WinEvent.java                              robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\_AdvancedRadiansRobot.java                 robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\_AdvancedRobot.java                        robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\_Robot.java                                robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\_RobotBase.java                            robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\package.html                               robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\robotinterfaces                            robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\exception                                  robotapi\src\main\java\robocode
svn mv robocode\src\main\java\robocode\util\Utils.java                            robotapi\src\main\java\robocode\util


rem ********************************************************************************************
svn mkdir common
rem ********************************************************************************************
svn mkdir                                                                         common\src\main\java\robocode
svn mv prepared\common\pom.xml                                                    common


rem ********************************************************************************************
svn mkdir robocodeui
rem ********************************************************************************************
svn mkdir                                                                         robocodeui\src\main\java\robocode\util
svn mkdir                                                                         robocodeui\src\main\java\robocode\manager
svn mv prepared\robocodeui\pom.xml                                                robocodeui
svn mv robocode\src\main\resources                                                robocodeui\src\main\
svn mv robocode\src\main\java\robocode\battleview                                 robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\dialog                                     robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\editor                                     robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\gfx                                        robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\manager                                    robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\packager                                   robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\sound                                      robocodeui\src\main\java\robocode
svn mv robocode\src\main\java\robocode\util\BoundingRectangle.java                robocodeui\src\main\java\robocode\util
svn mv robocode\src\main\java\robocode\util\GraphicsState.java                    robocodeui\src\main\java\robocode\util
svn mv robocode\src\main\java\robocode\manager\BrowserManager.java                robocodeui\src\main\java\robocode\manager
svn mv robocode\src\main\java\robocode\manager\ImageManager.java                  robocodeui\src\main\java\robocode\manager
svn mv robocode\src\main\java\robocode\manager\RobotDialogManager.java            robocodeui\src\main\java\robocode\manager
svn mv robocode\src\main\java\robocode\manager\VersionManager.java                robocodeui\src\main\java\robocode\manager
svn mv robocode\src\main\java\robocode\manager\WindowManager.java                 robocodeui\src\main\java\robocode\manager


cd ..
