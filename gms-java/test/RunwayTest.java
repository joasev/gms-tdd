import java.time.LocalTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class RunwayTest {
	
	@Test
	public void smallPlaneCanBookAllRunwaySizes() {
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
				
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.SHORT, "00:10");
		
		Assert.assertEquals(rs.getName(), rs.book(new FlightInfo(f)).getBookedResource());
		Assert.assertEquals(rm.getName(), rm.book(new FlightInfo(f)).getBookedResource());
		Assert.assertEquals(rl.getName(), rl.book(new FlightInfo(f)).getBookedResource());
	}
	
	@Test
	public void mediumPlaneCanBookMediumAndLongRunways() {
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
				
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.MEDIUM, "00:10");
		
		Assert.assertEquals(null, rs.book(new FlightInfo(f)));
		Assert.assertEquals(rm.getName(), rm.book(new FlightInfo(f)).getBookedResource());
		Assert.assertEquals(rl.getName(), rl.book(new FlightInfo(f)).getBookedResource());
	}
	
	@Test
	public void bigPlaneCanBookOnlyLongRunways() {
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
				
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.LONG, "00:10");
		
		Assert.assertEquals(null, rs.book(new FlightInfo(f)));
		Assert.assertEquals(null, rm.book(new FlightInfo(f)));
		Assert.assertEquals(rl.getName(), rl.book(new FlightInfo(f)).getBookedResource());
	}
	
	@Test
	public void bookingWithOkOffsetSatisfiedAndEnoughInterlanding() {
		Runway r = new Runway("Runway",Runway.SHORT);
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f1));
		
		Flight f2 = new Flight("AirlineB", 2, "09:56", Runway.SHORT, "11:30");
		Booking b2 = r.book(new FlightInfo(f2));
		
		Assert.assertEquals(LocalTime.of(10, 05), b2.getFromTime());
		Assert.assertEquals("Runway", b2.getBookedResource());
		Assert.assertEquals(LocalTime.of(10, 10), b2.getToTime());
	}
	
	@Test
	public void bookingWithOkOffsetSatisfiedThroughSeveralBookingJumps() {
		Runway r = new Runway("Runway",Runway.SHORT);
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f1));
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f2));
		Flight f3 = new Flight("AirlineC", 3, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f3));
		Flight f4 = new Flight("AirlineD", 4, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f4));
		Flight f5 = new Flight("AirlineE", 5, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f5));
		Flight f6 = new Flight("AirlineF", 6, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f6));
		
		Flight f7 = new Flight("AirlineB", 7, "10:00", Runway.SHORT, "11:30");
		Booking b7 = r.book(new FlightInfo(f7));
		
		Assert.assertEquals(LocalTime.of(10, 30), b7.getFromTime());
		Assert.assertEquals("Runway", b7.getBookedResource());
		Assert.assertEquals(LocalTime.of(10, 35), b7.getToTime());
	}
	
	@Test
	public void bookingWithOkOffsetNotSatisfied() {
		Runway r = new Runway("Runway",Runway.MEDIUM);
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f1));
		Flight f2 = new Flight("AirlineB", 2, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f2));
		Flight f3 = new Flight("AirlineC", 3, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f3));
		Flight f4 = new Flight("AirlineD", 4, "10:00", Runway.MEDIUM, "11:00");
		r.book(new FlightInfo(f4));
		Flight f5 = new Flight("AirlineE", 5, "10:00", Runway.MEDIUM, "11:00");
		r.book(new FlightInfo(f5));
		Flight f6 = new Flight("AirlineF", 6, "10:00", Runway.MEDIUM, "11:00");
		r.book(new FlightInfo(f6));
		
		Flight f7 = new Flight("AirlineB", 7, "09:56", Runway.SHORT, "11:30");
		Booking b7 = r.book(new FlightInfo(f7));
		
		Assert.assertEquals(null, b7);
	}
	
	@Test
	public void bookingWithOkOffsetFittingOnlyIntoFirstGapWithEnoughInterlandingSpace() {
		Runway r = new Runway("Runway",Runway.LONG);
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f1));
		Flight f2 = new Flight("AirlineB", 2, "10:08", Runway.SHORT, "11:00");
		r.book(new FlightInfo(f2));
		Flight f3 = new Flight("AirlineC", 3, "10:16", Runway.MEDIUM, "11:00");
		r.book(new FlightInfo(f3));
		Flight f4 = new Flight("AirlineD", 4, "10:26", Runway.MEDIUM, "11:00");
		r.book(new FlightInfo(f4));
		Flight f5 = new Flight("AirlineE", 5, "10:40", Runway.LONG, "11:00");
		r.book(new FlightInfo(f5));
		Flight f6 = new Flight("AirlineF", 6, "10:50", Runway.LONG, "11:00");
		r.book(new FlightInfo(f6));
		
		Flight f7 = new Flight("AirlineB", 7, "10:00", Runway.MEDIUM, "11:30");
		Booking b7 = r.book(new FlightInfo(f7));
		
		Assert.assertEquals(LocalTime.of(10, 21), b7.getFromTime());
		Assert.assertEquals("Runway", b7.getBookedResource());
		Assert.assertEquals(LocalTime.of(10, 26), b7.getToTime());
	}
}
