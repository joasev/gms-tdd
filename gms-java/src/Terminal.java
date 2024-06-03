

public class Terminal implements Bookable <Terminal>{

	private String name;
	private BookableSet<Gate> gates = new BookableSet<>();  

	public Terminal(String name){ 
		this.name=name;	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean add(Gate gate) {
		return gates.add(gate);
	}

	@Override
	public Booking book(FlightInfo flightInfo) {
		return gates.book(flightInfo);
	}
	
	public boolean equals (Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Terminal objTerminal = (Terminal) obj;
		return name.equals(objTerminal.getName());
	}

	public int hashCode() { 
		return name.hashCode();
	}

	@Override
	public int bookingPriorityCompareTo(Terminal otherTerminal, FlightInfo flightInfo) {
		return name.compareTo(otherTerminal.getName());
	}

}


