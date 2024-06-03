import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConsoleLogger {

	public static void logSchedule (String airportName, Set<FlightInfo> flightInfos) {
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println("--------------------------------- "+airportName + " Arrivals ---------------------------------");
		System.out.println("-------------------------------------------------------------------------------------------");
		//|Terminal\t
		String header =  "Airline name\t|Flight number\t|Arrival time\t|Departure time\t|Runway\t\t|Gate";
		System.out.println(header);
		
		List<FlightInfo> fiSorted = new ArrayList<>(flightInfos);
		Collections.sort(fiSorted, (f1,f2)-> f1.getArrivalTime().compareTo(f2.getArrivalTime()));
				
		for (FlightInfo fi : fiSorted) {
			Booking runwayBooking = fi.getRunwayBooking();
			Booking gateBooking = fi.getGateBooking();
			System.out.println(
					getCorrectDisplay(fi.getAirlineName())+
					getCorrectDisplay(fi.getUniqueId()+"")+
					getCorrectDisplay(fi.getArrivalTime().toString(),runwayBooking.getFromTime().toString())+
					getCorrectDisplay(fi.getDepartureTime().toString(),gateBooking.getToTime().toString())+
					getCorrectDisplay(runwayBooking.getBookedResource()) +
					getCorrectDisplay(gateBooking.getBookedResource(),fi.getArrivalTime().toString(),gateBooking.getFromTime().toString())  
					);
		}
	}
	
	private static String getCorrectDisplay(String alwaysDisplay, String s1Normal, String s2Shifted) {
		if (s1Normal.equals(s2Shifted)) {
			return getCorrectDisplay(alwaysDisplay);
		}else {
			return getCorrectDisplay(alwaysDisplay+" ("+s2Shifted+")");
		}
	}
	private static String getCorrectDisplay(String s1, String s2) {
		if (s1.equals(s2)) {
			return getCorrectDisplay(s1);
		}else {
			return getCorrectDisplay(s1+" ("+s2+")");
		}
	}
	private static String getCorrectDisplay(String stringToDisplay) {
		if (stringToDisplay.length() < 8) {
			return " "+stringToDisplay + "\t\t";
		}else if (stringToDisplay.length() < 16) {
			return " "+stringToDisplay + "\t";
		}
		return stringToDisplay;
	}
}
