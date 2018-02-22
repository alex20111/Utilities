package home.ipChecker;


public interface IpListener {

	public void IpReceived( String event , String newIp, boolean changed);
}
