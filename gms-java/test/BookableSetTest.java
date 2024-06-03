import java.time.LocalTime;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class BookableSetTest {
	
	@Test
	public void cantAddTwoSimilar() {
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
		
		Runway rs2 = new Runway("ShortRunway",Runway.SHORT);
		br.add(rs);
		br.add(rm);
		br.add(rl);
		
		Assert.assertEquals(3,br.size());
		Assert.assertFalse(br.add(rs2));
	}
	
	@Test
	public void canAddThreeDifferent() { 
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);

		Assert.assertTrue(br.add(rs));
		Assert.assertTrue(br.add(rm));
		Assert.assertTrue(br.add(rl));
		Assert.assertEquals(3,br.size());
	}
	
	@Test
	public void canAddManyWithRepeatedSizes() {
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
		Runway rs2 = new Runway("ShortRunway2",Runway.SHORT);
		Runway rm2 = new Runway("MediumRunway2",Runway.MEDIUM);
		
		br.add(rs);
		br.add(rm);
		br.add(rl);
		br.add(rs2);
		br.add(rm2);
		
		Assert.assertEquals(5,br.size());
	}
	/*
	@Test
	public void runwaysSortedAscending() { 
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);

		br.add(rs);
		br.add(rm);
		br.add(rl);
		
		Assert.assertEquals(rs.getName(),br.first().getName());
		Assert.assertEquals(rl.getName(),br.last().getName());
	}*/
	
	@Test
	public void smallPlaneBooksShort() { 
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.SHORT, "00:10");
		
		br.add(rs);
		br.add(rm);
		br.add(rl);
		
		Assert.assertEquals("ShortRunway", br.book(new FlightInfo(f)).getBookedResource());
	}
	
	@Test
	public void mediumPlaneBooksMedium() { 
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
		Runway rs2 = new Runway("ShortRunway2",Runway.SHORT);
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.MEDIUM, "00:10");
		
		br.add(rl);
		br.add(rs);
		br.add(rm);
		br.add(rs2);
		
		Assert.assertEquals("MediumRunway", br.book(new FlightInfo(f)).getBookedResource());
	}
	
	@Test
	public void bigPlaneBooksLarge() {
		BookableSet<Runway> br = new BookableSet<>();
		Runway rs = new Runway("ShortRunway",Runway.SHORT);
		Runway rm = new Runway("MediumRunway",Runway.MEDIUM);
		Runway rl = new Runway("LongRunway",Runway.LONG);
		Runway rs2 = new Runway("ShortRunway2",Runway.SHORT);
		Runway rm2 = new Runway("MediumRunway2",Runway.MEDIUM);
		Flight f = new Flight("RYANAIR", 1, "00:00", Runway.LONG, "00:10");
		
		br.add(rl);
		br.add(rs);
		br.add(rm);
		br.add(rs2);
		br.add(rm2);
		
		Assert.assertEquals("LongRunway", br.book(new FlightInfo(f)).getBookedResource());
	}
	
	
	//************ BookableTreeSet<Gate> nada que testear porque schedule testeada y 
	         //** Gates no ordenadas en el set. 
	//***************** VER COMO RANDOMIZAR PARA NO SATURAR UNA GATE
	
	//**** Oportunidad para usar el no-constructor 
	//**** BookableSet.of(...) y devuelve o un BookableTreeSet o un ShufflingSet.
	
	//*** Test all adding methods...
	
	//**** Bookabletreeset c/ ordered flights para reardenar custom c/bigger gap for security
	//*** Seria BookableSet clase, que retorna BookableSet.of(Terminal) for example
	                                       //**Entonces el Terminal es especial por el add de gate unique
	                                       //**Entonces el Gate es BookableSet normal
	                                       //**Entonces el Runway es bookable set normal tambien creo
	
	
	//************ Podria testear todos los equals y compareTo's directamente, no??
	//*************** CAMBIO DE PARECER ******************
	//*** No usar treeSet bajo el argumento de que fixed default to overlodea una pista
	//*** Y no se espera que un aeropuerto tenga tantas pistas como para que un algoritmo
	//*** de sorting importe que en cada iteracion se separen las listas en 3. Si es importante
	//*** entonces mantengo 3 + 1 sets de runways. 3 shuffle sets y 1 hashSet de unicidad de nombres.
	//*** los shuffle sets se pueden ordenar con un array de 3, no se espera que cambie.
	//*** The most runways is 8 - Chicago O'Hare International Airport
	//*** So filtering is 8 comparisons max. I lambda is more flexible than 4 sets.
	//*** Good for my assignment becauses I want to demonstrate usage of lambda.
	   //*** Anyway este no es un algorithm assignment sino OO assignment
	       //*** SI HAY QUE ORDENARLOS PARA QUE NO BOOKEE LARGE SIN PROBAR PRIMER SHORT.
	
	//*** Remember to get rid of stringTime!!!
	
	@Test
	public void canAddTwoDifferentTerminal() { 
		BookableSet<Terminal> bt = new BookableSet<>();
		Terminal t1 = new Terminal("T1");
		Terminal t2 = new Terminal("T2");
		Assert.assertTrue(bt.add(t1));
		Assert.assertTrue(bt.add(new Terminal("T2")));
	}
	
	@Test
	public void prefersBookingEmptyGate() { 
		Airport a = new Airport("Oxford Airport");
		a.add(new Terminal("T1"));
		try {
			a.add(new Gate("1"), "T1");
			a.add(new Gate("2"), "T1");
			a.add(new Gate("3"), "T1");
			a.add(new Gate("4"), "T1");
		} catch (UndefinedTerminalException e1) {
		}
		a.add(new Runway("North",Runway.LONG));
		a.add(new Runway("South",Runway.MEDIUM));
		
		a.add(new Airline ("OXAIR"));
		Flight f1 = new Flight("OXAIR", 1, "10:00", Runway.SHORT, "11:00");
		Flight f8 = new Flight("OXAIR", 2, "14:00", Runway.SHORT, "15:00");
		
		try {
			a.add(f1);
			a.add(f8);
		} catch (UndefinedAirlineException e) {
		}
		
		try{
			a.calculateSchedule();
		}catch (NotAllFlightsAssignedException e) {
			Assert.fail();
		}
		Set<FlightInfo> fiSet = a.getFlightInfoSet();
		ConsoleLogger.logSchedule(a.getName(), fiSet);
	}
	

}
