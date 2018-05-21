/******************************************************************************
 * This class performs functions as a safe way to kill the lobby.
 *****************************************************************************/
package protocol;

import channel.Client;
public class PoisonProtocol 
extends Protocol
implements Runnable {
	private static final String name = "Poison";
	/**
	 * Constructor is called early in main, 
	 * so full initialisation comes later.
	 */
 	public PoisonProtocol() {
		channel = new Client();
	}

	public boolean ensured() {
		if (channel.connect(channel.PORT1) < 0)
			return false;
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run() {
		if (ensured()) 
			// Use the empty string as the poison pill.
			write("");
	}
}
