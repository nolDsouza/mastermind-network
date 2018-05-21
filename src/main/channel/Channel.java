package channel;


import java.io.*;
import java.net.*;
import java.net.ConnectException;
/******************************************************************************
 * An interface for socket interaction. The procedure is as follows:
 * 1. Connect to the Port and Host.
 * 2. Open Input/Output channels.
 * 3. Communication between ports based on some protocol.
 * 4. Closure of all opened ports.
 * Client and Server sockets have differences in connection, but otherwise are
 * quite similar.
 *****************************************************************************/
public interface Channel {
	int BAD_CONNECT = -3;
	int BAD_ADDRESS = -4;
	int BAD_IO = -5;
	int BUFF_SIZE = 1024;
	int PORT1 = 10251;
	int PORT2 = 20251;
	int PORT3 = 30251;
	String HOST = "127.0.0.1";
	/**
	 * Connect to the given port and open IO channels.
	 * @Return port connected to or an error code.
	 */
	int connect(int port);
	/**
	 * Setter for stream buffer.
	 */
	void setMsg(String str);
	/**
	 * Getter for stream buffer.
	 */
	String getMsg();
	/**
	 * A constant loop waiting for the scoket to receive next line.
	 * Once successful the message is set automatically.
	 */
	abstract void listen();
	/**
	 * Write whatever is in the buffer to the socket.
	 */
	abstract void tell();
	/**
	 * Close the ports.
	 */
	abstract void cleanup();
}
