using java.awt.@event;

namespace robocode.robotinterfaces
{
    public interface IInteractiveEvents
    {
        void onKeyPressed(java.awt.@event.KeyEvent ke);
        void onKeyReleased(java.awt.@event.KeyEvent ke);
        void onKeyTyped(java.awt.@event.KeyEvent ke);
        void onMouseClicked(java.awt.@event.MouseEvent me);
        void onMouseDragged(java.awt.@event.MouseEvent me);
        void onMouseEntered(java.awt.@event.MouseEvent me);
        void onMouseExited(java.awt.@event.MouseEvent me);
        void onMouseMoved(java.awt.@event.MouseEvent me);
        void onMousePressed(java.awt.@event.MouseEvent me);
        void onMouseReleased(java.awt.@event.MouseEvent me);
        void onMouseWheelMoved(MouseWheelEvent mwe);
    }
}