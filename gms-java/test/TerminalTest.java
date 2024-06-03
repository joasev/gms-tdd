import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TerminalTest {
	
	@Test
	public void cantBookTerminalWithoutGatesButNoExceptionThrown() { 
		Terminal terminals = new Terminal("T1");
		
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		Booking b = terminals.book(new FlightInfo(f));

		
		
		
		
		Assert.assertEquals(null, b);
	}
}
