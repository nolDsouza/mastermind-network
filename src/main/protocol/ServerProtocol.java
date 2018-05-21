/******************************************************************************
 * This class performs a protocol reading and writing between sockets. See
 * channel for how connection is established. The protocol for the serverside
 * socket is read once, tell twice. This means after initialisation the server
 * wait for a client to guess the code, then the server send the client a clue
 * if they were unsuccessful. The server then must tell the client their next
 * action to take depending on the game of the game.
 *****************************************************************************/
package protocol;

import utility.Message;
import utility.PrettyPrinter;
import channel.Channel;
import channel.Server;
import channel.Lobby;
import channel.Stream;
import game.Game;
import game.Agent;
import stdio.*;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.*;
public class ServerProtocol 
extends Protocol
implements Runnable {
	private static final String name = "Server";
	private Game game;
	// Transferrable encodes a game game which can generate a successor game
	private int numAgents;
	private Lobby commChannel;
	private Lobby gameChannel;
	private HashMap<String, Stream> commStreams;
	private HashMap<String, Stream> gameStreams;
	private HashMap<String, Agent> agents;
	/**
	 * Constructor is called early in main, 
	 * so full initialisation comes later.
	 */
	public ServerProtocol(int n) {
		commChannel = new Lobby();
		gameChannel = new Lobby();
		this.numAgents = n;
		commStreams = new HashMap<String, Stream>(numAgents);
		gameStreams = new HashMap<String, Stream>(numAgents);
		agents = new HashMap<String, Agent>(numAgents);
		GAMELOG.info("Started a new server");
	}
	/**
	 * The protocol interface contracts a name to be available so the
	 * superclass can attach a messenger to all messages sent through
	 * the channel.
	 * @Return The final string 'Server'.
	 */
	@Override
	public String getName() {
		return name;
	}
	/**
	 * Connects the server to the correct ports.  
	 * @Return success or fail.
	 */
	private boolean ensured() throws IOException {
		if (commChannel.connect(Channel.PORT2) < 0) {
			return false;
		}
		COMMLOG.info("Connected to: " + Channel.HOST + ":" + Channel.PORT2);
		for (int i=0; i<numAgents; i++) {
			Stream stream = new Stream(commChannel.accept());
			String name = readSilent(stream).getContents();
			commStreams.put(name, stream);
		}

		if (gameChannel.connect(Channel.PORT3) < 0) {
			return false;
		}
		COMMLOG.info("Connected to: " + Channel.HOST + ":" + Channel.PORT3);
		for (int i=0; i<numAgents; i++) {
			Stream stream = new Stream(gameChannel.accept());
			String name = readSilent(stream).getContents();
			gameStreams.put(name, stream);
			GAMELOG.info(name + " was added to the game");
		}
		write("Game has " + numAgents + " player(s)", commStreams.values());
		GAMELOG.info("Game was started for " + numAgents);
		return true;
	}
	/**
	 * Initialisation requires calclating an average to determine the length
	 * of the game code.
	 */
	private void initialise() throws IOException {
		int total = 0;
		int avg;
		for (int i=0; i<numAgents; i++) {
			// Convert vote into int, validated in client protocol.
			Message vote = read(commStreams.values());
			total += Integer.parseInt(vote.getContents());
			COMMLOG.info(vote.getMessenger() + " voted " + vote.getContents());
		}
		// Calculate average of all votes.	
		avg = total / numAgents;
		game = new Game(avg);
		GAMELOG.info("The game has code size " + avg);
		GAMELOG.info("Code generated was " + game.getPattern());
		// Assign a game state for each client.
		for (String name : gameStreams.keySet()) {
			agents.put(name, new Agent(game));
		}
		// Announce elected length of code.
		write(String.valueOf(avg), gameStreams.values());
	}
	/**
	 * Primary procedure for protocol. Read once, then write twice.
	 * In the case of the game ending, send all clients the actual code. 
	 */
	private void play() {
		PrettyPrinter.print(numAgents + " started");
		while(true) {
			// Get first available client message.
			Message metaMsg = read(gameStreams.values());
			String client = metaMsg.getMessenger();
			Stream gameStream = gameStreams.get(client);
			Stream commStream = commStreams.get(client);
			// Get the agent for this client.
			Agent agent = agents.get(client);
			// Agent generates a game based on the client, stored in game.
			int state = agent.generateState(metaMsg.getContents());
			// Switch the context of the currents state.
			switch (state) {
				// In the Unmoved game no successor was generated, guess count
				// was not incremented.
				case Agent.UNMOVED: 
					GAMELOG.info(client + " guessed an incorrect format");
					// Game getState is based off the previous successor.
					write("Format was incorrect, not counted", commStream);
					write(PROCEED, gameStream);
					break;
				// Game game is the most common game, the client has
				// more guesses remaining.
				case Agent.INGAME:
					GAMELOG.info(client + " guessed incorrectly");
					// Game getState is based off the previous successor.
					write(game.formatClue() + ", you have " +
							agent.getGuessCount() + " guess(es) remaining",
							commStream);
					write(PROCEED, gameStream);
					break;
				// Terminal game will occur if the client has reached the max
				// number of guesses or forfeighted. Final games occur from 
				// now on, terminal games end the game.
				case Agent.FINISHED:
					GAMELOG.info(client + " reached max turn limit");
					write("You have reached the maximum number of guesses" + 
							"Please wait for the end of the game", commStream);
					agents.remove(client);
					write(EXIT, gameStream);
					break;
				// Client chose to forgeight.
				case Agent.TERMINATED:
					GAMELOG.info(client + " forfeighted");
					write("You have elected to forfeight, Please wait for the" +
						  " end of the game", commStream);
					agents.remove(client);
					write(EXIT, gameStream);
					break;
				// Goal game is a terminal game where the client has guessed
				// the code corectly. The difference between the regular
				// terminal game is this will end the game for all clients.
				case Agent.WINNER:
					GAMELOG.info(client + " guessed correctly");
					announceWinner(client, gameStream);
					write(EXIT, gameStream);
					return;
			}
			if (isOver()) {
				GAMELOG.info("Game ended");
				return;
			}
		}
	}
	/**
	 * No winner, but all clients have made max guesses.
	 */
	private boolean isOver() {
		System.out.println(agents.size());
		return (agents.size() == 0);
	}
	/**
	 * Once a winner has been announced the server must announce the winner
	 * to each client and end the game. The thread will not read any inputs
	 * which are inserted from now onward.
	 */
	private void announceWinner(String winner, Stream winnerStream) {
		// Tell all streams there is a winner.
		for (Stream stream : commStreams.values()) {
			if (stream == winnerStream) {
				write("You guessed the code!", winnerStream);
			} else {
				write(winner + " guessed the code!", stream);
			}
		}
		// Tell all clients to end inputs.
		write(EXIT, gameStreams.values());
	}
	/**
	 * Once the game is each client can choose to play again, and return
	 * to the lobby or to quit (They will require a new server). 
	 * Either way the client must tell the server its plan of action.
	 */
	private void end() {
		write("The correct code was " + game.getPattern(), 
				commStreams.values());
		for (Stream stream : commStreams.values()) {
			stream.close();
		}
		for (Stream stream : gameStreams.values()) {
			stream.close();
		}
		commChannel.cleanup();
		gameChannel.cleanup();
	}
	
	/**
	 * This is the only method the main class has access to. Will be started
	 * on a new thread.
	 */
	@Override	
	public void run() {
		try {		
			if (!ensured()) {
				return;
			}
			initialise();
			play();
			end();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
