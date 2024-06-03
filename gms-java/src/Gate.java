

public class Gate implements Bookable<Gate>{
	
	private static final int MAX_TARMAC_WAIT_MINUTES = 15;
	
	private String gateNumber;
	private Schedule schedule = new Schedule();
	
	public Gate(String name) {
		gateNumber=name;
	}

	public String getGateNumber() {
		return gateNumber;
	}

	public void setGateNumber(String gateNumber) {
		this.gateNumber = gateNumber;
	}

	@Override
	public Booking book(FlightInfo flightInfo) {
		return schedule.book(flightInfo.getDisplayName(),
							 getGateNumber(), 
				             flightInfo.getRunwayBooking().getFromTime(), //** This getRunwayBooking hace que se puedan sumar el tarmac y el previous waiting time
				             flightInfo.getDepartureTime(), 
				             MAX_TARMAC_WAIT_MINUTES, flightInfo.getMinimumTurnaround());
	}
	
	private int getSmallestMargin(FlightInfo flightInfo) {
		return (int) schedule.getSeparationFromPreviousBooking(flightInfo.getRunwayBooking().getFromTime(),
				            		  						   flightInfo.getDepartureTime(),
				            		  						   flightInfo.getMinimumTurnaround());
	}

	@Override
	public int bookingPriorityCompareTo(Gate otherGate, FlightInfo flightInfo) {
		int marginG1 = getSmallestMargin(flightInfo);
		int marginG2 = otherGate.getSmallestMargin(flightInfo);
		if (marginG1 == marginG2) {
			return gateNumber.compareTo(otherGate.getGateNumber());   
		} else if (marginG1 < marginG2) {
			return 1;
		}else {
			return -1;
		}
	}

	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Airline objAirline = (Airline) obj;
		return gateNumber.equals(objAirline.getAirlineName());
	}
	
	public int hashCode() { 
		return gateNumber.hashCode();
	}
	
}
