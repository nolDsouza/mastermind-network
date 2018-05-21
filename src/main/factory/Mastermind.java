package main;

import channel.Channel;
import channel.Server;
import protocol.*;
import java.util.Map;
import java.util.HashMap;
/******************************************************************************
 * This is the main class for the mastermind program. The function is to create
 * a thread based on the given parameters (Lobby, Server or Client) and start
 * the thread.
 ******************************************************************************/
public class Mastermind {
	private static final int EXPARGS = 1;
	private static final Map<String, Runnable> factory = createMap();
	/**
	 * The main method dispatches a new thread.
	 */
	public static void main(String[] args) {
		// validation
		if (args.length < EXPARGS) {
			System.err.println("Error too few arguements");
		} else if (args.length > EXPARGS) {
			System.err.println("Error too many arguements");
		} else if (!factory.containsKey(args[0])) {
			System.err.println("Error invalid parameter specified: " 
					+ args[0]);
		} else {
			Thread t = new Thread(factory.get(args[0]));
			t.start();
		}
	}
	/**
	 * Map class names to runnable threads.
	 * @Return a runnable implementation if the class is mapped.
	 */
	private static final Map<String, Runnable> createMap() {
		Map<String, Runnable> map = new HashMap<String, Runnable>();
		map.put("CLIENT", new ClientProtocol());
		map.put("LOBBY", new LobbyProtocol());
		map.put("POISON", new PoisonProtocol());
		return map;
	}
}
