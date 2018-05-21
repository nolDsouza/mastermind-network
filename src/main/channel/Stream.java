package channel;

import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.lang.NullPointerException;
import java.io.IOException;
/******************************************************************************
 * An class for socket communication. The stream is provides Input/Output for
 * an already connected socket. This is seperated logic seeing as many clients
 * connect to the same port but only one server can. So servers must maintain
 * streams for multiple sockets.
 *****************************************************************************/
public class Stream {
	private BufferedReader reader;
	private PrintWriter writer;
	/**
	 * Connect to the given port and open IO channels.
	 * @Return port connected to or an error code.
	 */
	public Stream(Socket socket) {
		try {
			reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Returns if the input has a next line.
	 */
	public boolean hasNext() {
		try {
			return reader.ready();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
	}
	/**
	 * Loop until the reader can read line. Loop is required as the server
	 * and clients share no global objects.
	*/
	public String read() {
		try {	
			do {
				if (reader.ready()) {
					String msg = reader.readLine();
					return msg;
				}
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Write message to buffer.
	 */
	public void write(String msg) {
		writer.println(msg);
	}
	/**
	 * Close both streams.
	 */
	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}  
