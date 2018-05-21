package game;

import java.util.Stack;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.lang.StringBuilder;
/******************************************************************************
 * This class encapsulates all details for the mastermind game. The game itself
 * is not expected to be instantiated more than once. A game instance generates
 * a random code. It also contains a transferrable game. The transferrable 
 * game can change its own instance. States can query the game to based
 * on a given guess. States will make transition this games game to their own
 * based on the context.
 *****************************************************************************/
public class Game {
	final int CLUE_LEN = 2;
	final int CORRECT = 0;
	final int INCORRECT = 1;
	private Set<Integer> digits;
	private int[] code;
	private int[] clue;
	/**
	 * @Param codeLen: determines the length of the games code.
	 */
	public Game(int codeLen) {
		code = new int[codeLen];
		clue = new int[CLUE_LEN];
		digits = new HashSet<Integer>(CLUE_LEN);
		generateCode();
	}
	
	/**
	 * Get numerical reperesentation of generated code.
	 */
	public int[] getCode() {
		return code;
	}

	/**
	 * Get the generated code as a readable string.
	 */
	public String getPattern() {
		StringBuilder sb = new StringBuilder();
		for (int i : code) {
			sb.append(i);
		}
		return sb.toString();
	}
	/**
	 * Calculate two numbers, the number of correct elements in the correct
	 * place and the number of correct elements in the incorrect place.
	 * The calculation is done in one pass.
	 * @Return These numbers in an arrray tuple.
	 */
	public int[] clue(String input) {
		// Validation.
		if (input.length() != code.length) {
			return null;
		}

		clue[CORRECT] = 0;
		clue[INCORRECT] = 0;
		for (int i=0; i<input.length(); i++) {
			// Numerical validation.
			if (!Character.isDigit(input.charAt(i))) {
				return null;
			}
			int temp = Character.getNumericValue(input.charAt(i));
			// Check if element is in code.
			if (digits.contains(temp)) {
				// Check element is in correct position.
				if (code[i] == temp) {
					clue[CORRECT]++;
				} else {
					clue[INCORRECT]++;
				}
			}
		}
		return clue;
	}
	/**
	 * Returns the clue as a string, assumes clue() called first.
	 * @Return the clue as a readable sentence.
	 */
	public String formatClue() {
		return String.format("Correct positions: %s Incorrect Positions: %s",
				clue[CORRECT], clue[INCORRECT]);
	}
	/**
	 * Generate a random code of a given length. each element of the code
	 * is unique (no duplicates). length is in the range [3-8].
	 */
	private void generateCode() {
		// Using stack for shuffle.
		final Stack<Integer> digitStack = new Stack<Integer>();
		// Add all possble symbols to stack.
		for (int i=0; i<10; i++) {
			digitStack.push(i);
		}
		// Shuffle the stack.
	 	Collections.shuffle(digitStack);	
		// Pop elements into code n times.
		for (int i=0; i<code.length; i++) {
			int temp = digitStack.pop();
			// This is the actual code.
			code[i] = temp;
			// This set provides a useful contains method.
			digits.add(temp);
		}
	}
}
