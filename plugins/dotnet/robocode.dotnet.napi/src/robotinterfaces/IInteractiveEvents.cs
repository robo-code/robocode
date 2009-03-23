namespace robocode.robotinterfaces
{
    public interface IInteractiveEvents
    {
        void onKeyPressed(KeyEvent ke);
        void onKeyReleased(KeyEvent ke);
        void onKeyTyped(KeyEvent ke);
        void onMouseClicked(MouseEvent me);
        void onMouseDragged(MouseEvent me);
        void onMouseEntered(MouseEvent me);
        void onMouseExited(MouseEvent me);
        void onMouseMoved(MouseEvent me);
        void onMousePressed(MouseEvent me);
        void onMouseReleased(MouseEvent me);
        void onMouseWheelMoved(MouseWheelMovedEvent mwe);
    }
}