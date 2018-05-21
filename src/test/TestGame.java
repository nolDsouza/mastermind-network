package test;

import game.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.lang.StringBuilder;
public class TestGame {
	protected Game game;

	@Test
	public void testUniqueness() {
		game = new Game(9);
		int count = 0;
		for (int i=0; i<5; i++) {
			for (int j : game.getCode()) {
				if (i == j) {
					count++;
				}
			}
		assertTrue(count < 2);
		count = 0;
		}

	}
	
	@Test
	public void testSurjectiveness() {
		game = new Game(10);
		int count = 0;
		for (int i=0; i<9; i++) {
			for (int j : game.getCode()) {
				if (i == j) {
					count++;
				}
			}
		assertTrue(count == 1);
		count = 0;
		}
	}

	@Test
	public void testIncorrectFormatQuery1() {
		assertTrue(new Game(8).clue("badstring") == null);
	}

	@Test
	public void testIncorrectFormatQuery2() {
		assertTrue(new Game(8).clue("1234567a") == null);
	}

	@Test
	public void testIncorrectFormatQuery3() {
		assertTrue(new Game(8).clue("a0918273") == null);
	}
	
	@Test
	public void testIncorrectFormatQuery4() {
		assertTrue(new Game(2).clue("666") == null);
	}
	
	@Test
	public void testIncorrectFormatQuery5() {
		assertTrue(new Game(8).clue("1029385") == null);
	}

	@Test
	public void testCorrectFormatQuery1() {
		assertTrue(new Game(8).clue("12345678") != null);
	}

	@Test
	public void testFull() {
		game = new Game(10);
		int[] clue = game.clue("1111111111");
		assertEquals(1, clue[0]);
		assertEquals(9, clue[1]);
	}

	@Test
	public void testCorrect() {
		game = new Game(8);
		StringBuilder sb = new StringBuilder();
		for (int i : game.getCode()) {
			sb.append(i);
		}
		int[] clue = game.clue(sb.toString());
		assertEquals(8, clue[0]);
		assertEquals(0, clue[1]);
	}
}
