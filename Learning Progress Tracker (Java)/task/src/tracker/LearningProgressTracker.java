package tracker;

import java.util.*;

public class LearningProgressTracker {
    private final Scanner scanner = new Scanner(System.in);
    StudentManager studentManager = new StudentManager();
    CourseStatistics courseStats = new CourseStatistics(studentManager, scanner);

    public void run() {
        System.out.println("Learning Progress Tracker");
        while (true) {
            String input = scanner.nextLine().trim();
            switch (input.toLowerCase()) {
                case "":
                    System.out.println("No input");
                    break;
                case "exit":
                    System.out.println("Bye!");
                    return;
                case "add students":
                    addStudents();
                    break;
                case "back":
                    System.out.println("Enter 'exit' to exit the program");
                    break;
                case "list":
                    if (studentManager.getStudentCount() == 0) {
                        System.out.println("No students found.");
                    } else {
                        System.out.println("Students:");
                        for (Student student : studentManager.getStudents()) {
                            System.out.println(student.getStudentId());
                        }
                    }
                    break;
                case "add points":
                    addPoints();
                    break;
                case "find":
                    find();
                    break;
                case "statistics":
                    System.out.println("Type the name of a course to see details or 'back' to quit:");
                    Map<Course, CourseStats> stats = courseStats.collectStatistics();
                    boolean anyData = stats.values().stream().anyMatch(cs -> cs.getEnrolled() > 0);
                    if (!anyData) {
                        System.out.println("Most popular: n/a");
                        System.out.println("Least popular: n/a");
                        System.out.println("Highest activity: n/a");
                        System.out.println("Lowest activity: n/a");
                        System.out.println("Easiest course: n/a");
                        System.out.println("Hardest course: n/a");
                    } else {
                        Map<String, List<String>> categorizedCourses = courseStats.categorizeCourses(stats);
                        courseStats.printStatistics(
                                categorizedCourses.get("Most Popular"),
                                categorizedCourses.get("Least Popular"),
                                categorizedCourses.get("Highest Activity"),
                                categorizedCourses.get("Lowest Activity"),
                                categorizedCourses.get("Easiest Course"),
                                categorizedCourses.get("Hardest Course")
                        );
                    }
                    while (true) {
                        String courseInput = scanner.nextLine().trim();
                        if (courseInput.equalsIgnoreCase("back")) {
                            break;
                        }
                        if (courseStats.courseExists(courseInput)) {
                            System.out.println("Showing details for course: " + courseInput);
                            courseStats.showCourseDetails(courseInput);
                        } else {
                            System.out.println("Unknown course.");
                        }
                    }
                    break;
                case "notify":
                    notifyStudents();
                    break;

//                case "statistics":
//                    // First, prompt the user
//                    System.out.println("Type the name of a course to see details or 'back' to quit:");
//
//                    // Now gather the statistics and categorize courses
//                    Map<Course, CourseStats> stats = courseStats.collectStatistics();
//
//                    // Check if there is any data in stats (i.e., any course with enrolled students)
//                    boolean anyData = stats.values().stream().anyMatch(cs -> cs.getEnrolled() > 0);
//
//                    // If no data exists, display 'n/a' for all categories
//                    if (!anyData) {
//                        System.out.println("Most popular: n/a");
//                        System.out.println("Least popular: n/a");
//                        System.out.println("Highest activity: n/a");
//                        System.out.println("Lowest activity: n/a");
//                        System.out.println("Easiest course: n/a");
//                        System.out.println("Hardest course: n/a");
//                    } else {
//                        // If data exists, categorize the courses and display the statistics
//                        Map<String, List<String>> categorizedCourses = courseStats.categorizeCourses(stats);
//                        courseStats.printStatistics(
//                                categorizedCourses.get("Most Popular"),
//                                categorizedCourses.get("Least Popular"),
//                                categorizedCourses.get("Highest Activity"),
//                                categorizedCourses.get("Lowest Activity"),
//                                categorizedCourses.get("Easiest Course"),
//                                categorizedCourses.get("Hardest Course")
//                        );
//                    }
//
//                    if ("back".equalsIgnoreCase(input)) {
//                        break;  // Exit the statistics section if the user types 'back'
//                    } else if (courseStats.courseExists(input)) {
//                        // Assuming `courseExists` is a method that checks if the course exists
//                        System.out.println("Showing details for course: " + input);
//                        // Call method to show course details here (you would need to implement this method)
//                        courseStats.showCourseDetails(input);
//                    } else {
//                        System.out.println("Unknown command!");
//                    }
//                    break;

                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    private void addStudents() {
        System.out.println("Enter student credentials or 'back' to return:");
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Incorrect credentials.");
                    continue;
                }
                if (input.equalsIgnoreCase("back")) {
                    System.out.printf("Total %d students have been added.%n", studentManager.getStudentCount());
                    return;
                }
                if (studentManager.addStudent(input)) {
                    System.out.println("The student has been added.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred while reading input: " + e.getMessage());
            }

        }
    }

    private void addPoints() {
        System.out.println("Enter an id and points or 'back' to return:");
        System.out.println("studentId pointsForJava pointsForDSA pointsForDatabases pointsForSpring");
        while (true) {
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (input.equalsIgnoreCase("back")) {
                System.out.println("Enter 'exit' to exit the program");
                return;
            }

            if (parts.length != 5) {
                System.out.println("Incorrect points format.");
                continue;
            }

            String studentIdToken = parts[0];
            int studentId;
            try {
                studentId = Integer.parseInt(studentIdToken);
            } catch (NumberFormatException e) {
                System.out.printf("No student is found for id=%s.%n", studentIdToken);
                continue;
            }

            try {
                int javaPoints = Integer.parseInt(parts[1]);
                int dsaPoints = Integer.parseInt(parts[2]);
                int dbPoints = Integer.parseInt(parts[3]);
                int springPoints = Integer.parseInt(parts[4]);
                if (javaPoints < 0 || dsaPoints < 0 || dbPoints < 0 || springPoints < 0) {
                    throw new NumberFormatException("Incorrect points format.");
                }

                Student student = studentManager.findStudentById(studentId);
                if (student == null) {
                    System.out.printf("No student is found for id=%d.%n", studentId);
                } else {
                    student.addPoints(javaPoints, dsaPoints, dbPoints, springPoints);
                    System.out.println("Points updated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect points format.");
            }
        }
    }

    private void find() {
        System.out.println("Enter an id or 'back' to return:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) {
                System.out.println("Enter 'exit' to exit the program");
                return;
            }
            try {
                int studentId = Integer.parseInt(input);
                Student student = studentManager.findStudentById(studentId);
                if (student == null) {
                    System.out.printf("No student is found for id=%d.%n", studentId);
                } else {
                    System.out.printf("%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d%n",
                            student.getStudentId(),
                            student.getJavaPoints(),
                            student.getDsaPoints(),
                            student.getDbPoints(),
                            student.getSpringPoints());
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect id format." + e.getMessage());
            }
        }
    }

    private void notifyStudents() {
        // Получаем список всех курсов из enum
        List<Course> courses = Arrays.asList(Course.values());

        // Множество для хранения уникальных ID студентов, которым отправили уведомления в данном запуске команды
        Set<Integer> notifiedStudents = new HashSet<>();

        // Проходим по всем студентам
        for (Student student : studentManager.getStudents()) {
            // Для каждого курса проверяем, выполнил ли студент его и было ли уведомление отправлено
            for (Course course : courses) {
                if (student.hasCompleted(course) && !student.hasBeenNotified(course)) {
                    System.out.println("To: " + student.getEmail());
                    System.out.println("Re: Your Learning Progress");
                    System.out.println("Hello, " + student.getFirstName() + " " + student.getLastName()
                            + "! You have accomplished our " + course.getName() + " course!");

                    // Отмечаем, что уведомление по этому курсу отправлено
                    student.markNotified(course);
                    // Регистрируем студента как уведомлённого
                    notifiedStudents.add(student.getStudentId());
                }
            }
        }

        System.out.println("Total " + notifiedStudents.size() + " students have been notified.");
    }



//    private void notify() {
//        String input = scanner.nextLine().trim();
//        if (input.equalsIgnoreCase("back")) {
//            System.out.println("Enter 'exit' to exit the program");
//            return;
//        }
//        Set<Integer> notifiedStudents = new HashSet<>();
//
//        for (Student student : students) {
//            for (Course course : courses) {
//                if (student.hasCompleted(course) && !student.hasBeenNotified(course)) {
//                    // Вывод сообщения
//                    System.out.println("To: " + student.getEmail());
//                    System.out.println("Re: Your Learning Progress");
//                    System.out.println("Hello, " + student.getFirstName() + student.getLastName() + "! You have accomplished our " + course.getName() + " course!");
//
//                    // Помечаем, что уведомление отправлено
//                    student.markNotified(course);
//
//                    // Добавляем студента в список уведомлённых (если ещё не добавлен)
//                    notifiedStudents.add(student.getStudentId());
//                }
//            }
//        }
//
//        System.out.println("Total " + notifiedStudents.size() + " students have been notified.");
//
//    }

}

