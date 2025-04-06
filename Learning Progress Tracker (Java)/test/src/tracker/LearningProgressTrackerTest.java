package tracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.*;

public class LearningProgressTrackerTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        // Перенаправляємо вивід для подальшої перевірки
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Тест для перевірки команди "add students":
     * 1. Вводимо команду "add students"
     * 2. Вводимо дані студента (наприклад: "John Doe john@example.com")
     * 3. Вводимо "back" для виходу з режиму додавання студентів
     * 4. Завершуємо програму командою "exit"
     * Після цього перевіряємо, що студент був доданий до менеджера.
     */
    @Test
    public void testAddStudentCommand() {
        String input = "add students\n" +
                "John Doe john@example.com\n" +
                "back\n" +
                "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LearningProgressTracker tracker = new LearningProgressTracker();
        tracker.run();

        // Перевіряємо, що у менеджера збережено 1 студента
        assertEquals(1, tracker.getStudentManager().getStudentCount());

        // Перевірка даних студента (припускаємо, що ім'я та прізвище розбираються з рядка)
        Student student = tracker.getStudentManager().getStudents().get(0);
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("john@example.com", student.getEmail());
    }

    /**
     * Тест для перевірки команди "add points":
     * 1. Додаємо студента через команду "add students"
     * 2. За допомогою "add points" оновлюємо бали студента
     * 3. Перевіряємо, що бали оновлено відповідно до введених даних.
     */
    @Test
    public void testAddPointsCommand() {
        String input = "add students\n" +
                "John Doe john@example.com\n" +
                "back\n" +
                "add points\n" +
                "1 10 20 30 40\n" +
                "back\n" +
                "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LearningProgressTracker tracker = new LearningProgressTracker();
        tracker.run();

        Student student = tracker.getStudentManager().getStudents().get(0);
        assertEquals(10, student.getJavaPoints(), "Java points мають дорівнювати 10");
        assertEquals(20, student.getDsaPoints(), "DSA points мають дорівнювати 20");
        assertEquals(30, student.getDbPoints(), "Databases points мають дорівнювати 30");
        assertEquals(40, student.getSpringPoints(), "Spring points мають дорівнювати 40");
    }

    /**
     * Тест для перевірки команди "find":
     * 1. Додаємо студента та оновлюємо його бали.
     * 2. Викликаємо команду "find" та вводимо ID студента.
     * 3. Перевіряємо, що вивід містить інформацію про студента та його бали.
     */
    @Test
    public void testFindCommand() {
        String input = "add students\n" +
                "John Doe john@example.com\n" +
                "back\n" +
                "add points\n" +
                "1 10 20 30 40\n" +
                "back\n" +
                "find\n" +
                "1\n" +
                "back\n" +
                "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LearningProgressTracker tracker = new LearningProgressTracker();
        tracker.run();

        String output = outputStream.toString();
        assertTrue(output.contains("1 points:"), "Вивід має містити ID студента та його бали");
        assertTrue(output.contains("Java=10"), "Вивід має містити інформацію про 10 балів з Java");
        assertTrue(output.contains("DSA=20"), "Вивід має містити інформацію про 20 балів з DSA");
        assertTrue(output.contains("Databases=30"), "Вивід має містити інформацію про 30 балів з Databases");
        assertTrue(output.contains("Spring=40"), "Вивід має містити інформацію про 40 балів з Spring");
    }

    /**
     * Тест для перевірки некоректного формату введення балів:
     * Вводимо рядок з некоректною кількістю параметрів.
     * Перевіряємо, що вивід містить повідомлення "Incorrect points format."
     */
    @Test
    public void testIncorrectAddPointsFormat() {
        String input = "add points\n" +
                "1 10 20 30\n" +
                "back\n" +
                "exit\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LearningProgressTracker tracker = new LearningProgressTracker();
        tracker.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Incorrect points format."), "Вивід має містити повідомлення про некоректний формат балів");
    }
}
