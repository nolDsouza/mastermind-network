package test;

import utility.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.StringBuilder;
public class TestMessage {
	protected Message message;
	protected final String real = "[John Doe]: Test Message";

	@Before
	public void setUp() {
		message = new Message("John Doe", "Test Message");
	}

	@Test
	public void testConstruction() {
		assertEquals("[John Doe]: Test Message", message.toString()); 
	}

	@Test
	public void testParserName() {
		assertEquals(new Message(real).getMessenger(), "John Doe");
	}
	
	@Test
	public void testParserMessage() {
		assertEquals(new Message(real).getContents(), "Test Message");
	}
	
	@Test
	public void testTrickyParse() {
		assertEquals(new Message("[]]:]: ]: ba").getMessenger(), "]]:]: "); 
	}
}

