package tracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CourseStatisticsTest {
    private StudentManager studentManager;
    private CourseStatistics courseStats;
    private Scanner scanner;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        // Створюємо порожню базу студентів
        studentManager = new StudentManager();
        // Scanner не використовується безпосередньо в тестах, тому можна створити dummy Scanner
        scanner = new Scanner(System.in);
        courseStats = new CourseStatistics(studentManager, scanner);

        // Перенаправляємо вивід для перевірки
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testCourseExists() {
        // Перевіряємо, що метод courseExists повертає true для існуючих курсів
        assertTrue(courseStats.courseExists("Java"));
        assertTrue(courseStats.courseExists("DSA"));
        assertTrue(courseStats.courseExists("databases"));
        assertTrue(courseStats.courseExists("spring"));
        // Для невідомого курсу має повернути false
        assertFalse(courseStats.courseExists("unknown"));
    }

    @Test
    public void testCollectStatistics() {
        // Створюємо тестових студентів
        Student s1 = new Student(10000, "Smith", "alice", "alice@example.com");
        // s1 бере участь лише в курсі Java
        s1.addPoints(50, 0, 0, 0);

        Student s2 = new Student(10001, "Johnson", "bob", "bob@example.com");
        // s2 бере участь лише в курсі DSA
        s2.addPoints(0, 40, 0, 0);

        Student s3 = new Student(10002, "Williams", "carol", "carol@example.com");
        // s3 не набирає балів – не зараховується до жодного курсу

        // Додаємо студентів до менеджера
        studentManager.getStudents().add(s1);
        studentManager.getStudents().add(s2);
        studentManager.getStudents().add(s3);

        Map<Course, CourseStats> stats = courseStats.collectStatistics();

        // Для курсу JAVA лише s1 має бали > 0
        CourseStats javaStats = stats.get(Course.JAVA);
        assertEquals(1, javaStats.getEnrolled(), "Java: має бути зараховано 1 студента");

        // Для курсу DSA лише s2 має бали > 0
        CourseStats dsaStats = stats.get(Course.DSA);
        assertEquals(1, dsaStats.getEnrolled(), "DSA: має бути зараховано 1 студента");

        // Для DATABASES та SPRING жоден студент не набрав балів
        CourseStats dbStats = stats.get(Course.DATABASES);
        CourseStats springStats = stats.get(Course.SPRING);
        assertEquals(0, dbStats.getEnrolled(), "Databases: 0 студентів");
        assertEquals(0, springStats.getEnrolled(), "Spring: 0 студентів");
    }

    @Test
    public void testCategorizeCourses_NoEnrollment() {
        // Якщо жоден студент не зарахований (немає балів), категоризація має повернути "n/a"
        Map<Course, CourseStats> stats = courseStats.collectStatistics();
        Map<String, List<String>> categorized = courseStats.categorizeCourses(stats);

        assertEquals(List.of("n/a"), categorized.get("Most Popular"));
        assertEquals(List.of("n/a"), categorized.get("Least Popular"));
        assertEquals(List.of("n/a"), categorized.get("Highest Activity"));
        assertEquals(List.of("n/a"), categorized.get("Lowest Activity"));
        assertEquals(List.of("n/a"), categorized.get("Easiest Course"));
        assertEquals(List.of("n/a"), categorized.get("Hardest Course"));
    }

    @Test
    public void testPrintStatistics() {
        // Створюємо dummy-дані для категоризації
        List<String> mostPopular = List.of("Java", "DSA");
        List<String> leastPopular = List.of("Databases");
        List<String> highestActivity = List.of("Java");
        List<String> lowestActivity = List.of("Spring");
        List<String> easiestCourse = List.of("Java");
        List<String> hardestCourse = List.of("DSA");

        courseStats.printStatistics(mostPopular, leastPopular, highestActivity, lowestActivity, easiestCourse, hardestCourse);
        String output = outputStream.toString();
        assertTrue(output.contains("Most popular: Java, DSA"));
        assertTrue(output.contains("Least popular: Databases"));
        assertTrue(output.contains("Highest activity: Java"));
        assertTrue(output.contains("Lowest activity: Spring"));
        assertTrue(output.contains("Easiest course: Java"));
        assertTrue(output.contains("Hardest course: DSA"));
    }

    @Test
    public void testShowCourseDetails_NoEnrollment() {
        // Якщо жоден студент не зарахований на курс, має бути повідомлення "No students enrolled."
        courseStats.showCourseDetails("Java");
        String output = outputStream.toString();
        assertTrue(output.contains("No students enrolled."), "Вивід має містити повідомлення про відсутність студентів");
    }

    @Test
    public void testShowCourseDetails_WithEnrollment() {
        // Створюємо студента, який бере участь у курсі Java
        Student s1 = new Student(10003, "Brown", "dave", "dave@example.com");
        s1.addPoints(60, 0, 0, 0); // Бали лише для Java
        studentManager.getStudents().add(s1);

        // Очищаємо вивід, щоб перевірити новий виклик
        outputStream.reset();
        courseStats.showCourseDetails("Java");
        String output = outputStream.toString();

        // Перевіряємо, що вивід містить заголовок таблиці та інформацію про студента
        assertTrue(output.contains("id\tpoints\tcompleted"), "Повинен бути виведений заголовок таблиці");
        assertTrue(output.contains("1\t60\t"), "Повинен бути виведений ID студента та набрані бали");
    }
}
