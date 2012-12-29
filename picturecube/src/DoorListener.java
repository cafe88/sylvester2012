import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DoorListener implements Runnable {

	private static final long DOOR_BELL_RINGIN_TIME = 2000;
	ServerSocket server;
	MyProcessingSketch parent;
	int port;

	public DoorListener(int port, MyProcessingSketch parent) {
		this.port = port;
		this.parent = parent;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Server could not be started");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {

			try {
				// blocking until a client is init
				Socket client = server.accept();
				parent.setDoorBell(true);

				if (client != null)
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				try {
					// wait for it wait for it...
					Thread.sleep(DOOR_BELL_RINGIN_TIME);
				} catch (Exception e) {
				}
				parent.setDoorBell(false);
			} catch (IOException e) {
				System.err.println("Server could not accept client!");
				e.printStackTrace();
			}
		}
	}

}
