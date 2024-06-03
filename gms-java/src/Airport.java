import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Airport {
	
	private String name;
	private BookableSet<Terminal> terminals = new BookableSet<>();
	private BookableSet<Runway> runways = new BookableSet<>();
	private Set<Flight> flights = new HashSet<>();
	                                        
	private AirlineSet validAirlines = new AirlineSet();
	class AirlineSet extends HashSet<Airline>{
		public boolean add(Flight f) throws UndefinedAirlineException {
			Optional<Airline> oa = this.stream().filter((a)->a.getAirlineName()==f.getAirlineName()).findAny();
			if (oa.isPresent()) {
				return oa.get().add(f);
			}else {
				throw new UndefinedAirlineException();				
			}
			}
		}

	public Airport(String name) {setName(name);}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public boolean add(Terminal t) {return terminals.add(t);}
	public boolean add(Runway r) {return runways.add(r);}
	public boolean add(Airline al) {return validAirlines.add(al);}
	
	public boolean add(Gate g, String terminalName) throws UndefinedTerminalException{
		Optional<Terminal> ot = terminals.stream().filter((t)-> t.equals(new Terminal (terminalName))).findFirst();
		if (ot.isPresent()) 
			return ot.get().add(g);
		else
			throw new UndefinedTerminalException();
	}
	
	public boolean add(Flight f) throws UndefinedAirlineException {
		validAirlines.add(f);  
		return flights.add(f);
		}
	
	public void calculateSchedule() throws NotAllFlightsAssignedException { 
		boolean someNotBooked = false;
		List<Flight> fSorted = new ArrayList<>(flights);
		Collections.sort(fSorted, (f1,f2)-> f1.getArrivalTime().compareTo(f2.getArrivalTime()));
		for (Flight f: fSorted) {
			Booking runwayBooking = runways.book(new FlightInfo(f));	
			Booking gateBooking=null;
			if(runwayBooking != null) {
				f.setRunwayBooking(runwayBooking);
			    gateBooking = terminals.book(new FlightInfo(f));
			}
			f.setGateBooking(gateBooking);
			if (runwayBooking==null || gateBooking==null) { 
				someNotBooked = true;
			}
		}
		if (someNotBooked)
			throw new NotAllFlightsAssignedException();
	}
	
	public Set<FlightInfo> getFlightInfoSet() {
		Set<FlightInfo> flightInfoSet = new HashSet<>(); 
		for (Flight f : flights) {
			flightInfoSet.add(new FlightInfo(f)); 
		}
		return flightInfoSet;
	}
	public static void main (String []args) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.setTime(new java.util.Date("1/1/1601"));
		long base_1601_time = calendar.getTimeInMillis();

		calendar.setTime(new java.util.Date("1/1/1970"));
		long base_1970_time = calendar.getTimeInMillis();

		long ms_offset = base_1970_time - base_1601_time;
		long adTime = 2650466918000000000L;
		calendar.setTimeInMillis(adTime/ 10000 - ms_offset);
		System.out.println( calendar.getTime());
	}
}


