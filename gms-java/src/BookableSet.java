import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class BookableSet<T extends Bookable<T>> extends HashSet<T> { 
		
	public Booking book (FlightInfo flightInfo) { 
		List<T> prioritizedBookingCandidates = getPrioritizedBookingCandidates(flightInfo);
		for (T t : prioritizedBookingCandidates) {
			Booking booking = t.book(flightInfo);  
			if(booking!=null) {
				return booking;
				}
			}
		return null;
		}
	
	private List<T> getPrioritizedBookingCandidates(FlightInfo flightInfo) {
		List<T> prioritizedBookingCandidates = new ArrayList<>(this);
		Collections.sort(prioritizedBookingCandidates, 
			          (r1, r2) -> r1.bookingPriorityCompareTo(r2,flightInfo)); 
		return prioritizedBookingCandidates;
	}	
}
