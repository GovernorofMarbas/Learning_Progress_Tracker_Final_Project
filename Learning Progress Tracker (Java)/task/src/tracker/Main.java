package tracker;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Learning Progress Tracker!");
        System.out.println("Available commands:");
        System.out.println("- add students: Register new students.");
        System.out.println("- list: Show all registered students.");
        System.out.println("- add points: Assign course points to students.");
        System.out.println("- find: Look up a student's progress.");
        System.out.println("- statistics: View course statistics.");
        System.out.println("- notify: Send completion notifications.");
        System.out.println("- exit: Close the program.");

        LearningProgressTracker learningProgressTracker = new LearningProgressTracker();
        learningProgressTracker.run();
    }
}
