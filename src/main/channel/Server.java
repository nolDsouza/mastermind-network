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
import java.util.HashMap;
import java.lang.NullPointerException;
/******************************************************************************
 * An interface for socket interaction. The procedure is as follows:
 * 1. Connect to the Port and Host.
 * 2. Open Input/Output channels.
 * 3. Communication between ports based on some protocol.
 * 4. Closure of all opened ports.
 * The Server can accept multiple clients to the same port.
 *****************************************************************************/
public class Server implements Channel {
	protected Stream stream;
	protected String msg;
	protected ServerSocket server;
	protected Socket client;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String str) {
		this.msg = str;
	}
	/**
	 * Connect to the given port and open IO channels.
	 * @Return port connected to or an error code.
	 */
	public int connect(int port) {
		try {
			client = null;
			// Try to connect to given port.
			server = new ServerSocket(port);
			while (true) {
				// Accept a client.
				client = server.accept();
				// Open up I/O ports.
				if (client != null) {
					stream = new Stream(client);
					return port;
				}
			}
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
	 * Loop until the reader can read line. Loop is required as the server
	 * and clients share no global objects.
	 */
	public void listen() {
		this.msg = stream.read();
	}
	/**
	 * Write message to buffer.
	 */
	public void tell() {
		stream.write(msg);
	}
	/**
	 * Close all ports.
	 */
	public void cleanup() {
		try {
			stream.close();
			server.close();
			client.close();
		} catch (IOException e) {
			System.err.println("Couldn't close socket for host: " + HOST);
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}  

