import java.time.LocalTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class GateBookableSetTest {
	
	@Test
	public void firstBookigPrefersAlphabetical() { 
		BookableSet<Gate> gates = new BookableSet<>();

		gates.add(new Gate("Gate1"));
		gates.add(new Gate("Gate2"));

		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		Booking b1 = gates.book(new FlightInfo(f1));
		
		Assert.assertEquals(f1.getArrivalTime(), b1.getFromTime());
		Assert.assertEquals("Gate1", b1.getBookedResource());
		Assert.assertEquals(f1.getDepartureTime(), b1.getToTime());
	}

	@Test
	public void bookingPrefersBiggestGapFromPreviousBooking() { 
		BookableSet<Gate> gates = new BookableSet<>();

		gates.add(new Gate("Gate1"));
		gates.add(new Gate("Gate2"));

		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "10:30");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		gates.book(new FlightInfo(f1));
		gates.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("AirlineC", 3, "12:00", Runway.SHORT, "14:00");
		f3.setRunwayBooking(new Booking("AirlineC 3","Runway3",LocalTime.of(12, 00),LocalTime.of(12, 05)));
		Booking b3 = gates.book(new FlightInfo(f3));
		
		//Natural order without the 'biggest gap preference' would be alphabetical (would book Gate1)
		Assert.assertEquals(f3.getArrivalTime(), b3.getFromTime());
		Assert.assertEquals("Gate2", b3.getBookedResource());
		Assert.assertEquals(f3.getDepartureTime(), b3.getToTime());
	}
	
	@Test
	public void bookingPrefersEmptyGateWhenNoOffsetNeeded() { 
		BookableSet<Gate> gates = new BookableSet<>();

		gates.add(new Gate("Gate1"));
		gates.add(new Gate("Gate2"));

		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "10:40");
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "10:30");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		gates.book(new FlightInfo(f1));
		gates.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("AirlineC", 3, "10:30", Runway.SHORT, "12:00");
		f3.setRunwayBooking(new Booking("AirlineC 3","Runway3",LocalTime.of(10, 30),LocalTime.of(10, 35)));
		Booking b3 = gates.book(new FlightInfo(f3));
		
		//Natural order without the 'no offset preference' would be alphabetical (would book Gate1)
		Assert.assertEquals(f3.getArrivalTime(), b3.getFromTime());
		Assert.assertEquals("Gate2", b3.getBookedResource());
		Assert.assertEquals(f3.getDepartureTime(), b3.getToTime());
	}
	
	
	@Test
	public void bookingPrefersSmallestShift() { 
		BookableSet<Gate> gates = new BookableSet<>();

		gates.add(new Gate("Gate1"));
		gates.add(new Gate("Gate2"));

		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "10:41");
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "10:40");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		gates.book(new FlightInfo(f1));
		gates.book(new FlightInfo(f2));
		
		Flight f3 = new Flight("AirlineC", 3, "10:30", Runway.SHORT, "12:00");
		f3.setRunwayBooking(new Booking("AirlineC 3","Runway3",LocalTime.of(10, 30),LocalTime.of(10, 35)));
		Booking b3 = gates.book(new FlightInfo(f3));
		
		//Natural order without the 'smallest offset preference' would be alphabetical (would book Gate1)
		Assert.assertEquals(f3.getArrivalTime().plusMinutes(10), b3.getFromTime());
		Assert.assertEquals("Gate2", b3.getBookedResource());
		Assert.assertEquals(f3.getDepartureTime(), b3.getToTime());
	}
	
	@Test
	public void bookingPrefersBiggerShiftIfSmallerShiftDoesntFit() { 
		BookableSet<Gate> gates = new BookableSet<>();

		gates.add(new Gate("Gate1"));
		gates.add(new Gate("Gate2"));

		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "10:41");
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "10:40");
		Flight f3 = new Flight("AirlineC", 3, "10:50", Runway.SHORT, "11:50");
		f1.setRunwayBooking(new Booking("AirlineA 1","Runway1",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		f2.setRunwayBooking(new Booking("AirlineB 2","Runway2",LocalTime.of(10, 00),LocalTime.of(10, 05)));
		f3.setRunwayBooking(new Booking("AirlineC 3","Runway3",LocalTime.of(10, 50),LocalTime.of(10, 55)));
		gates.book(new FlightInfo(f1));
		gates.book(new FlightInfo(f2));
		gates.book(new FlightInfo(f3));
		
		Flight f4 = new Flight("AirlineD", 4, "10:30", Runway.SHORT, "12:00");
		f4.setRunwayBooking(new Booking("AirlineD 4","Runway4",LocalTime.of(10, 30),LocalTime.of(10, 35)));
		Booking b4 = gates.book(new FlightInfo(f4));
		
		//Gate 2 requires a smaller shift, but it doesn't fit so Gate 1 is preferred
		Assert.assertEquals(f4.getArrivalTime().plusMinutes(11), b4.getFromTime());
		Assert.assertEquals("Gate1", b4.getBookedResource());
		Assert.assertEquals(f4.getDepartureTime(), b4.getToTime());
	}
}
