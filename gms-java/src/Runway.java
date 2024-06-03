

public class Runway implements Bookable<Runway>{
	
	public static final int SHORT = 1;
	public static final int MEDIUM = 2;
	public static final int LONG = 3;
	
	private static final int MAX_CIRCULATION_BEFORE_LANDING = 30;
	private static final int INTERLANDING_GAP = 5;
	
	private String name;
	private int length;
	private Schedule schedule = new Schedule();
	
	
	public Runway(String n, int length) {
		setName(n);
		this.setLength(length);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public Booking book(FlightInfo flightInfo) {
		if (isSizeSuitableForBooking(flightInfo)) {
			return schedule.book(flightInfo.getDisplayName(),
					             getName(), 
					             flightInfo.getArrivalTime(), 
					             flightInfo.getArrivalTime().plusMinutes(INTERLANDING_GAP), 
					             MAX_CIRCULATION_BEFORE_LANDING, 
					             INTERLANDING_GAP);			
		}
		return null;
	}
	
		
	private boolean isSizeSuitableForBooking(FlightInfo flightInfo) {
		int minimumSizeNeeded = flightInfo.getRunwaySize();
		if (length==minimumSizeNeeded) {return true;}
		if (length==SHORT) {
			if(minimumSizeNeeded==MEDIUM || minimumSizeNeeded==LONG) {return false;}
		}else if (length==MEDIUM) {
			if(minimumSizeNeeded==LONG) {return false;}
		}
		return true;
	}

	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Runway objRunway = (Runway) obj;
		return name.equals(objRunway.getName());
	}

	public int hashCode() { 
		return name.hashCode();
	}

	
	@Override
	public int bookingPriorityCompareTo(Runway otherRunway, FlightInfo flightInfo) {
		int minimumSizeNeeded = flightInfo.getRunwaySize();
		if (length==minimumSizeNeeded) {return sameSizeRunwayCompareTo(otherRunway, flightInfo);}
		if (length==SHORT) {
			if(minimumSizeNeeded==MEDIUM || minimumSizeNeeded==LONG) {return -1;}
		}else if (length==MEDIUM) {
			if(minimumSizeNeeded==LONG) {return -1;}
		}
		return 1;
	}
	//*** EH???? private funciona??
	private int getSmallestMargin(FlightInfo flightInfo) {
		return (int) schedule.getSeparationFromPreviousBooking(
				                       flightInfo.getArrivalTime(),
                                       flightInfo.getArrivalTime().plusMinutes(INTERLANDING_GAP),
                                       INTERLANDING_GAP);
	}
	
	private int sameSizeRunwayCompareTo(Runway otherRunway, FlightInfo flightInfo) {
			int marginR1 = getSmallestMargin(flightInfo);
			int marginR2 = otherRunway.getSmallestMargin(flightInfo);
			if (marginR1 == marginR2) {
				return name.compareTo(otherRunway.getName());
			} else if (marginR1 < marginR2) {
				return 1;
			}else {
				return -1;
			}
	}


}


