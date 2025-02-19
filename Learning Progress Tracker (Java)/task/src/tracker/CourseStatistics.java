package tracker;

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

        boolean anyEnrollment = stats.values().stream().anyMatch(cs -> cs.getEnrolled() > 0);

        if (!anyEnrollment) {
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
            System.out.println("id\tpoints\tcompleted");

            List<Student> enrolledStudents = studentManager.getStudentsEnrolledIn(course);
            if (enrolledStudents.isEmpty()) {
                System.out.println("No students enrolled.");
            } else {
                enrolledStudents.sort((s1, s2) -> {
                    int p1 = s1.getPointsForCourse(course);
                    int p2 = s2.getPointsForCourse(course);
                    if (p1 != p2) {
                        return Integer.compare(p2, p1);
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

    private double calculateCompletionPercentage(int points, Course course) {
        int targetPoints = course.getTargetPoints(); // Убедись, что Course содержит этот метод
        return targetPoints == 0 ? 0.0 : (points * 100.0) / targetPoints;
    }
}
