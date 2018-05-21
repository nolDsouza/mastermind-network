package channel;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.lang.StringBuilder;
import java.io.IOException;
/******************************************************************************
 * An interface for socket interaction. The procedure is as follows:
 * 1. Connect to the Port and Host.
 * 2. Open Input/Output channels.
 * 3. Communication between ports based on some protocol.
 * 4. Closure of all opened ports.
 * The Client will throw ConnectException if no server is running.
 *****************************************************************************/
public class Client implements Channel {
	private Socket socket;
	private String msg;
	private Stream stream;
	
	public void setMsg(String str) {
		this.msg = str;
	}

	public String getMsg() {
		return msg;
	}
	/**
	 * Connect to the given port and open IO channels.
	 * @Return port connected to or an error code.
	 */
	public int connect(int port) {
		try {
			// Connect to socket.
			socket = new Socket(HOST, port);
			// Open up I/O ports.
			stream = new Stream(socket);
			return port;
		// No server available.
		} catch (ConnectException ce) {
			System.err.println("Error: No available server running");
			ce.printStackTrace();
			return BAD_CONNECT;
		// Host is unreachable.
		} catch (UnknownHostException uhe) {
			System.err.println("Don't know about host: " + HOST);
			return BAD_ADDRESS;
		// IO ports failed to open.
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for host: " + HOST);
			return BAD_IO;
		}
	}
	/**
	 * Loop until reader can read line. Loop is required as seperate server
	 * has no common variable to notify. After this the message is set.
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
			socket.close();
		} catch (IOException e) {
			System.err.println("Couldn't close socket for host: " + HOST);
			e.printStackTrace();
		}
	}
}

