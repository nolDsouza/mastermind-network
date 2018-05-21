package test;

import game.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.StringBuilder;
public class TestAgent {
	protected Game game;
	protected Agent agent;

	@Before
	public void setUp() throws IOException {
		game = new Game(8);
		Agent agent = new Agent(game);
	}
	@Test
	public void testConsistent() {
		assertEquals(Agent.UNMOVED, agent.generateState("1234a678"));
	}
	
	@Test
	public void testGame() {
		assertEquals(Agent.INGAME, agent.generateState("12345677"));
	}
	
	@Test
	public void testConsistent2() {
		agent.generateState("12345677");
		assertEquals(Agent.INGAME, agent.generateState("77777777"));
	}

	@Test
	public void testForfeight() {
		assertEquals(Agent.TERMINATED, agent.generateState("f"));
	}

	@Test
	public void testNearUnsuccessfull() {
		for (int i=0; i<6; i++) {
			agent.generateState("77777777");
		}
		assertEquals(Agent.INGAME, agent.generateState("77777777"));
	}

	@Test
	public void testUnsuccessfull() {
		for (int i=0; i<7; i++) {
			agent.generateState("77777777");
		}
		assertEquals(Agent.FINISHED, agent.generateState("77777777"));
	}
	
	@Test
	public void testNull1() {
		assertEquals(Agent.UNMOVED, agent.generateState(""));
	}
	
	@Test
	public void testNull2() {
		agent.generateState("12345678");
		assertEquals(Agent.UNMOVED, agent.generateState(""));
	}

	@Test
	public void testCorrect() {
		StringBuilder sb = new StringBuilder();
		for (int i : game.getCode()) {
			sb.append(i);
		}
		assertEquals(Agent.WINNER, agent.generateState(sb.toString()));
	}
	
}
