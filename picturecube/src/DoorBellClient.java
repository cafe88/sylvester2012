import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class DoorBellClient {
	final static String HOSTNAME="localhost";
	final static int PORT = 4242;

	public static void main(String[] args) {
		Socket server = null;
		try {
			server = new Socket(HOSTNAME,PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null)
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
