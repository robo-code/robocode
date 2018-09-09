package robocode;

public final class GameRules implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final int gamefieldWidth;
    private final int gamefieldHeight;
    private final int duration;

    private GameRules(int gamefieldWidth, int gamefieldHeight, int duration) {
        this.gamefieldWidth = gamefieldWidth;
        this.gamefieldHeight = gamefieldHeight;
        this.duration = duration;
    }
}
