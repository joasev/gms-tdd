use std::{collections::{HashSet, BTreeSet, HashMap}, hash::{Hasher,Hash}, cmp::Ordering, rc::Rc, cell::RefCell};
use chrono::{NaiveTime, Duration};


#[derive(Debug)]
struct Airport {
    name: String,
    terminals: HashMap<String, Terminal>, //HashSet<RefCell<Terminal>>, //Solucion, cambiar HashSet a HashMap para poder modificar el Value.
    runways: HashMap<String, Runway>,     // Mas adelante volver a mejorarlo con un RefCell en el HashSet.
    flights: HashSet<Rc<Flight>>, 
    valid_airlines: HashMap<String, Airline>,

}
impl Airport {
    fn new(name: &str) -> Airport {
        Airport {
            name: name.to_string(),
            terminals: HashMap::new(),
            runways: HashMap::new(),
            flights: HashSet::new(),
            valid_airlines: HashMap::new(),
        }
    }
    fn calculate_schedule(&mut self) {
        for f in &self.flights {
            if let Some(successful_runway_booking) = self.runways.book(f) {          
                *f.runway_booking.borrow_mut() = Some(successful_runway_booking);
                if let Some (successful_gate_booking) = self.terminals.book(f) {
                    *f.gate_booking.borrow_mut() = Some(successful_gate_booking)
                }
            }
        }
    }

    fn add_airlines(&mut self, airlines: Vec<Airline>) {
        self.valid_airlines.extend(airlines.into_iter().map(|a| (a.name.clone(), a)));
        // VALID BUT THE ABOVE WAS SHORTER
        /* //airlines.push(Airline::new("OXAIR")); ESTO REQUIERE mut airlines: Vec<Airline>
        for airline in airlines {  // POR QUE NO PIDE QUE AIRLINES SEA mut SI ESTA MOVE TODOS LOS VALORES??
                                           // creo que porque into_iter() no hace nada por si solo, y airlinE es mut
            self.valid_airlines.insert(airline);
            //airline.name = "Hi".to_string(); NO DEJA PORQUE AIRLINE NO ES MUT.
                                                    // Se puede hacer for mut airline in airlines !!

                            //Creo que la solucion es que los owned not-mut si son movibles!
                            // SI! PROBADO! SE PUEDE MOVER non-mut Y MOVE OUT of non-mut. Pero no se puede modificar
        } */
    }
    fn add_flights(&mut self, flights: Vec<Rc<Flight>>) -> Vec<Rc<Flight>> {
        let mut flights_not_added = Vec::new();
        for f in flights {
            match self.valid_airlines.get_mut(&f.airline_name) {
                Some(airline) => {
                    airline.add_flight(Rc::clone(&f));
                    self.flights.insert(f);
                }
                None => flights_not_added.push(f),
            }
        }
        flights_not_added
        //self.flights.extend(flights);
    }
    fn add_terminals(&mut self, terminals: Vec<Terminal>) {
        //self.terminals.extend(terminals); 
        self.terminals.extend(terminals.into_iter().map(|t| (t.name.clone(), t)));
    }
    fn add_gate(&mut self, gate: Gate, terminal: &str) -> bool {
        let terminal = self.terminals.get_mut(terminal);
        match terminal {
            Some(mut terminal) => {
                terminal.add_gate(gate);
                true
            }
            None => false,
        }
    }
    fn add_runways(&mut self, runways: Vec<Runway>){
        //self.runways.extend(runways);
        self.runways.extend(runways.into_iter().map(|r| (r.name.clone(), r)));
    }
}

#[derive(Debug,Eq,Clone)]
struct Terminal {
    name: String,
    gates: HashMap<String, Gate>,
}
impl PartialEq for Terminal {
    fn eq(&self, other: &Self) -> bool {
        self.name == other.name
    }
}
impl Hash for Terminal {
    fn hash <H: Hasher>(&self, hasher: &mut H) {
        self.name.hash(hasher);
    }
}
impl Terminal {
    fn new(name: String) -> Terminal {
        Terminal { name, gates: HashMap::new(), }
    }
    fn add_gate(&mut self, gate: Gate) {
        self.gates.insert(gate.gate_number.clone(), gate);
    }
}
impl Bookable for Terminal {
    fn book(&mut self, f: &Flight) -> Option<Rc<Booking>>{
        self.gates.book(f)
    }
}

trait Bookable {
    fn book(&mut self, flight: &Flight) -> Option<Rc<Booking>>;
    //fn booking_priority_cmp
}
impl<T> Bookable for HashMap<String, T> 
where T: Bookable {
    fn book(&mut self, flight: &Flight) -> Option<Rc<Booking>> {
        let mut prioritized_bookables: Vec<&mut T> = self.values_mut().collect();
        //vec.sort_by(|a, b| );
        //prioritized_bookables.iter().find_map(|t| t.borrow_mut().book(flight))
        prioritized_bookables.iter_mut().find_map(|t| t.book(flight))
        //self.values().find_map(|t| t.book(flight))
    }
}

#[derive(Debug,Clone)]
struct Gate {
    gate_number: String,
    schedule: Schedule,
}
impl PartialEq for Gate {
    fn eq(&self, other: &Self) -> bool {
        self.gate_number == other.gate_number
    }
}
impl Eq for Gate {}
impl Hash for Gate {
    fn hash<H: Hasher>(&self, hasher: &mut H){
        self.gate_number.hash(hasher);
    }
}
impl Gate {
    fn new(gate_number: String) -> Gate {
        Gate { gate_number, schedule: Schedule::new(),}
    }
}
impl Bookable for Gate {
    fn book(&mut self, flight: &Flight) -> Option<Rc<Booking>> {
        let ideal_booking = Booking {
            booking_resource: flight.get_flight_code(),
            booked_resource: self.gate_number.clone(),
            from: flight.runway_booking.borrow().as_ref()?.from,
            to: flight.departure_time, 
        };
        self.schedule.schedule(ideal_booking)
    }
}

#[derive(Debug,Clone)]
struct Runway {
    name: String,
    length: RunwayLength,
    schedule: Schedule,
}
#[derive(Debug,Clone,PartialEq, Eq)]
enum RunwayLength {
    Short,
    Medium,
    Long,
}
impl PartialEq for Runway {
    fn eq(&self, other: &Self) -> bool {
        self.name == other.name
    }
}
impl Eq for Runway {}
impl Hash for Runway {
    fn hash<H: Hasher>(&self, hasher: &mut H){
        self.name.hash(hasher);
    }
}
impl Bookable for Runway {
    fn book(&mut self, flight: &Flight) -> Option<Rc<Booking>> {
        if !self.flight_fits_runway(flight) {
            return None;
        }
        //Overflow case not considered.
        let interlanding_gap_duration = Duration::minutes(Runway::INTERLANDING_GAP);
        let to_time = flight.arrival_time.overflowing_add_signed(interlanding_gap_duration).0; 
        let ideal_booking = Booking {
            booking_resource: flight.get_flight_code(),
            booked_resource: self.name.clone(),
            from: flight.arrival_time,
            to: to_time, 
        };
        self.schedule.schedule(ideal_booking)
    }

}
impl Runway {
    //const MAX_CIRCULATION_BEFORE_LANDING: u8 = 30;
    const INTERLANDING_GAP: i64 = 5;

    fn new(name: String, length: RunwayLength) -> Runway {
        Runway { 
            name, 
            length, 
            schedule: Schedule::new(),
        }
    }
    fn flight_fits_runway(&self, flight: &Flight) -> bool {
        match flight.runway_length {
            RunwayLength::Short => true,
            RunwayLength::Medium => self.length == RunwayLength::Medium || self.length == RunwayLength::Long,
            RunwayLength::Long => self.length == RunwayLength::Long,
        }
    }
}


#[derive(Debug,Clone)]
struct Airline {
    name: String,
    flights: HashSet<Rc<Flight>>,
}
impl Airline {
    fn new(name: &str) -> Airline {
        Airline { 
            name: name.to_string(),
            flights: HashSet::new(),
         }
    }
    fn add_flight(&mut self, flight: Rc<Flight>) {
        self.flights.insert(flight);
    }
}
impl PartialEq for Airline {
    fn eq(&self, other: &Self) -> bool {
        self.name == other.name
    }
}
impl Eq for Airline {}
impl Hash for Airline {
    fn hash<H: Hasher>(&self, hasher: &mut H){
        self.name.hash(hasher);
    }
}

#[derive(Debug,Clone)]
struct Flight {
    airline_name: String,
    unique_id: u32,
    arrival_time: NaiveTime,
    runway_length: RunwayLength,
    departure_time: NaiveTime,
    
    runway_booking: RefCell<Option<Rc<Booking>>>,
    gate_booking: RefCell<Option<Rc<Booking>>>,
}
impl PartialEq for Flight {
    fn eq(&self, other: &Self) -> bool {
        self.airline_name == other.airline_name && 
        self.unique_id == other.unique_id
    }
}
impl Eq for Flight {}
impl Hash for Flight {
    fn hash<H: Hasher>(&self, hasher: &mut H){
        self.airline_name.hash(hasher);
        self.unique_id.hash(hasher);
    }
}
impl Flight {
    const MINIMUM_TURNAROUND_SMALL: u8 = 30;
    const MINIMUM_TURNAROUND_MEDIUM: u8 = 60;
    const MINIMUM_TURNAROUND_LARGE: u8 = 90;

    fn new (airline_name: &str, unique_id: u32, arrival_time: NaiveTime, runway_length: RunwayLength, departure_time: NaiveTime) -> Flight {
        Flight {
            airline_name: airline_name.to_string(),
            unique_id,
            arrival_time,
            runway_length,
            departure_time,
            runway_booking: RefCell::new(None),
            gate_booking: RefCell::new(None),
        }
    }
    fn get_minimum_turnaround_time(&self) -> u8 {
        match self.runway_length {
            RunwayLength::Short => Flight::MINIMUM_TURNAROUND_SMALL,
            RunwayLength::Medium => Flight::MINIMUM_TURNAROUND_MEDIUM,
            RunwayLength::Long => Flight::MINIMUM_TURNAROUND_LARGE,
        }
    }
    fn get_flight_code(&self) -> String {
        format!("{} {}", self.airline_name, self.unique_id)
    }
}

#[derive(Debug,PartialEq,Eq,Clone)]
struct Booking {
    booking_resource: String,
    booked_resource: String,
    from: NaiveTime,
    to: NaiveTime,
}
impl Ord for Booking {
    fn cmp(&self, other: &Self) -> Ordering {
        if self.from > other.from {
            Ordering::Greater
        } else if self.from < other.from {
            Ordering::Less
        } else {
            Ordering::Equal
        }
    }
}
impl PartialOrd for Booking {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}
impl Booking {
    //fn fits_between(&self, left: Option<&Booking>, right: Option<&&Rc<Booking>>) -> bool {
    fn fits_between(&self, left: Option<&Rc<Booking>>, right: Option<&Rc<Booking>>) -> bool {
        match (left, right) {
            (Some(left), Some(right)) => left.to <= self.from && self.to <= right.from,
            (Some(left), None) => left.to <= self.from,
            (None, Some(right)) => self.to <= right.from,
            (None, None) => true,
        }
    }
}

#[derive(Debug,Clone)]
struct Schedule (BTreeSet<Rc<Booking>>);
impl Schedule {
    fn new() -> Schedule {
        Schedule(BTreeSet::new())
    }
    fn schedule(&mut self, ideal_booking: Booking) -> Option<Rc<Booking>> {
        let rc_b = Rc::new(ideal_booking);
        if self.schedule_as_is(Rc::clone(&rc_b)) {
            Some(rc_b)
        } else {
            None
        }
    }

    fn schedule_as_is(&mut self, booking_to_insert: Rc<Booking>) -> bool { 
        let mut iter = self.0.iter().peekable();
        match iter.peek() {
            None => return self.0.insert(booking_to_insert),
            Some(b) =>
                if booking_to_insert.fits_between(None, Some(b)) {
                    return self.0.insert(booking_to_insert);
                }
        };
        while let Some(b) = iter.next() {
            let next_b = iter.peek().copied();
            if booking_to_insert.fits_between(Some(b), next_b) {
                return self.0.insert(booking_to_insert);
            }
        }    
        false
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn create_airport_giving_name() {
        let ap = Airport::new("Oxford Airport");
        assert_eq!(ap.name, "Oxford Airport".to_string());
    }

    #[test]
    fn create_airlines_different_names() {
        let mut ap = Airport::new("Oxford Airport");   //El mut es por el .add que tomaa &mut self
        let mut airlines = Vec::new();                  //El mut es por el .push, NO por el add
        airlines.push(Airline::new("OXAIR"));
        airlines.push(Airline::new("BRITISH"));
        airlines.push(Airline::new("FLYCAM"));
        airlines.push(Airline::new("AIRFRANCE"));
        airlines.push(Airline::new("AERLINGUS"));

        let airlines_to_check = airlines.clone();

        ap.add_airlines(airlines);
        //ap.name = "Hi".to_string(); // ******************* DEJA! Quiza necesta estar behind mod !!
        assert!(airlines_to_check.iter().all(|item| ap.valid_airlines.contains_key(&item.name)));
        assert_eq!(ap.valid_airlines.len(), 5);
    }

    #[test]
    fn create_airlines_repeated_names() {
        let mut ap = Airport::new("Oxford Airport");   
        let mut airlines = Vec::new();                 
        airlines.push(Airline::new("OXAIR"));
        airlines.push(Airline::new("OXAIR"));
        airlines.push(Airline::new("FLYCAM"));
        airlines.push(Airline::new("AIRFRANCE"));
        airlines.push(Airline::new("AERLINGUS"));

        let airlines_to_check = airlines.clone();

        ap.add_airlines(airlines);
        
        assert!(airlines_to_check.iter().all(|item| ap.valid_airlines.contains_key(&item.name)));
        assert_eq!(ap.valid_airlines.len(), 4);
    }

    #[test]
    fn add_flight() {
        let mut ap = Airport::new("Oxford Airport");   
        let mut airlines = Vec::new();                 
        airlines.push(Airline::new("OXAIR"));
        ap.add_airlines(airlines);

        let arrival_time = NaiveTime::parse_from_str("10:00", "%H:%M").unwrap();
        let departure_time = NaiveTime::parse_from_str("11:00", "%H:%M").unwrap();
        
        let f1 = Rc::new(Flight::new("OXAIR",1, arrival_time, RunwayLength::Short,departure_time));
        let flight_to_check = Rc::clone(&f1);
        let flights = vec![f1];
        ap.add_flights(flights);

        assert!(ap.flights.contains(&flight_to_check));
        assert_eq!(ap.flights.len(), 1);
        //Check they were added to the airlines too
        let a = ap.valid_airlines.get(&flight_to_check.airline_name).unwrap();
        assert!(a.flights.contains(&flight_to_check));
    }

    #[test]
    fn add_terminals() {
        let mut ap = Airport::new("Oxford Airport"); 
        let terminals = vec![
            Terminal::new("T1".to_string()), 
            Terminal::new("T2".to_string())
            ];
        let terminals_to_check = terminals.clone();
        ap.add_terminals(terminals);

        assert!(terminals_to_check.iter().all(|item| ap.terminals.contains_key(&item.name)));
        assert_eq!(ap.terminals.len(), 2);
    }

    #[test]
    fn add_gates() {
        let mut ap = Airport::new("Oxford Airport"); 
        let terminals = vec![Terminal::new("T1".to_string()), Terminal::new("T2".to_string())];
        ap.add_terminals(terminals);

        ap.add_gate(Gate::new("1".to_string()), "T1");
        ap.add_gate(Gate::new("2".to_string()), "T1");
        ap.add_gate(Gate::new("3".to_string()), "T1");
        ap.add_gate(Gate::new("4".to_string()), "T1");

        ap.add_gate(Gate::new("10".to_string()), "T2");
        ap.add_gate(Gate::new("11".to_string()), "T2");
        ap.add_gate(Gate::new("12".to_string()), "T2");
        ap.add_gate(Gate::new("13".to_string()), "T2");
        ap.add_gate(Gate::new("14".to_string()), "T2");
        ap.add_gate(Gate::new("15".to_string()), "T2");
        ap.add_gate(Gate::new("16".to_string()), "T2");
        ap.add_gate(Gate::new("17".to_string()), "T2");
        ap.add_gate(Gate::new("18".to_string()), "T2");

        assert_eq!(ap.terminals.len(), 2);
        let total_gates: usize = ap.terminals
                                    .iter()
                                    .map(|(_, terminal)| terminal.gates.len() )
                                    .sum();
        assert_eq!(total_gates, 13);
    }

    #[test]
    fn add_runways() {
        let mut ap = Airport::new("Oxford Airport"); 
        let runways = vec![
            Runway::new("North".to_string(),RunwayLength::Long), 
            Runway::new("South".to_string(),RunwayLength::Medium)
            ];
        let runways_to_check = runways.clone();
        ap.add_runways(runways);

        assert!(runways_to_check.iter().all(|item| ap.runways.contains_key(&item.name)));
        assert_eq!(ap.runways.len(), 2);
    }

    #[test]
    fn single_booking_succeeds() {
        let mut schedule = Schedule::new();
        let booking_to_insert = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:00", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:01", "%H:%M").unwrap()
        };
        assert_ne!(schedule.schedule(booking_to_insert), None);
    }
    #[test]
    fn cannot_book_same_twice() {
        let mut schedule = Schedule::new();
        let booking_to_insert = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:00", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:01", "%H:%M").unwrap()
        };
        let same_booking = booking_to_insert.clone();
        assert_ne!(schedule.schedule(booking_to_insert), None);
        assert_eq!(schedule.schedule(same_booking), None);
    }
    #[test]
    fn book_two_adjacent() {
        let mut schedule = Schedule::new();
        let booking_1 = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:04", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:09", "%H:%M").unwrap()
        };
        let booking_2 = Booking { 
            booking_resource: "Flight2".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:09", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:14", "%H:%M").unwrap()
        };
        assert_ne!(schedule.schedule(booking_1), None);
        assert_ne!(schedule.schedule(booking_2), None);
    }
    #[test]
    fn book_third_in_the_middle() {
        let mut schedule = Schedule::new();
        let booking_1 = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:10", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:20", "%H:%M").unwrap()
        };
        let booking_2 = Booking { 
            booking_resource: "Flight2".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:50", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("01:00", "%H:%M").unwrap()
        };
        let booking_3 = Booking { 
            booking_resource: "Flight3".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:30", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:40", "%H:%M").unwrap()
        };
        assert_ne!(schedule.schedule(booking_1), None);
        assert_ne!(schedule.schedule(booking_2), None);
        assert_ne!(schedule.schedule(booking_3), None);
    }
    #[test]
    fn book_third_in_the_end() {
        let mut schedule = Schedule::new();
        let booking_1 = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:10", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:20", "%H:%M").unwrap()
        };
        let booking_2 = Booking { 
            booking_resource: "Flight2".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:50", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("01:00", "%H:%M").unwrap()
        };
        let booking_3 = Booking { 
            booking_resource: "Flight3".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("10:00", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("10:40", "%H:%M").unwrap()
        };
        assert_ne!(schedule.schedule(booking_1), None);
        assert_ne!(schedule.schedule(booking_2), None);
        assert_ne!(schedule.schedule(booking_3), None);
    }
    #[test]
    fn book_third_before_all() {
        let mut schedule = Schedule::new();
        let booking_1 = Booking { 
            booking_resource: "Flight1".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:10", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:20", "%H:%M").unwrap()
        };
        let booking_2 = Booking { 
            booking_resource: "Flight2".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:50", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("01:00", "%H:%M").unwrap()
        };
        let booking_3 = Booking { 
            booking_resource: "Flight3".to_string(), 
            booked_resource: "BookedObject1".to_string(), 
            from: NaiveTime::parse_from_str("00:00", "%H:%M").unwrap(), 
            to: NaiveTime::parse_from_str("00:05", "%H:%M").unwrap()
        };
        assert_ne!(schedule.schedule(booking_1), None);
        assert_ne!(schedule.schedule(booking_2), None);
        assert_ne!(schedule.schedule(booking_3), None);
    }

    #[test]
    fn simple_full_booking() {
        let mut ap = Airport::new("Oxford Airport");   

        //Add airlines
        let mut airlines = Vec::new();                  
        airlines.push(Airline::new("OXAIR"));
        airlines.push(Airline::new("BRITISH"));
        airlines.push(Airline::new("FLYCAM"));
        airlines.push(Airline::new("AIRFRANCE"));
        airlines.push(Airline::new("AERLINGUS"));
        ap.add_airlines(airlines);

        //Add flight
        let arrival_time = NaiveTime::parse_from_str("10:00", "%H:%M").unwrap();
        let departure_time = NaiveTime::parse_from_str("11:00", "%H:%M").unwrap();
        let f1 = Rc::new(Flight::new("OXAIR",1, arrival_time, RunwayLength::Short,departure_time));
        ap.add_flights(vec![f1]);

        //Add terminals
        let terminals = vec![
            Terminal::new("T1".to_string()), 
            Terminal::new("T2".to_string())
            ];            
        ap.add_terminals(terminals);

        //Add gates
        ap.add_gate(Gate::new("1".to_string()), "T1");
        ap.add_gate(Gate::new("2".to_string()), "T1");
        ap.add_gate(Gate::new("3".to_string()), "T1");
        ap.add_gate(Gate::new("4".to_string()), "T1");

        ap.add_gate(Gate::new("10".to_string()), "T2");
        ap.add_gate(Gate::new("11".to_string()), "T2");

        //Add runways
        let runways = vec![
            Runway::new("North".to_string(),RunwayLength::Long), 
            Runway::new("South".to_string(),RunwayLength::Medium)
            ];
        ap.add_runways(runways);

        //Calculate schedule
        ap.calculate_schedule();

        dbg!(ap);
    }
}
