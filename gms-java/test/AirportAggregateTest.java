import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class AirportAggregateTest {


	@Test
	public void firstFourShouldBookSequentialGates() {
		Set<FlightInfo> fiSet = preLoadAirportWithScheduleDone();

		List<FlightInfo> fiSorted = new ArrayList<>(fiSet);
		Collections.sort(fiSorted, (f1,f2)-> f1.getArrivalTime().compareTo(f2.getArrivalTime()));
		
		Assert.assertEquals("1",fiSorted.get(0).getGateBooking().getBookedResource());
		Assert.assertEquals("2",fiSorted.get(1).getGateBooking().getBookedResource());
		Assert.assertEquals("3",fiSorted.get(2).getGateBooking().getBookedResource());
		Assert.assertEquals("4",fiSorted.get(3).getGateBooking().getBookedResource());
	}
	
	
	@Test
	public void choosingGateWithBiggestMargin() {
		Set<FlightInfo> fiSet = preLoadAirportWithScheduleDone();

		List<FlightInfo> fiSorted = new ArrayList<>(fiSet);
		Collections.sort(fiSorted, (f1,f2)-> f1.getArrivalTime().compareTo(f2.getArrivalTime()));
		
		//Gates 2 and 4 overlap, so no booking possible
		//Gate 1 has a margin of 1 hour
		//Gate 3 has a margin of half hour
		Assert.assertEquals("1",fiSorted.get(4).getGateBooking().getBookedResource());
		
		// Gates 1 and 2 overlap
		// Gate 3 has a margin of 2:30 hours
		// Gate 4 has a margin of 1:40 hours
		Assert.assertEquals("3",fiSorted.get(5).getGateBooking().getBookedResource());
		
		// Gates 1 and 2 overlap
		// Gate 3 has a margin of 0 hours
		// Gate 4 has a margin of 2:40 hours
		Assert.assertEquals("4",fiSorted.get(6).getGateBooking().getBookedResource());
		
	}
	
	
	
	
	
	
	private Set<FlightInfo> preLoadAirportWithScheduleDone() {
		Airport a = new Airport("Oxford Airport");
		
		a.add(new Terminal("T1"));
		a.add(new Terminal("T2"));
		try {
			a.add(new Gate("1"), "T1");//*** Test wrong adds
			a.add(new Gate("2"), "T1");
			a.add(new Gate("3"), "T1");
			a.add(new Gate("4"), "T1");
			a.add(new Gate("10"), "T2");
			a.add(new Gate("11"), "T2");
			a.add(new Gate("12"), "T2");
			a.add(new Gate("13"), "T2");
			a.add(new Gate("14"), "T2");
			a.add(new Gate("15"), "T2");
			a.add(new Gate("16"), "T2");
			a.add(new Gate("17"), "T2");
			a.add(new Gate("18"), "T2");
		} catch (UndefinedTerminalException e1) {
		}
		
		//Create runways, with differing lengths
		a.add(new Runway("North",Runway.LONG));
		a.add(new Runway("South",Runway.MEDIUM));

		//Create airlines
		a.add(new Airline ("OXAIR"));
		a.add(new Airline ("OXAIR"));
		a.add(new Airline ("BRITISH"));
		a.add(new Airline ("FLYCAM"));
		a.add(new Airline ("AIRFRANCE"));
		a.add(new Airline ("AERLINGUS"));
		
		
		////Create flights
		Flight f1 = new Flight("OXAIR", 1, "10:00", Runway.SHORT, "11:00");
		Flight f3 = new Flight("AERLINGUS", 45, "10:10", Runway.MEDIUM, "16:10");
		Flight f4 = new Flight("OXAIR", 1, "10:30", Runway.SHORT, "11:30");
		Flight f5 = new Flight("FLYCAM", 1, "11:00", Runway.SHORT, "12:20");
		Flight f7 = new Flight("AIRFRANCE", 909, "12:00", Runway.LONG, "19:00");
		Flight f8 = new Flight("OXAIR", 2, "14:00", Runway.SHORT, "15:00");
		Flight f9 = new Flight("FLYCAM", 6, "15:00", Runway.SHORT, "16:20");



		try {
			a.add(f1);
			a.add(f3);
			a.add(f4);
			a.add(f5);
			a.add(f7);
			a.add(f8);
			a.add(f9);
			
		} catch (UndefinedAirlineException e) {
		}
		
		try{
			a.calculateSchedule();
		}catch (NotAllFlightsAssignedException e) {
		}
		Set<FlightInfo> fiSet = a.getFlightInfoSet();
		ConsoleLogger.logSchedule(a.getName(), fiSet);
		
		return fiSet;
	}
}
