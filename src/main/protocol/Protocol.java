package protocol;

import channel.Channel;
import channel.Stream;
import utility.Message;
import utility.PrettyPrinter;
import java.io.IOException;
import java.util.logging.*;
/******************************************************************************
 * This class performs a protocol reading and writing between sockets. See
 * channel for how connection is established. The abstract class behaviour is
 * to read and write to the socket, a messenger is attached to the message.
 *****************************************************************************/
public abstract class Protocol implements Identifiable {
	int COMMUNICATION = 1;
	int GAME = 2;
	protected static final Logger COMMLOG = Logger.getLogger("CommLog");	
	protected static final Logger GAMELOG = Logger.getLogger("GameLog");		
	protected static final String PROCEED = "PROCEED";
	protected static final String EXIT = "EXIT";
	protected static final String ENDGAME = "ENDGAME";
	protected static final String AGAIN = "AGAIN";
	// Channel can be client or server.
	protected Channel channel;
	protected Channel commChannel;
	protected Channel gameChannel;
	private FileHandler ch;
	private FileHandler gh;

	protected Protocol() {
		try {
			ch = new FileHandler("CommunicationLog.log");
			COMMLOG.addHandler(ch);
			gh = new FileHandler("GamingLog.log");
			GAMELOG.addHandler(gh);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Read a message from the socket as a string. echo it back to the console
	 * for bookkeeping.
	 * @Return A Message object containing a message and messenger.
	 */
	public Message read() {
		channel.listen();
		String str = channel.getMsg();
		PrettyPrinter.print(str);
		COMMLOG.info("read from client: " + str);
		return new Message(str);
	}
	public Message read(Channel channel) {
		channel.listen();
		String str = channel.getMsg();
		PrettyPrinter.print(str);
		COMMLOG.info("read from client: " + str);
		return new Message(str);
	}
	/**
	 * Read a message from the socket as a string. echo it back to the console
	 * for bookkeeping.
	 * @Return A Message object containing a message and messenger.
	 */
	public Message read(Stream stream) {
		String str = stream.read();
		PrettyPrinter.print(str);
		COMMLOG.info("read from client: " + str);
		return new Message(str);
	}

	/**
	 * Read a stream without accounting.
	 */
	public Message readSilent(Stream stream) {
		return new Message(stream.read());
	}
	/**
	 * Read a stream without accounting.
	 */
	public Message readSilent(Channel channel) {
		channel.listen();
		return new Message(channel.getMsg());
	}
	/**
	 * Read from first stream to receive input.
	 */
	public Message read(Iterable<Stream> streams) {
		while (true) {
			for (Stream stream : streams) {
				if (stream.hasNext()) {
					return read(stream);
				}
			}
		}
	}
	/**
	 * Write a message to the socket. PrettyPrinter formats the string
	 * based on the getName method.
	 */
	public void write(String msg) {
		channel.setMsg(PrettyPrinter.format(msg, this));
		channel.tell();
		COMMLOG.info("wrote to client: " + msg);
	}

	public void write(String msg, Stream stream) {
		stream.write(PrettyPrinter.format(msg, this));
		COMMLOG.info("wrote to client: " + msg);
	}
	
	public void write(String msg, Channel channel) {
		channel.setMsg(PrettyPrinter.format(msg, this));
		channel.tell();
		COMMLOG.info("wrote to client: " + msg);
	}
	/**
	 * Write message to all streams.
	 */
	public void write(String msg, Iterable<Stream> streams) {
		for (Stream stream : streams) {
			write(msg, stream);
		}
	}
	/**
	 * PrettyPrinter formats the string based on this method.
	 */
	public abstract String getName();
}
