import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import org.junit.Assert;

public class TerminalBookableSetTest {
	
	//*********TerminalTest not needed because it is just a bookableSet of Gates
	//*********Tested already
		
	@Test
	public void prefersAlphabeticalOrder() { //********* Explain I would expect in reality preerred terminals by airline
		BookableSet<Terminal> terminals = preloadTerminalSet();
		
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		f.setRunwayBooking(new Booking("RYANAIR 1","Runway1", LocalTime.of(10,00), LocalTime.of(10,05)));
		Booking b = terminals.book(new FlightInfo(f));
		
		Assert.assertEquals(LocalTime.of(10, 00), b.getFromTime());
		Assert.assertEquals("Gate1-1", b.getBookedResource());
		Assert.assertEquals(LocalTime.of(11, 00), b.getToTime());
	}
	
	@Test
	public void prefersOtherGateInTerminalThanUnpreferredTerminal() { 
		BookableSet<Terminal> terminals = preloadTerminalSet();
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		f.setRunwayBooking(new Booking("RYANAIR 1","Runway1", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f));
		
		Flight f2 = new Flight("RYANAIR", 2, "10:00", Runway.SHORT, "11:00");
		f2.setRunwayBooking(new Booking("RYANAIR 2","Runway2", LocalTime.of(10,00), LocalTime.of(10,05)));
		Booking b2 = terminals.book(new FlightInfo(f2));
		
		Assert.assertEquals(LocalTime.of(10, 00), b2.getFromTime());
		Assert.assertEquals("Gate1-2", b2.getBookedResource());
		Assert.assertEquals(LocalTime.of(11, 00), b2.getToTime());
	}
	
	@Test
	public void prefersShiftGateTimeInTerminalThanUnpreferredTerminal() { 
		BookableSet<Terminal> terminals = preloadTerminalSet();
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		f.setRunwayBooking(new Booking("RYANAIR 1","Runway1", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f));
		
		Flight f2 = new Flight("RYANAIR", 2, "10:00", Runway.SHORT, "11:00");
		f2.setRunwayBooking(new Booking("RYANAIR 2","Runway2", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("RYANAIR", 3, "10:45", Runway.SHORT, "11:45");
		f3.setRunwayBooking(new Booking("RYANAIR 3","Runway3", LocalTime.of(10,45), LocalTime.of(10,50)));
		Booking b3 = terminals.book(new FlightInfo(f3));
		
		Assert.assertEquals(LocalTime.of(11, 00), b3.getFromTime());
		Assert.assertEquals("Gate1-1", b3.getBookedResource());
		Assert.assertEquals(LocalTime.of(11, 45), b3.getToTime());
	}
	
	@Test
	public void choosesSecondTerminalWhenShiftIsBiggerThanTarmacWaitingTime() { 
		BookableSet<Terminal> terminals = preloadTerminalSet();
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		f.setRunwayBooking(new Booking("RYANAIR 1","Runway1", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f));
		
		Flight f2 = new Flight("RYANAIR", 2, "10:00", Runway.SHORT, "11:00");
		f2.setRunwayBooking(new Booking("RYANAIR 2","Runway2", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("RYANAIR", 3, "10:44", Runway.SHORT, "11:45");
		f3.setRunwayBooking(new Booking("RYANAIR 3","Runway3", LocalTime.of(10,44), LocalTime.of(10,49)));
		Booking b3 = terminals.book(new FlightInfo(f3));
		
		Assert.assertEquals(LocalTime.of(10, 44), b3.getFromTime());
		Assert.assertEquals("Gate2-1", b3.getBookedResource());
		Assert.assertEquals(LocalTime.of(11, 45), b3.getToTime());
	}
	
	@Test
	public void cantBookWhenNoShiftIsPossibleInAnyGateInAnyTerminal() { 
		BookableSet<Terminal> terminals = preloadTerminalSet();
		Flight f = new Flight("RYANAIR", 1, "10:00", Runway.SHORT, "11:00");
		f.setRunwayBooking(new Booking("RYANAIR 1","Runway1", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f));
		
		Flight f2 = new Flight("RYANAIR", 2, "10:00", Runway.SHORT, "11:00");
		f2.setRunwayBooking(new Booking("RYANAIR 2","Runway2", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("RYANAIR", 3, "10:00", Runway.SHORT, "11:00");
		f3.setRunwayBooking(new Booking("RYANAIR 3","Runway3", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f3));
		
		Flight f4 = new Flight("RYANAIR", 4, "10:00", Runway.SHORT, "11:00");
		f4.setRunwayBooking(new Booking("RYANAIR 4","Runway4", LocalTime.of(10,00), LocalTime.of(10,05)));
		terminals.book(new FlightInfo(f4));
		
		Flight f5 = new Flight("RYANAIR", 5, "10:00", Runway.SHORT, "11:00");
		f5.setRunwayBooking(new Booking("RYANAIR 5","Runway5", LocalTime.of(10,00), LocalTime.of(10,05)));
		Booking b5 = terminals.book(new FlightInfo(f5));

		Assert.assertEquals(null, b5);
	}
	
	private BookableSet<Terminal> preloadTerminalSet(){
		BookableSet<Terminal> terminals = new BookableSet<>();
		Terminal t1 = new Terminal("T1");
		t1.add(new Gate("Gate1-1")); //*********** Inbloqueable! No puedo chequear unicidad de gate
		t1.add(new Gate("Gate1-2"));
		Terminal t2 = new Terminal("T2");
		t2.add(new Gate("Gate2-1"));
		t2.add(new Gate("Gate2-2"));
		terminals.add(t1);
		terminals.add(t2);
		
		return terminals;
	}
		
}
