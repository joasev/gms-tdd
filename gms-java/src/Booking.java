import java.time.LocalTime;

final public class Booking {

	private final String bookingResource;
	private final String bookedResource; 
	private final LocalTime from;
	private final LocalTime to;
	
	
	public Booking(String bookingResource, String bookedResource, LocalTime from, LocalTime to) {
		this.bookingResource = bookingResource;
		this.bookedResource=bookedResource;
		this.from=from;
		this.to=to;
	}
	
	public LocalTime getFromTime() {
		return from;
	}
	public LocalTime getToTime() {
		return to;
	}
	
	public String getBookingResource() {
		return bookingResource;
	}
	
	public String getBookedResource() {
		return bookedResource;
	}
	
	
}
