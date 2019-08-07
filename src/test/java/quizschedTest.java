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

	private quizzes quizList;

	private retakes retakesList;

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

	@Test
	public void testPrintCourseTitle() throws Exception {
		String title = "CS101";
		course = new courseBean("1", title, "120min", LocalDate.now(), LocalDate.now(), "Mason");
		retakesList = new retakes();

		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("GMU quiz retake scheduler for class " + title));
	}
	
	@Test
	public void testPrintNullCourseTitle() throws Exception {
		String title = null;
		course = new courseBean("1", title, "120min", LocalDate.now(), LocalDate.now(), "Mason");
		retakesList = new retakes();

		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("GMU quiz retake scheduler for class "));
	}
	
	@Test
	public void testPrintInfo() throws Exception {
		course = new courseBean("1", "CS101", "120min", LocalDate.now(), LocalDate.now(), "Mason");
		retakesList = new retakes();
		quizsched.printQuizScheduleForm(quizList, retakesList, course);
		Assert.assertTrue(output.contains("You can sign up for quiz retakes within the next two weeks."));
		Assert.assertTrue(output.contains("Enter your name (as it appears on the class roster),"));
		Assert.assertTrue(output.contains("then select which date, time, and quiz you wish to retake from the following list."));
		Assert.assertTrue(output.contains(LocalDate.now().getDayOfWeek().toString()));
		Assert.assertTrue(output.contains(LocalDate.now().getMonth().toString()));
		Assert.assertTrue(output.contains(LocalDate.now().getDayOfMonth() + ""));
	}

}
