import java.time.LocalTime;

final public class FlightInfo {
	private final String airlineName;
    private final int uniqueId;
	private final LocalTime arrivalTime;
	private final int runwaySize;
	private final LocalTime departureTime;
	private final int minimumTurnaround;
	private final Booking runwayBooking;
	private final Booking gateBooking;
	

	public FlightInfo (final Flight f) {
		airlineName = f.getAirlineName();
		uniqueId = f.getUniqueId();
		arrivalTime = f.getArrivalTime();
		runwaySize = f.getRunwaySize();
		departureTime = f.getDepartureTime();
		runwayBooking = f.getRunwayBooking();
		gateBooking = f.getGateBooking();
		minimumTurnaround = f.getMinimumTurnaroundTime();
	}
	
	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		FlightInfo objFI = (FlightInfo) obj;
		return (uniqueId == objFI.getUniqueId()
				&& airlineName.equals(objFI.getAirlineName())
				&& arrivalTime.equals(objFI.getArrivalTime())
				);
	}
	public int hashCode() {
		return uniqueId;
	}
	
	public String getAirlineName() {
		return airlineName;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public int getRunwaySize() {
		return runwaySize;
	}
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	
	public Booking getRunwayBooking() {
		return runwayBooking;
	}
	public Booking getGateBooking() {
		return gateBooking;
	}
	public int getMinimumTurnaround() {
		return minimumTurnaround;
	}
	
	public String getDisplayName() {
		return airlineName + " " +uniqueId;
	}
}
