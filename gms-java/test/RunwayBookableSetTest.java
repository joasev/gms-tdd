import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import org.junit.Assert;

public class RunwayBookableSetTest {
	
	//************* SAY HERE AIRLINES DONT MATTER
	
	@Test
	public void prefersBookingShortAndSpaced() { 
		BookableSet<Runway> runways = preloadEmptyBookableSet();
		
		Flight f = new Flight("AirlineX", 200, "10:10", Runway.SHORT, "11:00");
		Booking b = runways.book(new FlightInfo(f));
		
		Assert.assertEquals(LocalTime.of(10, 10), b.getFromTime());
		Assert.assertEquals("ShortRunway1", b.getBookedResource());
		Assert.assertEquals(LocalTime.of(10,15), b.getToTime());
	}
	
	@Test
	public void prefersBookingMediumAndSpaced() { 
		BookableSet<Runway> runways = preloadEmptyBookableSet();
		
		Flight f = new Flight("AirlineX", 200, "10:10", Runway.MEDIUM, "11:00");
		Booking b = runways.book(new FlightInfo(f));
		
		Assert.assertEquals(LocalTime.of(10, 10), b.getFromTime());
		Assert.assertEquals("MediumRunway1", b.getBookedResource());
		Assert.assertEquals(LocalTime.of(10,15), b.getToTime());
	}
	
	@Test
	public void prefersBookingLongAndSpaced() { 
		BookableSet<Runway> runways = preloadEmptyBookableSet();
		
		Flight f = new Flight("AirlineX", 200, "10:10", Runway.LONG, "11:00");
		Booking b = runways.book(new FlightInfo(f));
		
		Assert.assertEquals(LocalTime.of(10, 10), b.getFromTime());
		Assert.assertEquals("LongRunway1", b.getBookedResource());
		Assert.assertEquals(LocalTime.of(10,15), b.getToTime());
	}
	
	@Test
	public void smallestshiftWithinSmallestSuitableTakesPreferenceOverBiggerRunway() { 
		BookableSet<Runway> runways = preloadEmptyBookableSet();
		
		Flight f1 = new Flight("AirlineX", 200, "10:05", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f1));
		Flight f = new Flight("AirlineX", 201, "10:05", Runway.SHORT, "11:00");
		Booking b = runways.book(new FlightInfo(f));
		
		Assert.assertEquals(LocalTime.of(10, 07), b.getFromTime());
		Assert.assertEquals("ShortRunway2", b.getBookedResource());
		Assert.assertEquals(LocalTime.of(10,12), b.getToTime());
	}
	
	@Test
	public void smallestshiftWithinSmallestSuitableTakesPreferenceOverBiggerOrSmallerRunway() { 
		BookableSet<Runway> runways = preloadEmptyBookableSet();
		
		Flight f1M = new Flight("AirlineXM", 300, "10:05", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f1M));
		Flight fM = new Flight("AirlineX", 301, "10:05", Runway.MEDIUM, "11:00");
		Booking bM = runways.book(new FlightInfo(fM));

		Assert.assertEquals(LocalTime.of(10, 07), bM.getFromTime());
		Assert.assertEquals("MediumRunway2", bM.getBookedResource());
		Assert.assertEquals(LocalTime.of(10,12), bM.getToTime());
	}
	

	
	private BookableSet<Runway> preloadEmptyBookableSet(){
		BookableSet<Runway> runways = new BookableSet<>();
		
		runways.add(new Runway("ShortRunway1",Runway.SHORT));
		Flight f1 = new Flight("AirlineA", 1, "10:00", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f1));
		runways.add(new Runway("ShortRunway2",Runway.SHORT));
		Flight f2 = new Flight("AirlineB", 2, "10:02", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f2));
		runways.add(new Runway("ShortRunway3",Runway.SHORT));
		Flight f3 = new Flight("AirlineC", 3, "10:07", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f3));
		runways.add(new Runway("ShortRunway4",Runway.SHORT));
		Flight f4 = new Flight("AirlineD", 4, "10:12", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f4));
		Flight f4a = new Flight("AirlineX", 100, "10:04", Runway.SHORT, "11:00");
		runways.book(new FlightInfo(f4a));
		
		runways.add(new Runway("MediumRunway1",Runway.MEDIUM));
		Flight f5 = new Flight("AirlineE", 5, "10:00", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f5));
		runways.add(new Runway("MediumRunway2",Runway.MEDIUM));
		Flight f6 = new Flight("AirlineF", 6, "10:02", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f6));
		runways.add(new Runway("MediumRunway3",Runway.MEDIUM));
		Flight f7 = new Flight("AirlineG", 7, "10:07", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f7));
		runways.add(new Runway("MediumRunway4",Runway.MEDIUM));
		Flight f8 = new Flight("AirlineH", 8, "10:12", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f8));
		Flight f8a = new Flight("AirlineX", 101, "10:04", Runway.MEDIUM, "11:00");
		runways.book(new FlightInfo(f8a));
		
		runways.add(new Runway("LongRunway1",Runway.LONG));
		Flight f9 = new Flight("AirlineI", 9, "10:00", Runway.LONG, "11:00");
		runways.book(new FlightInfo(f9));
		runways.add(new Runway("LongRunway2",Runway.LONG));
		Flight f10 = new Flight("AirlineJ", 10, "10:02", Runway.LONG, "11:00");
		runways.book(new FlightInfo(f10));
		runways.add(new Runway("LongRunway3",Runway.LONG));
		Flight f11 = new Flight("AirlineK", 11, "10:07", Runway.LONG, "11:00");
		runways.book(new FlightInfo(f11));
		runways.add(new Runway("LongRunway4",Runway.LONG));
		Flight f12 = new Flight("AirlineL", 12, "10:12", Runway.LONG, "11:00");
		runways.book(new FlightInfo(f12));
		Flight f12a = new Flight("AirlineX", 102, "10:04", Runway.LONG, "11:00");
		runways.book(new FlightInfo(f12a));
		
		
		return runways;
	}
}
