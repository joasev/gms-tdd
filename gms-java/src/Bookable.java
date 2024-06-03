
public interface Bookable<T> {
	                  
	public Booking book (FlightInfo flightInfo); 
			                                           
	public int bookingPriorityCompareTo(T t, FlightInfo flightInfo);

}
