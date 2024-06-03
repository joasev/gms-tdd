import java.time.LocalTime;

public class Flight {
	
	private static final int MINIMUM_TURNAROUND_SMALL = 30;
	private static final int MINIMUM_TURNAROUND_MEDIUM = 60;
	private static final int MINIMUM_TURNAROUND_LARGE = 90;
	
	private String airlineName;
    private int uniqueId;
	private LocalTime arrivalTime;
	private int runwaySize;
	private LocalTime departureTime;
	
	private Booking runwayBooking;
	private Booking gateBooking;
	

	public Flight(String anm, int id, String atm, int r, String dtm) {
		setAirlineName(anm);
		setUniqueId(id);
		setArrivalTime(atm);
		setRunwaySize(r);
		setDepartureTime(dtm);	
	}
	
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = LocalTime.parse(departureTime); 
	}
	public int getRunwaySize() {
		return runwaySize;
	}
	public void setRunwaySize(int runwaySize) {
		this.runwaySize = runwaySize;
	}
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = LocalTime.parse(arrivalTime);
	}
	public int getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getAirlineName() {
		return airlineName;
	}
	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}
	
	public String toString() {
		return 	airlineName +" "+ uniqueId +" "+ arrivalTime +" "+ runwaySize +" "+ departureTime;
	}
	
	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Flight objFlight = (Flight) obj;
		return (uniqueId == objFlight.getUniqueId()
				&& airlineName.equals(objFlight.getAirlineName())
				);
	}
	public int hashCode() { 
		return (airlineName+uniqueId).hashCode();
	}
	
	public Booking getRunwayBooking() {
		return runwayBooking;
	}
	public void setRunwayBooking(Booking runwayBooking) {
		this.runwayBooking = runwayBooking;
	}
	public Booking getGateBooking() {
		return gateBooking;
	}
	public void setGateBooking(Booking gateBooking) {
		this.gateBooking = gateBooking;
	}
	
	public int getMinimumTurnaroundTime() {
		if (runwaySize == Runway.SHORT) {
			return MINIMUM_TURNAROUND_SMALL;
		}else if (runwaySize == Runway.MEDIUM) {
			return MINIMUM_TURNAROUND_MEDIUM;
		}else {
			return MINIMUM_TURNAROUND_LARGE;
		}
	}

	

}
