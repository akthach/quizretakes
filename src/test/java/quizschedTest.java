import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class quizschedTest {

	private quizzes quizList = new quizzes();

	private retakes retakesList = new retakes();

	private courseBean course;

	@Mock
	private PrintStream printStream;

	private String output;


	@Before
	public void setup() {
		output = "";
		System.setOut(printStream);
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				System.err.println(args[0]);
				output += args[0];
				return null;
			}
		}).when(printStream).println(Mockito.anyString());
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				System.err.print(args[0]);
				output += args[0];
				return null;
			}
		}).when(printStream).print(Mockito.anyString());
	}

	public static retakeBean createRetakeBeanFromNow(int days) {
		LocalDate date = LocalDate.now().plusDays(days);
		return new retakeBean(1, "Mason", date.getMonthValue(), date.getDayOfMonth(), 0, 0);
	}

	public static String createDateString(LocalDate day) {
		return day.getDayOfWeek() + ", " + day.getMonth() + " " + day.getDayOfMonth();
	}
	
	@Test
	public void testPrintRetakeTimes() {
		course = new courseBean("1", "CS101", "120min", LocalDate.now(), LocalDate.now(), "Nguyen");		
		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("Currently scheduling quizzes for the next two weeks, until " + createDateString(LocalDate.now().plusDays(14))));
	}
	
	@Test
	public void testPrintReTakeQuizes() {
		
		output = "";
		course = new courseBean("1", "CS101", "120min", LocalDate.now(), LocalDate.now(), "Mason");
		retakeBean valid = createRetakeBeanFromNow(7);
		retakesList.addRetake(valid);
		retakeBean next30Day = createRetakeBeanFromNow(30);
		retakesList.addRetake(next30Day);
		retakeBean pass15Day = createRetakeBeanFromNow(-15);
		retakesList.addRetake(pass15Day);

		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("RETAKE: " + createDateString(valid.getDate())));
		Assert.assertFalse(output.contains("RETAKE: " + createDateString(next30Day.getDate())));
		Assert.assertFalse(output.contains("RETAKE: " + createDateString(pass15Day.getDate())));

	}
	
	@Test
	public void testPrintSkipWeek() {
		
		output = "";
		course = new courseBean("1", "CS101", "120min", LocalDate.now().plusDays(3), LocalDate.now().plusDays(10), "Mason");
		retakeBean next7day = createRetakeBeanFromNow(7);
		retakesList.addRetake(next7day);
		retakeBean next20Day = createRetakeBeanFromNow(20);
		retakesList.addRetake(next20Day);
		retakeBean next30Day = createRetakeBeanFromNow(30);
		retakesList.addRetake(next30Day);
		retakeBean pass15Day = createRetakeBeanFromNow(-15);
		retakesList.addRetake(pass15Day);

		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertFalse(output.contains("RETAKE: " + createDateString(next7day.getDate())));
		Assert.assertTrue(output.contains("RETAKE: " + createDateString(next20Day.getDate())));
		Assert.assertFalse(output.contains("RETAKE: " + createDateString(next30Day.getDate())));
		Assert.assertFalse(output.contains("RETAKE: " + createDateString(pass15Day.getDate())));
	}
	
	@Test
	public void testPrintSkipWeekMessage() {
		output = "";
		course = new courseBean("1", "CS101", "120min", LocalDate.now().plusDays(0), LocalDate.now().plusDays(7), "Mason");
		retakeBean next5day = createRetakeBeanFromNow(5);
		retakesList.addRetake(next5day);
		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("Skipping a week, no quiz or retakes."));
	}
	
	@Test
	public void testTryToRetakeQuizDuringSkipWeek() {
		output = "";
		course = new courseBean("1", "CS101", "120min", LocalDate.now().plusDays(7), LocalDate.now().plusDays(14), "Mason");
		retakeBean next8day = createRetakeBeanFromNow(8);
		retakesList.addRetake(next8day);
		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("Currently scheduling quizzes for the next two weeks, until " + createDateString(LocalDate.now().plusDays(3*7))));
		Assert.assertTrue(output.contains("Skipping a week, no quiz or retakes."));
	}
}
