/******************************************************************************
 * The Agent interfaces between the game and server. The server maintains a
 * collection of clients, each will be assigned to an Agent. The game instance
 * will be immutable once instantiated so the agents will be able to safely 
 * guess the code an receive clues to interperet. Agents are seperated from the 
 * game seeing as each client has a limited ammount of guesses. Agents have no
 * access to other agents.
 *****************************************************************************/
package game;

public class Agent {	
	public static final int INITIAL = 0;
	public static final int INGAME = 1;
	public static final int UNMOVED = 2;
	public static final int FINISHED = 3;
	public static final int WINNER = 4;
	public static final int TERMINATED = 5;
	private final static String FORFEIGHT = "f";
	private final static int MAXGUESS = 10;
	private Game game;
	private int guessCount;
	private int goal;
	
	/**
	 * Sets the initial game goal.
	 */
	public Agent(Game game) {
		this.game = game;
		this.goal = game.getCode().length;
		this.guessCount = 0;
	}
	/**
	 * This function is able to query the game for a clue. While all classes
	 * that have the game instance can do this, this is the only class that
	 * can interperet the clue.
	 * @Return An integer reperesentation of this agents state.
	 */
	public int generateState(String input) {

		// Client elected to forfeight.
		if (input.equals(FORFEIGHT)) {
			this.guessCount = 11;
			return TERMINATED;
		}
		// Formatting was incorrect.
		int[] clue = game.clue(input);
		if (clue == null) {
			return UNMOVED;
		} 
		// Format was correct.
		guessCount++;
		// Client guessed correct code
		if (clue[0] == goal) {
			return WINNER;
		}
		// Client has reached maximum guess limit.
		if (guessCount >= MAXGUESS) {
			return FINISHED;
		}
		// Client was incorrect but has guesses remaining.
		return INGAME;
	}

	public int getGuessCount() {
		return MAXGUESS - guessCount;
	}
}
