package channel;

import stdio.*;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.util.Queue;
import java.lang.NullPointerException;
import java.net.SocketException;
public class Lobby extends Server {
	/**
	 * Connect to the given port and open IO channels.
	 * @Return port connected to or an error code.
	 */
	@Override
	public int connect(int port) {
		try {
			client = null;
			// Try to connect to given port.
			server = new ServerSocket(port);
				return 0;
		// Host is unreachable.
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + HOST);
			e.printStackTrace();
			return BAD_ADDRESS;
		// IO ports failed to open.
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for host: " + HOST);
			e.printStackTrace();
			return BAD_IO;
		}
	}	
	/**
	 * Connect a client to the socket upon receiving a request.
	 * @Return The socket for the client.
	 */
	public Socket accept() {
		try {
			while (true) {
				// Accept a client.
				client = server.accept();
				// Open up I/O ports.
				if (client != null) {
					stream = new Stream(client);
					return client;
				}
			}
		} catch (IOException e) {
			System.err.println("Socket was closed: " + HOST);
			Thread.currentThread().interrupt();
			return null;
		}
	}
}
