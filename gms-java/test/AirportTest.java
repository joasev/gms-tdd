import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AirportTest {

	@Test
	public void nameGetsSet() {
		Airport a = new Airport("Oxford Airport");
		Assert.assertEquals("Oxford Airport",a.getName());
	}
	
	
	
	@Test
	public void oneFlightGetsAdded() {
		Airport a = new Airport("Oxford Airport");
		a.add(new Airline ("OXAIR"));
		Flight f = new Flight("OXAIR", 1, "10:00", Runway.SHORT, "11:00");
		try {
			a.add(f);
		} catch (UndefinedAirlineException e) {
			Assert.fail();
		}
		
		Set<FlightInfo> fiSet = a.getFlightInfoSet(); //** Este pattern es al reves del new FlightInfo(Flight f)
		
		Assert.assertEquals(1, fiSet.size());
		Assert.assertTrue(fiSet.contains(new FlightInfo(f))); 
	}
	
	@Test
	public void secondFlightGetsAdded() {
		Airport a = new Airport("Oxford Airport");
		a.add(new Airline ("OXAIR"));
		Flight f1 = new Flight("OXAIR", 1, "10:00", Runway.SHORT, "11:00");
		a.add(new Airline ("RYANAIR"));
		Flight f2 = new Flight("RYANAIR", 666, "10:05", Runway.MEDIUM, "10:30");
		try {
			a.add(f1);
			a.add(f2);
		} catch (UndefinedAirlineException e) {
			Assert.fail();
		}
		
		
		Set<FlightInfo> fiSet = a.getFlightInfoSet(); //** Este pattern es al reves del new FlightInfo(Flight f)
		
		Assert.assertEquals(2, fiSet.size());
		Assert.assertTrue(fiSet.contains(new FlightInfo(f1))); 
		Assert.assertTrue(fiSet.contains(new FlightInfo(f2))); 
	}
	
	/*
	@Test
	public void repeatedFlightIdNotAdded() {

		Assert.assertTrue(false); 
	}*/
	
	@Test //********(expected = UndefinedAirlineException.class)
	public void tryAddFlightInvalidAirline() { //**********Este check lo hace la clase airport. Consistency con la solucion de check gateway unique!
		Airport a = new Airport("Oxford Airport");
		Flight f = new Flight("WRONG_AIRLINE", 1, "10:00", Runway.SHORT, "11:00");
		 
		Assertions.assertThrows(UndefinedAirlineException.class, () -> a.add(f));
	}
	
	@Test 
	public void canAddOneTerminal() { 
		Airport a = new Airport("Oxford Airport");
		
		Assert.assertTrue(a.add(new Terminal("T1")));
	}
	
	@Test 
	public void canAddTwoTerminals() { 
		Airport a = new Airport("Oxford Airport");
		
		Assert.assertTrue(a.add(new Terminal("T1")));
		Assert.assertTrue(a.add(new Terminal("T2")));
	}
	
	@Test 
	public void canAddGateToAirportWithExistingTerminal() { 
		Airport a = new Airport("Oxford Airport");
		a.add(new Terminal("T1"));
		
		try {
			Assert.assertTrue(a.add(new Gate("1"), "T1"));
		} catch (UndefinedTerminalException e1) {
			Assert.fail();
		}
	}
	
	@Test
	public void cantAddGateToAirportWithWrongTerminal() { 
		Airport a = new Airport("Oxford Airport");
		a.add(new Terminal("T1"));
		
		Assertions.assertThrows(UndefinedTerminalException.class, () -> a.add(new Gate("1"), "TWrong"));

	}
	

	
}
