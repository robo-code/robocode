# Run several Classic Robocode battles without a display

Use the Control API when a tournament or evaluation needs several battles from one Java process. This example uses the sample robots included with an installed copy of Classic Robocode.

Save this as `HeadlessBattles.java` in the installation directory.

```java
import java.io.File;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

public class HeadlessBattles {
    public static void main(String[] args) {
        RobocodeEngine engine = new RobocodeEngine(new File("."));
        try {
            engine.setVisible(false);
            engine.addBattleListener(new BattleAdaptor() {
                private int battles;

                @Override
                public void onBattleCompleted(BattleCompletedEvent event) {
                    System.out.println("Battle " + ++battles);
                    for (BattleResults result : event.getSortedResults()) {
                        System.out.println(result.getTeamLeaderName() + " scored " + result.getScore());
                    }
                }
            });

            BattlefieldSpecification field = new BattlefieldSpecification(800, 600);
            RobotSpecification[] robots = engine.getLocalRepository("sample.Walls, sample.Corners");
            BattleSpecification battle = new BattleSpecification(1, field, robots);
            for (int game = 0; game < 2; game++) {
                engine.runBattle(battle, true);
            }
        } finally {
            engine.close();
        }
    }
}
```

Compile and run from the installation directory. These commands were tested in a POSIX shell with Java 21. On Windows, use `;` as the classpath separator and enter the `java` command on one line.

```sh
javac -cp 'libs/*' HeadlessBattles.java
java -Djava.awt.headless=true -Djava.security.manager=allow \
    --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens=java.desktop/javax.swing.text=ALL-UNNAMED \
    --add-opens=java.desktop/sun.awt=ALL-UNNAMED \
    -cp '.:libs/*' HeadlessBattles
```

Expect headings for `Battle 1` and `Battle 2`, followed by the two robots' scores for each battle. The second argument to `runBattle` tells the engine to wait for each battle to finish before starting the next. Keep a single engine for the batch and close it after the last battle.
