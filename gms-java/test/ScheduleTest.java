import java.time.LocalTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ScheduleTest {

	@Test
	public void singleBookingSucceeds() {
		Schedule schedule = new Schedule();

		Assert.assertNotNull(schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("00:01"),0,0));
	}
	

	@Test
	public void cantBookSameTwice() {
		Schedule schedule = new Schedule();
		
		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("00:01"),0,0);
		
		Assert.assertNull(schedule.book("Flight2","BookedObject2",LocalTime.parse("00:00"), LocalTime.parse("00:01"),0,0));
	}
	
	@Test
	public void bookTwoAdjacent() {
		Schedule schedule = new Schedule();

		Assert.assertNotNull(schedule.book("Flight1","BookedObject1",LocalTime.parse("00:04"), LocalTime.parse("00:09"),0,0)); 
		Assert.assertNotNull(schedule.book("Flight2","BookedObject2",LocalTime.parse("00:09"), LocalTime.parse("00:14"),0,0));
	}
	
	@Test
	public void bookThirdInMiddle() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:10"), LocalTime.parse("00:20"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("00:50"), LocalTime.parse("01:00"),0,0);

		Assert.assertNotNull(schedule.book("Flight3","BookedObject3",LocalTime.parse("00:30"), LocalTime.parse("00:40"),0,0)); 
	}
	
	@Test
	public void bookFirstGap() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNotNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("01:00"), LocalTime.parse("02:00"),0,0)); 
	}
	
	@Test
	public void bookSecondGap() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNotNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("03:00"), LocalTime.parse("04:00"),0,0)); 
	}
	
	@Test
	public void bookAfterAllBookings() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNotNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("06:00"), LocalTime.parse("07:00"),0,0)); 
	}
	
	@Test
	public void bookBeforeAllBookings() {
		Schedule schedule = new Schedule();
 
		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNotNull(schedule.book("Flight3","BookedObject3",LocalTime.parse("00:00"), LocalTime.parse("02:00"),0,0)); 
	}
	
	@Test
	public void bookFirstAndSecondGap() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNotNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("01:00"), LocalTime.parse("02:00"),0,0)); 
		Assert.assertNotNull(schedule.book("Flight5","BookedObject5",LocalTime.parse("03:00"), LocalTime.parse("04:00"),0,0)); 
	}
	
	@Test
	public void cantBookOverlappingEnd() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("01:30"), LocalTime.parse("02:30"),0,0)); 
	}
	
	@Test
	public void cantBookOverlappingBeginning() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("00:00"), LocalTime.parse("01:00"),0,0); 
		schedule.book("Flight2","BookedObject2",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight3","BookedObject3",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertNull(schedule.book("Flight4","BookedObject4",LocalTime.parse("02:30"), LocalTime.parse("03:30"),0,0)); 
	}
	
	/*
	
	@Test
	public void minimumOffsetToFitWhenEmptySchedule() {
		Schedule schedule = new Schedule();

		Assert.assertEquals(0,schedule.getMinimumOffsetToFit(LocalTime.parse("00:30"), LocalTime.parse("00:40"),30));  
	}
	
	@Test
	public void minimumOffsetToFitWhenFitting() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertEquals(0,schedule.getMinimumOffsetToFit(LocalTime.parse("00:30"), LocalTime.parse("00:40"),30)); 
		Assert.assertEquals(0,schedule.getMinimumOffsetToFit(LocalTime.parse("03:30"), LocalTime.parse("03:40"),30)); 
		Assert.assertEquals(0,schedule.getMinimumOffsetToFit(LocalTime.parse("05:30"), LocalTime.parse("05:40"),30)); 
	}
	
	@Test
	public void minimumOffsetToFitWhenOverlappingFirstAndFittingFirstGap() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertEquals(90,schedule.getMinimumOffsetToFit(LocalTime.parse("01:30"), LocalTime.parse("02:30"),30));
		Assert.assertEquals(30,schedule.getMinimumOffsetToFit(LocalTime.parse("02:30"), LocalTime.parse("03:30"),30)); 
	}
		
	public void minimumOffsetToFitWhenOverlappingFirstAndSecond() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight1","BookedObject1",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertEquals(210,schedule.getMinimumOffsetToFit(LocalTime.parse("01:30"), LocalTime.parse("04:30"),30)); 
	}
	
	
	public void minimumOffsetToFitWhenOverlappingSecond() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertEquals(90,schedule.getMinimumOffsetToFit(LocalTime.parse("03:30"), LocalTime.parse("04:30"),30)); 
		Assert.assertEquals(30,schedule.getMinimumOffsetToFit(LocalTime.parse("04:30"), LocalTime.parse("05:30"),30));
	}
	
	
	
	@Test
	public void minimumOffsetToFitWhenOverlappingFirstAndNotFittingFirstGap() {
		Schedule schedule = new Schedule();

		schedule.book("Flight1","BookedObject1",LocalTime.parse("02:00"), LocalTime.parse("03:00"),0,0);
		schedule.book("Flight2","BookedObject2",LocalTime.parse("04:00"), LocalTime.parse("05:00"),0,0); 
		
		Assert.assertEquals(210,schedule.getMinimumOffsetToFit(LocalTime.parse("01:30"), LocalTime.parse("02:45"),61));
		Assert.assertEquals(150,schedule.getMinimumOffsetToFit(LocalTime.parse("02:30"), LocalTime.parse("03:45"),61)); 
	}*/
	
	//***** QUIZA FALTARIA TESTEAR MAS LOS MINIMUM TURNAROUND
	//**** Y TAMBIEN EL EDGE CASE SOBRE EL FINAL DEL DIA
	
	
	
}
