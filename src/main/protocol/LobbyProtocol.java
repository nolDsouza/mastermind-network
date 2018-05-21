/******************************************************************************
 * This class performs a protocol reading and writing between sockets. See
 * channel for how connection is established. The protocol for the serverside
 * socket is read once, tell twice. This means after initialisation the server
 * wait for a client to guess the code, then the server send the client a clue
 * if they were unsuccessful. The server then must tell the client their next
 * action to take depending on the game of the game.
 *****************************************************************************/
package protocol;

import utility.Message;
import utility.PrettyPrinter;
import java.net.Socket;
import channel.Channel;
import channel.Server;
import channel.Lobby;
import channel.Stream;
import game.Game;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.NullPointerException;
import java.io.IOException;
import java.net.SocketException;
public class LobbyProtocol 
extends Protocol
implements Runnable {
	private static final String name = "Lobby";
	private final Object lock = new Object();
	private final Object shutdown = new Object();
	private boolean locked;
	private boolean running; 
	private ServerProtocol sp;
	// The queue of registered clients
	private Socket currClient;
	private Queue<Stream> clients;
	private Lobby lobby;	
	/**
	 * Constructor is called early in main, 
	 * so full initialisation comes later.
	 */
	public LobbyProtocol() {
		lobby = new Lobby();
		clients = new LinkedList<Stream>();
		locked = false;
		running = true;
		GAMELOG.info("Started new lobby");
		COMMLOG.info("Started new lobby");
	}
	/**
	 * The protocol interface contracts a name to be available so the
	 * superclass can attach a messenger to all messages sent through
	 * the channel.
	 * @Return The final string 'Server'.
	 */
	@Override
	public String getName() {
		return name;
	}
	/**
	 * Connects the server to the correct ports.  
	 * @Return success or fail.
	 */
	private boolean ensured() {
		if (lobby.connect(Channel.PORT1) < 0) {
			return false;
		}
		GAMELOG.fine("Connected to " + Channel.HOST + ":" + Channel.PORT1);	
		return true;
	}

	private void accept() {
		PrettyPrinter.print("Registration is now available!");
		new Thread( () -> startup() ).start();
		while (true) {
			try {
				Thread.sleep(2000);
				// Here the thread is blocked, hence why thread switches.
				currClient = lobby.accept();
				GAMELOG.info("Client accepted to socket");
				if (currClient != null) {
					// Start a new thread as another client may join before
					// this stream registers.
					new Thread( () -> register() ).start();
				}
			} catch (NullPointerException e) {
				GAMELOG.severe(e.toString());
				return;
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	/**
	 * This function will add connected clients to the queue. This will be
	 * run as a seperate thread and will occassionally context switch back
	 * this will allow clients to join the lobby at any time so long as the
	 * lobby itself exists.
	 */
	private void register() {
		// Create a communication channel for the client.
		Stream stream = new Stream(currClient);
		// Blocking call.
		Message msg = readSilent(stream);
		// Add client to queue.
		clients.add(stream);
		String name = msg.getMessenger();
		PrettyPrinter.print("Accepted a new client " + name);
		PrettyPrinter.print("Start a new round? (y)");
		COMMLOG.info(name + " Connected was added to the lobby");
		// Poison pill received, ClientProtocol cannot send empty string.
		if (msg.getContents().equals("")) {
			PrettyPrinter.print("uh oh, I don't feel so good...");
			GAMELOG.info("Lobby was closed");
			shutdown();
		}
		write("Welcome " + name + "!", stream);
		// Unlock Server thread as queue is no longer empty.
		if (locked) {
			unlock();
		}
	}	
	/**
	 * Have control wait if processing cannot be continued without receiving
	 * a new resource.
	 */
	private void lock() throws InterruptedException {
		locked = true;
		GAMELOG.info("Lock was put on game construction");
		// Enter safely moitored state.
		synchronized(lock) {
			lock.wait();
		}
	}
	/**
	 * Open control up to the waiting threads.
	 */ 
	private void unlock() {
		locked = false;
		synchronized(lock) {
			lock.notify();
		}
		GAMELOG.info("Game construction unlocked");
	}
	/**
	 * This function will probe for user input asking for if the user wants to
	 * start a game. If there are no clients in the queue control is blocked 
	 * until that is changed. Starting the server will take 3 or less clients.
	 * This thread has no use once the Server begins.
	 */
	private void requestGame() {
		// This is the only system input a server can take.
		final BufferedReader in = 
			new BufferedReader(new InputStreamReader(System.in));
		String temp;
		int len;
		try {
			// Sleep so thread doesn't hog resources.
			Thread.sleep(2000);
			
			// Here the thread is blocked, so control will switch occasionally.
			do {
				temp = in.readLine();
			} while (!temp.equals("y"));
			if (!running) {
				return;
			}
				
			PrettyPrinter.print("Starting new round...");
			// Calculate maximum number of clients.
			len = (clients.size() >= 3) ? 3 : clients.size();
			
			// Begin server construction, see the ServerProtocol for game cons.
			sp = new ServerProtocol(len);
			for (int i=0; i<len; i++) {
				write("Game has started!", clients.remove());
				COMMLOG.info(i + " Client removed from queue");
			}
			// Start serverprotocol thread.
			Thread gameThread = new Thread(sp);
			// Don't let current thread loop until game is finished.
			gameThread.start();
			gameThread.join();
			GAMELOG.info("New game was started");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Any thread can technically call for the shutdown. It is expected to come
	 * from the registration thread.
	 */
	private void startup() {
		try {
			synchronized(shutdown) {
				shutdown.wait();
			}
			lobby.cleanup();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void shutdown() {
		synchronized(shutdown) {
			shutdown.notify();
		}
	}
	/**
	 * This is the only method the main class has access to. Will be started
	 * on a new thread.
	 */
	@Override	
	public void run() {
		if (!ensured()) return;
		// Execute accept in a new thread.
		new Thread( () -> accept() ).start();
		while (true) {
			// Wait for clients to be added to the queue.
			try {
				if (clients.size() == 0) {
					lock();
				}
				if (!running) {
					return;
				}
				requestGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
}
