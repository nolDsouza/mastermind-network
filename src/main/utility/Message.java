package utility;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
/******************************************************************************
 * This class reperesents two strings: A message and a messenger name. Strings
 * which follow a certain format can be converted into Messages and Messages 
 * can be formatted into single strings. This is used by Protocols to 
 * communicate between sockets.
 *****************************************************************************/
public class Message {
	private String messenger;
	private String message;
	/**
	 * Create a Message based off the given user and message.
	 */
	public Message(String usr, String msg) {
		this.messenger = usr;
		this.message = msg;
	}

	public String getMessenger() {
		return messenger;
	}

	public String getContents() {
		return message;
	}
	/**
	 * Message assumes the given string is formatted correctly seeing as
	 * protocols cannot read/write without using message format.
	 * @Return a Message which seperates the message from the messenger
	 */
	public Message(String str) {
		final Pattern pattern = Pattern.compile(".*: ");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			this.messenger = str.substring(start+1, end-3);
			this.message = str.substring(end); 
		}
	}

	/**
	 * Flatten message into one string.
	 * @Return Printable string for the console, parsable for the constructor.
	 */
	public String toString() {
		return "[" + messenger + "]: " + message;
	}
}
