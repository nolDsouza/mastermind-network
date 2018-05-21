package test;

import org.junit.*;
import static org.junit.Assert.assertEquals;

public class TestJunit {

	@Test
	public void testWillAlwaysPass() {
		String str = "Junit is working fine";
		assertEquals("Junit is working fine", str);
	}
}
