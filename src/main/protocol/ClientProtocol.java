/******************************************************************************
 * This class performs a protocol reading and writing between sockets. See
 * channel for how connection is established. The protocol for the clientside
 * socket is tell once, read twice. This means after initialisation the client
 * must guess the code once, then wait for a response from the server. The 
 * first response must be echoed back to the console and the second determines
 * the next action to take.
 *****************************************************************************/
package protocol;

import channel.Channel;
import channel.Client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import utility.PrettyPrinter;
import java.io.IOException;
public class ClientProtocol 
extends Protocol
implements Runnable {
	private BufferedReader in;
	private String name;
	/**
	 * Constructor is called early in main, 
	 * so full initialisation comes later.
	 */
 	public ClientProtocol() {
		super.channel = new Client();
		super.commChannel = new Client();
		super.gameChannel = new Client();
		this.in = new BufferedReader(new InputStreamReader(System.in));
		// name.length called in reister loop.
		this.name = "";
	}
	/**
	 * Connects the client to the correct ports. 
	 * @Return success or fail.
	 */
	private boolean ensured() {
		if (channel.connect(Channel.PORT1) < 0)
			return false;
		return true;
	}

	private void registerName() {
		try {
			// Name has length validation. 
			while (name.length() < 1) {
				PrettyPrinter.print("Enter player name: ");
				name = in.readLine();
				System.out.println(name);
			}
			write(name);
			read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialisation is required at begginning of game to designate the length
	 * of the game code.
	 */
	private void initialise() {
		try {
			// Waiting for green light from server.
			read();
			if (commChannel.connect(Channel.PORT2) < 0)
				return;
			write(name, commChannel);
			if (gameChannel.connect(Channel.PORT3) < 0)
				return;
			write(name, gameChannel);

			read(commChannel);
			// Input validates numerical input and within range.
			while (true) {
				PrettyPrinter.print("Enter preferred length of secret code: ");
				PrettyPrinter.print("Please enter a digit between 3 and 8");	
				String input = in.readLine();
				if (input.length() != 1) {
					PrettyPrinter.print("Input should be one digit");
					continue;
				}
				if (!Character.isDigit(input.charAt(0))) {
					PrettyPrinter.print("Please enter an integer");
					continue;
				}
				int n = Character.getNumericValue(input.charAt(0));
				if (n >= 3 && n <= 8) {
					write(input, commChannel);
					readSilent(gameChannel);
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Primary procedure for protocol. Write once, then read twice.
	 * In the case of the game ending, read once more from the server 
	 * to receive the actual code.
	 */
	private void play() {
		try {
			while(true) {
				PrettyPrinter.print("Guess code: ");
				// Write the guess for the code.
				write(in.readLine(), gameChannel);
				// Read the servers feedback.
				read(commChannel);
				// Read the servers interperetation of the game state.
				if (read(gameChannel).getContents().equals(EXIT)) {
					end();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Once the game is over the client can choose to play again, and return
	 * to the lobby or to quit. Either way the client must tell the server its
	 * plan of action.
	 */
	private void end() {
		// Receive information about the code.
		read(commChannel);
		// Check if this client wants to reconnect to the lobby.
		try {
			String temp;
			PrettyPrinter.print("Play again? (p/q)");
			while (true) {
				temp = in.readLine();
				if (temp.equals("p")) {
					run();
					return;
				} else if (temp.equals("q")) {
					channel.cleanup();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This is the only method the main class has access to. Will be started
	 * on a new thread.
	 */
	@Override
	public void run() {
		if (!ensured()) {
			return;
		}
		registerName();
		// Will block until new game has started.
		initialise();
		play();
	}
	/**
	 * The protocol interface contracts a name to be available so the
	 * superclass can attach a messenger to all messages sent through
	 * the channel.
	 * @Return Name set during registration.
	 */
	@Override
	public String getName() {
		return name;
	}
}
