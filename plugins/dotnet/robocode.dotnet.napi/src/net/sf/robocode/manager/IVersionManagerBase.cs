namespace net.sf.robocode.manager
{
    public interface IVersionManagerBase
    {
        string getVersion();
        int getVersionAsInt();
        bool isLastRunVersionChanged();
    }
}