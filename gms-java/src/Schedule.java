import java.time.Duration;
import java.time.LocalTime;
import java.util.TreeSet;

 
public class Schedule{
	
	
	private TreeSet<Booking> existingBookings = new TreeSet<>(
								(b1,b2)-> b1.getFromTime().compareTo(b2.getFromTime()) );
	
	
	public int getSeparationFromPreviousBooking(LocalTime bookFrom, LocalTime bookTo, int minimumBookingLength) {
		if (existingBookings.isEmpty()) {
			return (int) Duration.between(LocalTime.of(0, 0), LocalTime.of(23, 59)).toMinutes();
			
		}else if (existingBookings.first().getFromTime().compareTo(bookTo) >= 0){
			return (int) Duration.between(LocalTime.of(0, 0), bookFrom).toMinutes();
			
		} else {
			
			int offsetToFit = Integer.MIN_VALUE;
			for (Booking previousBooking : existingBookings) {
				Booking nextBooking = existingBookings.higher(previousBooking);
				if (bookFrom.compareTo(previousBooking.getToTime())>= 0) { 
					if (nextBooking==null) {
						return (int) Duration.between(previousBooking.getToTime(), bookFrom).toMinutes(); 
						                                          
					}else if (nextBooking.getFromTime().compareTo(bookTo) >= 0) {
						return (int) Duration.between(previousBooking.getToTime(), bookFrom).toMinutes();
						}
					}else {
						int candidateOffset = -1 * (int)Duration.between(bookFrom, previousBooking.getToTime()).toMinutes();
						LocalTime earliestPossibleBookTo = bookFrom.plusMinutes(candidateOffset+minimumBookingLength);
						LocalTime adjustedBookTo = bookTo.isBefore(earliestPossibleBookTo) ? earliestPossibleBookTo : bookTo;
						
						if (nextBooking==null
								|| nextBooking.getFromTime().compareTo(adjustedBookTo) >= 0) {
							offsetToFit = Math.max(offsetToFit, candidateOffset);
							}
						
					}
				}
			
			return offsetToFit;
			}
	}
	
	public Booking book (String bookingResource, String bookedResource, LocalTime bookFrom, LocalTime bookTo, int maxOffset, int minimumBookingLength) {

		int offsetNeededToBook = getMinimumOffsetToFit(bookFrom, bookTo, minimumBookingLength);
		if (offsetNeededToBook <= maxOffset) {
			bookFrom = bookFrom.plusMinutes(offsetNeededToBook);
			
			LocalTime earliestPossibleBookTo = bookFrom.plusMinutes(minimumBookingLength);
			bookTo = bookTo.isBefore(earliestPossibleBookTo) ? earliestPossibleBookTo : bookTo;
			
			if (commitStrictBooking(bookingResource, bookedResource,bookFrom, bookTo)) {
				return new Booking(bookingResource, bookedResource,bookFrom, bookTo); 		
			}
		}
		return null;
	}
	
	private boolean commitStrictBooking (String bookingResource, String bookedResource, LocalTime bookFrom, LocalTime bookTo) {
		if (existingBookings.isEmpty() ||
				existingBookings.first().getFromTime().compareTo(bookTo) >= 0 ){
			return existingBookings.add(new Booking(bookingResource, bookedResource,bookFrom,bookTo));
		} else {
			
			for (Booking b : existingBookings) {
				if (bookFrom.compareTo(b.getToTime()) >= 0) { 
					Booking  nextBooking = existingBookings.higher(b); 
					
					if (nextBooking==null ||
							nextBooking.getFromTime().compareTo(bookTo) >= 0) {
						return existingBookings.add(new Booking(bookingResource, bookedResource,bookFrom,bookTo)); 
						}
					}
				}
			return false;
			}
			
		}

	private int getMinimumOffsetToFit(LocalTime bookFrom, LocalTime bookTo, int minimumBookingLength) {
		if (existingBookings.isEmpty()
				|| existingBookings.first().getFromTime().compareTo(bookTo) >= 0){
			return 0;
		} else {
			//Starts with offset to not fitting, iterates to improve knowledge of better fit
			int offsetToFit = -1;
			for (Booking previousBooking : existingBookings) {
				Booking nextBooking = existingBookings.higher(previousBooking);
				if (bookFrom.compareTo(previousBooking.getToTime())>= 0) { 
					if (nextBooking==null
							|| nextBooking.getFromTime().compareTo(bookTo) >= 0) {
						return 0;
						}
					}else {
						int candidateOffset = (int)Duration.between(bookFrom, previousBooking.getToTime()).toMinutes();
						LocalTime earliestPossibleBookTo = bookFrom.plusMinutes(candidateOffset+minimumBookingLength);
						LocalTime adjustedBookTo = bookTo.isBefore(earliestPossibleBookTo) ? earliestPossibleBookTo : bookTo;
						
						if (nextBooking==null
								|| nextBooking.getFromTime().compareTo(adjustedBookTo) >= 0) {
							offsetToFit = getNewBestOffset(offsetToFit, candidateOffset);
							}
					}
				}
			return offsetToFit;
			}
	}
	
	private int getNewBestOffset(int oldOffset, int candidateOffset) {
		return (oldOffset == -1) ? candidateOffset : Math.min(oldOffset, candidateOffset);
	}
}
