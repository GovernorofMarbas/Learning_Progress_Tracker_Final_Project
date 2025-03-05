//package tracker;
//
//
//import javax.swing.*;
//import javax.swing.SwingUtilities;
//
//public class Main {
//
////        System.out.println("Welcome to the Learning Progress Tracker!");
////        System.out.println("Available commands:");
////        System.out.println("- add students: Register new students.");
////        System.out.println("- list: Show all registered students.");
////        System.out.println("- add points: Assign course points to students.");
////        System.out.println("- find: Look up a student's progress.");
////        System.out.println("- statistics: View course statistics.");
////        System.out.println("- notify: Send completion notifications.");
////        System.out.println("- exit: Close the program.");
////
////        LearningProgressTracker learningProgressTracker = new LearningProgressTracker();
////        learningProgressTracker.run();
////        SwingUtilities.invokeLater(() -> {
////            LearningProgressTracker tracker = new LearningProgressTracker();
////            LearningProgressGUI gui = new LearningProgressGUI(tracker);
////            gui.setVisible(true);
////        });
//
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            // Создаем экземпляр вашей основной логики
//            LearningProgressTracker tracker = new LearningProgressTracker();
//
//            // Пример: создаем пользователя-админа для запуска GUI
//            User currentUser = new Admin("Admin", "User", "admin@example.com");
//
//            // Если хотите запустить для студента, можно, например, получить его из StudentManager:
//            // User currentUser = tracker.getStudentManager().findStudentById(1);
//
//            // Запускаем GUI с переданным пользователем
//            LearningProgressGUI gui = new LearningProgressGUI(tracker, currentUser);
//            gui.setVisible(true);
//        });
//    }
//}
//
//
//


package tracker;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Создаем экземпляр бизнес-логики
            LearningProgressTracker tracker = new LearningProgressTracker();
            User currentUser = null;

            // Спрашиваем у пользователя, кто он
            String[] options = {"Student", "Teacher"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Who are you?",
                    "User Role",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                // Регистрация студента
                String firstName = JOptionPane.showInputDialog(null, "Enter your first name:");
                String lastName = JOptionPane.showInputDialog(null, "Enter your last name:");
                String email = JOptionPane.showInputDialog(null, "Enter your email:");

                // Проверка ввода
                if (firstName == null || lastName == null || email == null ||
                        firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Registration cancelled or invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                // Регистрируем студента через StudentManager
                boolean success = tracker.getStudentManager().addStudent(firstName + " " + lastName + " " + email);
                if (!success) {
                    JOptionPane.showMessageDialog(null, "Registration failed. A student with that email might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                // Получаем зарегистрированного студента (предполагаем, что новый студент добавляется в конец списка)
                List<Student> students = tracker.getStudentManager().getAllStudents();
                Student newStudent = students.get(students.size() - 1);
                currentUser = newStudent;
            } else if (choice == 1) {
                // Вход учителя/админа
                String username = JOptionPane.showInputDialog(null, "Enter username:");
                String password = JOptionPane.showInputDialog(null, "Enter password:");
                if ("admin".equals(username) && "admin".equals(password)) {
                    currentUser = new Admin("Admin", "User", "admin@example.com");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            } else {
                // Пользователь закрыл окно выбора
                System.exit(0);
            }

            // Запускаем GUI с переданными данными
            LearningProgressGUI gui = new LearningProgressGUI(tracker, currentUser);
            gui.setVisible(true);
        });
    }
}
