package utility;

import stdio.Out;
import protocol.Identifiable;
/******************************************************************************
 * This class is a go-between messages and Protocols. Protocols will use 
 * format to write to the socket. 
 *****************************************************************************/
public class PrettyPrinter	{
	private static final Out out = new Out();
	/**
	 * Wrapper for Sysyem.out.println
	 */
	public static void print(String str) {
		out.println(str);
	}
	/**
	 * Takes in a string and protocol, returns a formatted string
	 * which is readable on the console.
	 */
	public static String format(String str, Identifiable usr) {
		return new Message(usr.getName(), str).toString();
	}
}
