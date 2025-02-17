package tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CourseStatistics {
    private final StudentManager studentManager;
    private final Scanner scanner;
    private final Map<String, Course> courses = new HashMap<>();


    public Map<Course, CourseStats> collectStatistics() {
        Map<Course, CourseStats> stats = new HashMap<>();

        for (Course course : Course.values()) {
            stats.put(course, new CourseStats());
        }

        List<Student> students = studentManager.getStudents();
        for (Student student : students) {
            updateCourseStats(stats, Course.JAVA, student.getJavaPoints(), student.getJavaSubmissions());
            updateCourseStats(stats, Course.DSA, student.getDsaPoints(), student.getDsaSubmissions());
            updateCourseStats(stats, Course.DATABASES, student.getDbPoints(), student.getDbSubmissions());
            updateCourseStats(stats, Course.SPRING, student.getSpringPoints(), student.getSpringSubmissions());
        }

        return stats;
    }

    private void updateCourseStats(Map<Course, CourseStats> stats, Course course, int points, int submissions) {
        if (points > 0) {
            CourseStats cs = stats.get(course);
            cs.incrementEnrolled();
            cs.addPoints(points);
            cs.addSubmissions(submissions);
        }
    }


    public Map<String, List<String>> categorizeCourses(Map<Course, CourseStats> stats) {
        Map<String, List<String>> categorizedCourses = new HashMap<>();

        List<String> mostPopular = new ArrayList<>();
        List<String> leastPopular = new ArrayList<>();
        List<String> highestActivity = new ArrayList<>();
        List<String> lowestActivity = new ArrayList<>();
        List<String> easiestCourse = new ArrayList<>();
        List<String> hardestCourse = new ArrayList<>();

        // Проверяем, есть ли вообще зачисленные студенты
        boolean anyEnrollment = stats.values().stream().anyMatch(cs -> cs.getEnrolled() > 0);

        if (!anyEnrollment) {
            // Если ни один курс не имеет зачисленных студентов, во всех категориях должно быть "n/a"
            categorizedCourses.put("Most Popular", List.of("n/a"));
            categorizedCourses.put("Least Popular", List.of("n/a"));
            categorizedCourses.put("Highest Activity", List.of("n/a"));
            categorizedCourses.put("Lowest Activity", List.of("n/a"));
            categorizedCourses.put("Easiest Course", List.of("n/a"));
            categorizedCourses.put("Hardest Course", List.of("n/a"));
            return categorizedCourses;
        }

        int maxEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).max().orElse(0);
        int minEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).min().orElse(0);
        int maxSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).max().orElse(0);
        int minSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).min().orElse(0);
        double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
        double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);

        for (Course course : Course.values()) {
            CourseStats cs = stats.get(course);

            if (cs.getEnrolled() == maxEnrolled) {
                mostPopular.add(course.getName());
            }
            if (cs.getEnrolled() == minEnrolled && minEnrolled < maxEnrolled) {
                leastPopular.add(course.getName());
            }
            if (cs.getSubmissions() == maxSubmissions) {
                highestActivity.add(course.getName());
            }
            if (cs.getSubmissions() == minSubmissions && minSubmissions < maxSubmissions) {
                lowestActivity.add(course.getName());
            }
            if (cs.getAverage() == maxAverage) {
                easiestCourse.add(course.getName());
            }
            if (cs.getAverage() == minAverage && minAverage < maxAverage) {
                hardestCourse.add(course.getName());
            }
        }

        categorizedCourses.put("Most Popular", mostPopular);
        categorizedCourses.put("Least Popular", leastPopular.isEmpty() ? List.of("n/a") : leastPopular);
        categorizedCourses.put("Highest Activity", highestActivity);
        categorizedCourses.put("Lowest Activity", lowestActivity.isEmpty() ? List.of("n/a") : lowestActivity);
        categorizedCourses.put("Easiest Course", easiestCourse);
        categorizedCourses.put("Hardest Course", hardestCourse.isEmpty() ? List.of("n/a") : hardestCourse);

        return categorizedCourses;
    }

    public void showCourseDetails(String courseName) {
        String key = courseName.toLowerCase();
        if (courses.containsKey(key)) {
            Course course = courses.get(key);
            // Removed duplicate or extra print of "Showing details for course: ..."
            System.out.println("id\tpoints\tcompleted");

            List<Student> enrolledStudents = studentManager.getStudentsEnrolledIn(course);
            if (enrolledStudents.isEmpty()) {
                System.out.println("No students enrolled.");
            } else {
                // Sort students by descending points, then ascending student id
                enrolledStudents.sort((s1, s2) -> {
                    int p1 = s1.getPointsForCourse(course);
                    int p2 = s2.getPointsForCourse(course);
                    if (p1 != p2) {
                        return Integer.compare(p2, p1); // Descending by points
                    }
                    return Integer.compare(s1.getStudentId(), s2.getStudentId());
                });

                for (Student student : enrolledStudents) {
                    int points = student.getPointsForCourse(course);
                    double completion = calculateCompletionPercentage(points, course);
                    System.out.printf("%d\t%d\t%.1f%%%n", student.getStudentId(), points, completion);
                }
            }
        } else {
            System.out.println("Course not found.");
        }
    }


//    public void showCourseDetails(String courseName) {
//        String key = courseName.toLowerCase();
//        if (courses.containsKey(key)) {
//            Course course = courses.get(key);
//            System.out.println("Showing details for course: " + course.getName());
//            System.out.println("id\tpoints\tcompleted");
//
//            List<Student> enrolledStudents = studentManager.getStudentsEnrolledIn(course);
//            if (enrolledStudents.isEmpty()) {
//                System.out.println("No students enrolled.");
//            } else {
//                // Sort students: descending by points, then ascending by id.
//                enrolledStudents.sort((s1, s2) -> {
//                    int points1 = s1.getPointsForCourse(course);
//                    int points2 = s2.getPointsForCourse(course);
//                    if (points1 != points2) {
//                        return Integer.compare(points2, points1); // Descending by points
//                    } else {
//                        return Integer.compare(s1.getStudentId(), s2.getStudentId()); // Ascending by id
//                    }
//                });
//
//                for (Student student : enrolledStudents) {
//                    int points = student.getPointsForCourse(course);
//                    double completion = calculateCompletionPercentage(points, course);
//                    System.out.printf("%d\t%d\t%.1f%%%n", student.getStudentId(), points, completion);
//                }
//            }
//        } else {
//            System.out.println("Course not found.");
//        }
//    }



//    public static final int JAVA_TARGET = 600;
//    public static final int DSA_TARGET = 400;
//    public static final int DATABASES_TARGET = 480;
//    public static final int SPRING_TARGET = 550;

//    private String formatList(List<String> list) {
//        if (list == null || list.isEmpty()) {
//            return "n/a";
//        }
//        return String.join(", ", list);
//    }


//    public Map<Course, CourseStats> collectStatistics() {
//        Map<Course, CourseStats> stats = new HashMap<>();
//        for (Course course : Course.values()) {
//            stats.put(course, new CourseStats());
//        }
//
//        List<Student> students = studentManager.getStudents();
//        for (Student student : students) {
//            updateCourseStats(stats, student, Course.JAVA, student.getJavaPoints(), student.getJavaSubmissions());
//            updateCourseStats(stats, student, Course.DSA, student.getDsaPoints(), student.getDsaSubmissions());
//            updateCourseStats(stats, student, Course.DATABASES, student.getDbPoints(), student.getDbSubmissions());
//            updateCourseStats(stats, student, Course.SPRING, student.getSpringPoints(), student.getSpringSubmissions());
//        }
//        return stats;
//    }

//    public Map<String, List<String>> categorizeCourses(Map<Course, CourseStats> stats) {
//        Map<String, List<String>> categorizedCourses = new LinkedHashMap<>();
//
//        List<String> mostPopular = new ArrayList<>();
//        List<String> leastPopular = new ArrayList<>();
//        List<String> highestActivity = new ArrayList<>();
//        List<String> lowestActivity = new ArrayList<>();
//        List<String> easiestCourse = new ArrayList<>();
//        List<String> hardestCourse = new ArrayList<>();
//
//        int maxEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).max().orElse(0);
//        int minEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).min().orElse(0);
//        int maxSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).max().orElse(0);
//        int minSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).min().orElse(0);
//        double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
//        double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);
//

    /// /        int maxEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).max().orElse(0);
    /// /        int minEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).min().orElse(0);
    /// /        int maxSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).max().orElse(0);
    /// /        int minSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).min().orElse(0);
    /// /        double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
    /// /        double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);
//
//        for (Course course : Course.values()) {
//            CourseStats cs = stats.get(course);
//
//            if (cs.getEnrolled() == maxEnrolled) {
//                mostPopular.add(course.getName());
//            }
//            if (cs.getEnrolled() == minEnrolled) {
//                leastPopular.add(course.getName());
//            }
//            if (cs.getSubmissions() == maxSubmissions) {
//                highestActivity.add(course.getName());
//            }
//            if (cs.getSubmissions() == minSubmissions) {
//                lowestActivity.add(course.getName());
//            }
//            if (cs.getAverage() == maxAverage) {
//                easiestCourse.add(course.getName());
//            }
//            if (cs.getAverage() == minAverage) {
//                hardestCourse.add(course.getName());
//            }
//        }
//
//        // ✅ Теперь вместо удаления ставим "n/a", если списки совпадают
//        if (new HashSet<>(mostPopular).equals(new HashSet<>(leastPopular))) {
//            leastPopular = List.of("n/a");
//        }
//        if (new HashSet<>(highestActivity).equals(new HashSet<>(lowestActivity))) {
//            lowestActivity = List.of("n/a");
//        }
//
//        categorizedCourses.putIfAbsent("Most popular", List.of("n/a"));
//        categorizedCourses.putIfAbsent("Least popular", List.of("n/a"));
//        categorizedCourses.putIfAbsent("Highest activity", List.of("n/a"));
//        categorizedCourses.putIfAbsent("Lowest activity", List.of("n/a"));
//        categorizedCourses.putIfAbsent("Easiest course", List.of("n/a"));
//        categorizedCourses.putIfAbsent("Hardest course", List.of("n/a"));
//
//
//        return categorizedCourses;
//    }


//    public Map<String, List<String>> categorizeCourses(Map<Course, CourseStats> stats) {
//        Map<String, List<String>> categorizedCourses = new HashMap<>();
//
//        List<String> mostPopular = new ArrayList<>();
//        List<String> leastPopular = new ArrayList<>();
//        List<String> highestActivity = new ArrayList<>();
//        List<String> lowestActivity = new ArrayList<>();
//        List<String> easiestCourse = new ArrayList<>();
//        List<String> hardestCourse = new ArrayList<>();
//
//        int maxEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).max().orElse(0);
//        int minEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).min().orElse(0);
//        int maxSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).max().orElse(0);
//        int minSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).min().orElse(0);
//        double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
//        double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);
//
//        for (Course course : Course.values()) {
//            CourseStats cs = stats.get(course);
//
//            if (cs.getEnrolled() == maxEnrolled) {
//                mostPopular.add(course.getName());
//            }
//            if (cs.getEnrolled() == minEnrolled) {
//                leastPopular.add(course.getName());
//            }
//            if (cs.getSubmissions() == maxSubmissions) {
//                highestActivity.add(course.getName());
//            }
//            if (cs.getSubmissions() == minSubmissions) {
//                lowestActivity.add(course.getName());
//            }
//            if (cs.getAverage() == maxAverage) {
//                easiestCourse.add(course.getName());
//            }
//            if (cs.getAverage() == minAverage) {
//                hardestCourse.add(course.getName());
//            }
//        }
//
//        // ✅ Исправлено: если Most Popular == Least Popular, то Least Popular не печатается
//        if (new HashSet<>(mostPopular).equals(new HashSet<>(leastPopular))) {
//            leastPopular.clear();
//        }
//
//        // ✅ Исправлено: если Highest Activity == Lowest Activity, то Lowest Activity не печатается
//        if (new HashSet<>(highestActivity).equals(new HashSet<>(lowestActivity))) {
//            lowestActivity.clear();
//        }
//
//        categorizedCourses.put("Most Popular", mostPopular);
//        categorizedCourses.put("Least Popular", leastPopular);
//        categorizedCourses.put("Highest Activity", highestActivity);
//        categorizedCourses.put("Lowest Activity", lowestActivity);
//        categorizedCourses.put("Easiest Course", easiestCourse);
//        categorizedCourses.put("Hardest Course", hardestCourse);
//
//        return categorizedCourses;
//    }
//    public Map<String, List<String>> categorizeCourses(Map<Course, CourseStats> stats) {
//        Map<String, List<String>> categorizedCourses = new HashMap<>();
//
//        List<String> mostPopular = new ArrayList<>();
//        List<String> leastPopular = new ArrayList<>();
//        List<String> highestActivity = new ArrayList<>();
//        List<String> lowestActivity = new ArrayList<>();
//        List<String> easiestCourse = new ArrayList<>();
//        List<String> hardestCourse = new ArrayList<>();
//
//        int maxEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).max().orElse(0);
//        int minEnrolled = stats.values().stream().mapToInt(CourseStats::getEnrolled).min().orElse(0);
//        int maxSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).max().orElse(0);
//        int minSubmissions = stats.values().stream().mapToInt(CourseStats::getSubmissions).min().orElse(0);
//        double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
//        double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);
//
//        for (Course course : Course.values()) {
//            CourseStats cs = stats.get(course);
//
//            if (cs.getEnrolled() == maxEnrolled) {
//                mostPopular.add(course.getName());
//            }
//            if (cs.getEnrolled() == minEnrolled) {
//                leastPopular.add(course.getName());
//            }
//            if (cs.getSubmissions() == maxSubmissions) {
//                highestActivity.add(course.getName());
//            }
//            if (cs.getSubmissions() == minSubmissions) {
//                lowestActivity.add(course.getName());
//            }
//            if (cs.getAverage() == maxAverage) {
//                easiestCourse.add(course.getName());
//            }
//            if (cs.getAverage() == minAverage) {
//                hardestCourse.add(course.getName());
//            }
//        }
//
//        categorizedCourses.put("Most Popular", mostPopular);
//        categorizedCourses.put("Least Popular", leastPopular);
//        categorizedCourses.put("Highest Activity", highestActivity);
//        categorizedCourses.put("Lowest Activity", lowestActivity);
//        categorizedCourses.put("Easiest Course", easiestCourse);
//        categorizedCourses.put("Hardest Course", hardestCourse);
//
//        return categorizedCourses;
//    }

    public void printStatistics(List<String> mostPopular, List<String> leastPopular,
                                List<String> highestActivity, List<String> lowestActivity,
                                List<String> easiestCourse, List<String> hardestCourse) {
        System.out.printf("Most popular: %s%n", formatList(mostPopular));
        System.out.printf("Least popular: %s%n", formatList(leastPopular));
        System.out.printf("Highest activity: %s%n", formatList(highestActivity));
        System.out.printf("Lowest activity: %s%n", formatList(lowestActivity));
        System.out.printf("Easiest course: %s%n", formatList(easiestCourse));
        System.out.printf("Hardest course: %s%n", formatList(hardestCourse));
    }

    private String formatList(List<String> list) {
        return list.isEmpty() ? "n/a" : String.join(", ", list);
    }

    private void updateCourseStats(Map<Course, CourseStats> stats, Student student, Course course, int points, int submissions) {
        if (points > 0) {
            CourseStats cs = stats.get(course);
            cs.incrementEnrolled();
            cs.addPoints(points);
            cs.addSubmissions(submissions);
        }
    }

    public CourseStatistics(StudentManager studentManager, Scanner scanner) {
        this.studentManager = studentManager;
        this.scanner = scanner;
        for (Course course : Course.values()) {
            courses.put(course.getName().toLowerCase(), course);
        }
    }

    public boolean courseExists(String courseName) {
        return courses.containsKey(courseName.toLowerCase());
    }

//    public void showCourseDetails(String courseName) {
//
//        List<Student> enrolledStudents = studentManager.getStudentsEnrolledIn(course);
//        if (enrolledStudents == null || enrolledStudents.isEmpty()) {
//            System.out.println("No students enrolled.");
//        } else {
//            for (Student student : enrolledStudents) {
//                int points = student.getPointsForCourse(course);
//                double completion = calculateCompletionPercentage(points, course);
//                System.out.printf("%d\t%d\t%.1f%%%n", student.getStudentId(), points, completion);
//            }
//        }
//
//        String key = courseName.toLowerCase();
//        if (courses.containsKey(key)) {
//            Course course = courses.get(key);
//
//            System.out.println("id\tpoints\tcompleted"); // Заголовок таблицы
//
//            List<Student> enrolledStudents = studentManager.getStudentsEnrolledIn(course);
//            if (enrolledStudents.isEmpty()) {
//                System.out.println("No students enrolled.");
//            } else {
//                for (Student student : enrolledStudents) {
//                    int points = student.getPointsForCourse(course);
//                    double completion = calculateCompletionPercentage(points, course);
//                    System.out.printf("%d\t%d\t%.1f%%%n", student.getStudentId(), points, completion);
//                }
//            }
//        } else {
//            System.out.println("Course not found.");
//        }
//    }


    private double calculateCompletionPercentage(int points, Course course) {
        int targetPoints = course.getTargetPoints(); // Убедись, что Course содержит этот метод
        return targetPoints == 0 ? 0.0 : (points * 100.0) / targetPoints;
    }


//    public void showCourseDetails(String courseName) {
//        String key = courseName.toLowerCase();
//        if (courses.containsKey(key)) {
//            Course course = courses.get(key);
//            System.out.println("Course Name: " + course.getName());
//            System.out.println("Description: " + course.getDescription());
//            System.out.println("Enrolled Students: " + course.getEnrolledStudents());
//        } else {
//            System.out.println("Course not found.");
//        }
//    }


    //    private Map<String, Course> courses = new HashMap<>();
//    public boolean courseExists(String courseName) {
//        return courses.containsKey(courseName);
//    }
//
//    // Assuming Course has fields like name, description, and enrolled students
//    public void showCourseDetails(String courseName) {
//        if (courses.containsKey(courseName)) {
//            Course course = courses.get(courseName);
//
//            // Print out the details of the course
//            System.out.println("Course Name: " + course.getName());
//            System.out.println("Description: " + course.getDescription());
//            System.out.println("Enrolled Students: " + course.getEnrolledStudents());
//            // Add any other relevant details here
//        } else {
//            System.out.println("Course not found.");
//        }
//    }
//
//    public Map<Course, CourseStats> collectStatistics() {
//        Map<Course, CourseStats> stats = new HashMap<>();
//
//        for (Course course : Course.values()) {
//            stats.put(course, new CourseStats());
//        }
//
//        List<Student> students = studentManager.getStudents();
//        for (Student student : students) {
//            if (student.getJavaPoints() > 0) {
//                CourseStats cs = stats.get(Course.JAVA);
//                cs.incrementEnrolled();
//                cs.addPoints(student.getJavaPoints());
//                cs.addSubmissions(student.getJavaSubmissions());
//            }
//
//            if (student.getDsaSubmissions() > 0) {
//                CourseStats cs = stats.get(Course.DSA);
//                cs.incrementEnrolled();
//                cs.addPoints(student.getDsaPoints());
//                cs.addSubmissions(student.getDsaSubmissions());
//            }
//
//            if (student.getDbSubmissions() > 0) {
//                CourseStats cs = stats.get(Course.DATABASES);
//                cs.incrementEnrolled();
//                cs.addPoints(student.getDbPoints());
//                cs.addSubmissions(student.getDbSubmissions());
//            }
//
//            if (student.getSpringPoints() > 0) {
//                CourseStats cs = stats.get(Course.SPRING);
//                cs.incrementEnrolled();
//                cs.addPoints(student.getSpringPoints());
//                cs.addSubmissions(student.getSpringSubmissions());
//            }
//
//            for (Student student : studentManager.getStudents()) {
//                updateCourseStats(stats, student, Course.JAVA, student.getJavaPoints(), student.getJavaSubmissions());
//                updateCourseStats(stats, student, Course.DSA, student.getDsaPoints(), student.getDsaSubmissions());
//                updateCourseStats(stats, student, Course.DATABASES, student.getDbPoints(), student.getDbSubmissions());
//                updateCourseStats(stats, student, Course.SPRING, student.getSpringPoints(), student.getSpringSubmissions());
//            }
//            return stats;
//        }
//
//    private void updateCourseStats(Map<Course, CourseStats> stats, Student student, Course course, int points, int submissions) {
//        if (points > 0) {
//            CourseStats cs = stats.get(course);
//            cs.incrementEnrolled();
//            cs.addPoints(points);
//            cs.addSubmissions(submissions);
//        }
//    }
//
//    public void statistics() {
//        System.out.println("Type the name of a course to see details or 'back' to quit:");
//        List<Student> allStudents = studentManager.getStudents();
//        Map<Course, CourseStats> stats = this.collectStatistics();
//
//        // Если ни по одному курсу нет записей, выводим n/a для всех категорий
//        boolean anyData = stats.values().stream().anyMatch(cs -> cs.enrolled > 0);
//        if (!anyData) {
//            System.out.println("Most popular: n/a");
//            System.out.println("Least popular: n/a");
//            System.out.println("Highest activity: n/a");
//            System.out.println("Lowest activity: n/a");
//            System.out.println("Easiest course: n/a");
//            System.out.println("Hardest course: n/a");
//        } else {
//            // Находим максимальное и минимальное количество записанных студентов (enrolled)
//            int maxEnrolled = stats.values().stream().mapToInt(cs -> cs.enrolled).max().orElse(0);
//            int minEnrolled = stats.values().stream().mapToInt(cs -> cs.enrolled).min().orElse(0);
//
//            // Находим максимальное и минимальное число отправок (submissions)
//            int maxSubmissions = stats.values().stream().mapToInt(cs -> cs.submissions).max().orElse(0);
//            int minSubmissions = stats.values().stream().mapToInt(cs -> cs.submissions).min().orElse(0);
//
//            // Для среднего балла за задание (easiest/hardest)
//            double maxAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).max().orElse(0);
//            double minAverage = stats.values().stream().mapToDouble(CourseStats::getAverage).min().orElse(0);
//
//            // Формируем списки курсов по каждой категории.
//            // Если курс уже попал в одну категорию, он не должен появляться в противоположной.
//            List<String> mostPopular = new ArrayList<>();
//            List<String> leastPopular = new ArrayList<>();
//            List<String> highestActivity = new ArrayList<>();
//            List<String> lowestActivity = new ArrayList<>();
//            List<String> easiestCourse = new ArrayList<>();
//            List<String> hardestCourse = new ArrayList<>();
//
//            // Популярность по числу записанных студентов:
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.enrolled == maxEnrolled && maxEnrolled > 0) {
//                    mostPopular.add(course.getName());
//                }
//            }
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.enrolled == minEnrolled) {
//                    // Если курс уже в mostPopular, пропускаем его для leastPopular
//                    if (!mostPopular.contains(course.getName())) {
//                        leastPopular.add(course.getName());
//                    }
//                }
//            }
//
//            // Активность (число отправок):
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.submissions == maxSubmissions && maxSubmissions > 0) {
//                    highestActivity.add(course.getName());
//                }
//            }
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.submissions == minSubmissions) {
//                    if (!highestActivity.contains(course.getName())) {
//                        lowestActivity.add(course.getName());
//                    }
//                }
//            }
//
//            // Лёгкость/сложность (средний балл за задание):
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.getAverage() == maxAverage && cs.submissions > 0) {
//                    easiestCourse.add(course.getName());
//                }
//            }
//            for (Course course : Course.values()) {
//                CourseStats cs = stats.get(course);
//                if (cs.getAverage() == minAverage && cs.submissions > 0) {
//                    if (!easiestCourse.contains(course.getName())) {
//                        hardestCourse.add(course.getName());
//                    }
//                }
//            }
//
//            // Вывод итоговой статистики:
//            System.out.print("Most popular: ");
//            System.out.println(mostPopular.isEmpty() ? "n/a" : String.join(", ", mostPopular));
//
//            System.out.print("Least popular: ");
//            System.out.println(leastPopular.isEmpty() ? "n/a" : String.join(", ", leastPopular));
//
//            System.out.print("Highest activity: ");
//            System.out.println(highestActivity.isEmpty() ? "n/a" : String.join(", ", highestActivity));
//
//            System.out.print("Lowest activity: ");
//            System.out.println(lowestActivity.isEmpty() ? "n/a" : String.join(", ", lowestActivity));
//
//            System.out.print("Easiest course: ");
//            System.out.println(easiestCourse.isEmpty() ? "n/a" : String.join(", ", easiestCourse));
//
//            System.out.print("Hardest course: ");
//            System.out.println(hardestCourse.isEmpty() ? "n/a" : String.join(", ", hardestCourse));
//        }
//
//        if (stats.values().stream().noneMatch(cs -> cs.getEnrolled() > 0)) {
//            printStatistics("n/a", "n/a", "n/a", "n/a", "n/a", "n/a");
//        } else {
//            Map<String, List<String>> categorizedCourses = categorizeCourses(stats);
//            printStatistics(
//                    categorizedCourses.get("Most Popular"),
//                    categorizedCourses.get("Least Popular"),
//                    categorizedCourses.get("Highest Activity"),
//                    categorizedCourses.get("Lowest Activity"),
//                    categorizedCourses.get("Easiest Course"),
//                    categorizedCourses.get("Hardest Course")
//            );
//        }
//
//        while (true) {
//            String input = scanner.nextLine().trim();
//            if ("back".equalsIgnoreCase(input)) {
//                break;
//            }
//
//            Course selected = Arrays.stream(Course.values())
//                    .filter(c -> c.getName().equalsIgnoreCase(input))
//                    .findFirst()
//                    .orElse(null);
//
//            if (selected == null) {
//                System.out.println("Unknown course.");
//            } else {
//                printCourseDetails(selected, allStudents);
//            }
//        }
//    }
//
//        while (true) {
//            String input = scanner.nextLine().trim();
//            if (input.equalsIgnoreCase("back")) {
//                break;
//            }
//            // Определяем, соответствует ли введённое имя одному из курсов:
//            Course selected = null;
//            for (Course course : Course.values()) {
//                if (course.getName().equalsIgnoreCase(input)) {
//                    selected = course;
//                    break;
//                }
//            }
//            if (selected == null) {
//                System.out.println("Unknown course.");
//            } else {
//                printCourseDetails(selected, allStudents);
//            }
//        }
//    }
//
//    private void printStatistics(List<String> mostPopular, List<String> leastPopular,
//                                 List<String> highestActivity, List<String> lowestActivity,
//                                 List<String> easiestCourse, List<String> hardestCourse) {
//        System.out.printf("Most popular: %s%n", formatList(mostPopular));
//        System.out.printf("Least popular: %s%n", formatList(leastPopular));
//        System.out.printf("Highest activity: %s%n", formatList(highestActivity));
//        System.out.printf("Lowest activity: %s%n", formatList(lowestActivity));
//        System.out.printf("Easiest course: %s%n", formatList(easiestCourse));
//        System.out.printf("Hardest course: %s%n", formatList(hardestCourse));
//    }
//
//    private String formatList(List<String> list) {
//        return list.isEmpty() ? "n/a" : String.join(", ", list);
//    }
//}
//
//    private void printCourseDetails(Course course, List<Student> students) {
//        // Выводим заголовок и столбцы
//        System.out.println(course.getName());
//        System.out.println("id    points    completed");
//
//        // Формируем список студентов, записанных на данный курс (имеющих >0 баллов)
//        List<Student> enrolledStudents = new ArrayList<>();
//        for (Student s : students) {
//            int points = 0;
//            switch (course) {
//                case JAVA:
//                    points = s.getJavaPoints();
//                    break;
//                case DSA:
//                    points = s.getDsaPoints();
//                    break;
//                case DATABASES:
//                    points = s.getDbPoints();
//                    break;
//                case SPRING:
//                    points = s.getSpringPoints();
//                    break;
//            }
//            if (points > 0) {
//                enrolledStudents.add(s);
//            }
//        }
//        // Сортировка: сначала по баллам (убывание), затем по id (возрастание)
//        enrolledStudents.sort((s1, s2) -> {
//            int p1 = getPointsForCourse(s1, course);
//            int p2 = getPointsForCourse(s2, course);
//            if (p2 != p1) {
//                return Integer.compare(p2, p1);
//            }
//            return Integer.compare(s1.getStudentId(), s2.getStudentId());
//        });
//
//        // Вывод деталей для каждого студента
//        for (Student s : enrolledStudents) {
//            int points = getPointsForCourse(s, course);
//            // Вычисляем процент прохождения курса (points / threshold * 100)
//            double progress = 100.0 * points / course.getTargetPoints();
//            // Форматирование с одним знаком после запятой с использованием RoundingMode.HALF_UP:
//            String formattedProgress = new BigDecimal(progress)
//                    .setScale(1, RoundingMode.HALF_UP)
//                    .toString() + "%";
//            System.out.printf("%d    %d    %s%n", s.getStudentId(), points, formattedProgress);
//        }
//    }
//
//    private int getPointsForCourse(Student s, Course course) {
//        switch (course) {
//            case JAVA:
//                return s.getJavaPoints();
//            case DSA:
//                return s.getDsaPoints();
//            case DATABASES:
//                return s.getDbPoints();
//            case SPRING:
//                return s.getSpringPoints();
//            default:
//                return 0;
//        }
//    }
    }
