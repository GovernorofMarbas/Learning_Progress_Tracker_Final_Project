package tracker;

import java.util.Scanner;


public class LearningProgressTracker {
    private final Scanner scanner = new Scanner(System.in);
    StudentManager studentManager = new StudentManager();

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
//                    break;
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
            } catch (Exception e) {
                System.out.println("An error occurred while reading input");
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
//                    System.out.printf("%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d%n",
//                            student.getStudentId(),
//                            student.getJavaPoints(),
//                            student.getDsaPoints(),
//                            student.getDbPoints(),
//                            student.getSpringPoints());

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
}

