import java.util.HashSet;
import java.util.Set;

public class Airline {
	
	private String airlineName;
	private Set<Flight> flights = new HashSet<>();
	
	public Airline(String n) {
		setAirlineName(n);
	}
	public String getAirlineName() {
		return airlineName;
	}
	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}
	
	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Airline objAirline = (Airline) obj;
		return airlineName.equals(objAirline.getAirlineName());
	}
	
	public int hashCode() { 
		return airlineName.hashCode();
	}

	public boolean add(Flight f) {
		return flights.add(f); 
	}
	
}
