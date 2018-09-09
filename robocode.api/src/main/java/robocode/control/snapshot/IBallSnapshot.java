package robocode.control.snapshot;

public interface IBallSnapshot {
    double getVelocity();
    double getX();
    double getY();
    double getZ();
    double getPaintX();
    double getPaintY();
    double getPaintZ();
    int getColor();
    int getFrame();
    int getOwnerIndex();
}
