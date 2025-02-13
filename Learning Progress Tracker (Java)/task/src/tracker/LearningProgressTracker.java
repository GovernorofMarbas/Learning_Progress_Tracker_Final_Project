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
                    System.out.println("No input");
                    continue;
                }
                if (input.equalsIgnoreCase("back")) {
                    System.out.printf("Total %d students have been added.%n", studentManager.getStudentCount());
                    System.out.println("Enter 'exit' to exit the program");
                    return;
                }
                if (studentManager.addStudent(input)) {
                    System.out.println("The student has been added.");
                }
            }catch (Exception e){
                System.out.println("An error occurred while reading input");
            }
        }
    }
}
