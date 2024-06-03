import java.time.LocalTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class GateTest {
	
	@Test
	public void bookingWithOkOffsetAndTuraroundSatisfied() { 
		Gate g = new Gate("1");
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		g.book(new FlightInfo(f1));
		
		
		Flight f2 = new Flight("AirlineB", 2, "10:50", Runway.SHORT, "11:30");
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 50),LocalTime.of(10, 55)));
		Booking b2 = g.book(new FlightInfo(f2));
		
		Assert.assertEquals(LocalTime.of(11, 00), b2.getFromTime());
		Assert.assertEquals("1", b2.getBookedResource());
		Assert.assertEquals(LocalTime.of(11, 30), b2.getToTime());
	}
	
	@Test
	public void bookingWithOkOffsetAndTuraroundForcingDelayedDeparture() { 
		Gate g = new Gate("1");
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		g.book(new FlightInfo(f1));
		Flight f2 = new Flight("AirlineB", 1, "12:00", Runway.SHORT, "14:00");
		f2.setRunwayBooking(new Booking("AirlineB 1","Runway2",LocalTime.of(12, 00),LocalTime.of(12, 05)));
		g.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("AirlineC", 3, "10:50", Runway.MEDIUM, "11:55");
		f3.setRunwayBooking(new Booking("AirlineC 3","Runway3",LocalTime.of(10, 50),LocalTime.of(10, 55)));
		Booking b3 = g.book(new FlightInfo(f3));
		
		Assert.assertEquals(LocalTime.of(11, 00), b3.getFromTime());
		Assert.assertEquals("1", b3.getBookedResource());
		Assert.assertEquals(LocalTime.of(12, 00), b3.getToTime());
	}
	
	@Test
	public void cantBookWithOkOffsetAndTuraroundNotSatisfiable() { 
		Gate g = new Gate("1");
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		g.book(new FlightInfo(f1));
		Flight f2 = new Flight("AirlineB", 1, "11:55", Runway.SHORT, "14:00");
		f2.setRunwayBooking(new Booking("AirlineB 1","Runway2",LocalTime.of(11, 55),LocalTime.of(12, 00)));
		g.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("AirlineC", 2, "10:50", Runway.MEDIUM, "11:55");
		f3.setRunwayBooking(new Booking("AirlineC 2","Runway3",LocalTime.of(10, 50),LocalTime.of(10, 55)));
		Booking b3 = g.book(new FlightInfo(f3));
		
		Assert.assertEquals(null,b3);
	}
	
	@Test
	public void cantBookWithOffsetBiggerThanMaxTarmacWaitTime() { 
		Gate g = new Gate("1");
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		g.book(new FlightInfo(f1));
		
		
		Flight f2 = new Flight("AirlineB", 2, "10:30", Runway.SHORT, "11:30");
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 30),LocalTime.of(10, 35)));
		Booking b2 = g.book(new FlightInfo(f2));
		
		Assert.assertEquals(null,b2);
	}
}
