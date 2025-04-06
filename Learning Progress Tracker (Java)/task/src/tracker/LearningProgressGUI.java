package tracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LearningProgressGUI extends JFrame {
    private final LearningProgressTracker tracker;
    private final DefaultTableModel tableModel;
    private final JTable studentTable;


    private final User currentUser;


    public LearningProgressGUI(LearningProgressTracker tracker, User currentUser) {
        this.tracker = tracker;
        this.currentUser = currentUser;

        setTitle("Learning Progress Tracker");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создание таблицы
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Java", "DSA", "DB", "Spring"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Student");
        JButton refreshButton = new JButton("Refresh Data");
        JButton addPointsButton = new JButton("Add Points");
        JButton findStudentButton = new JButton("Find Student");
        JButton sortButton = new JButton("Sort by Course");

        addButton.addActionListener(e -> addStudent());
        refreshButton.addActionListener(e -> refreshTable());
        addPointsButton.addActionListener(e -> addPoints());
        findStudentButton.addActionListener(e -> findStudent());
        sortButton.addActionListener(e -> sortByCourse());

        buttonPanel.add(addPointsButton);
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(findStudentButton);
        buttonPanel.add(sortButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void addStudent() {
        // Только администратор может добавлять студентов
        if (!(currentUser instanceof Admin)) {
            JOptionPane.showMessageDialog(this, "Only admin can add new students.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String firstName = JOptionPane.showInputDialog(this, "Enter first name:");
        String lastName = JOptionPane.showInputDialog(this, "Enter last name:");
        String email = JOptionPane.showInputDialog(this, "Enter email:");

        if (firstName != null && lastName != null && email != null &&
                !firstName.trim().isEmpty() && !lastName.trim().isEmpty() && !email.trim().isEmpty()) {
            boolean success = tracker.getStudentManager().addStudent(firstName + " " + lastName + " " + email);
            if (success) {
                JOptionPane.showMessageDialog(this, "Student added!");
            } else {
                JOptionPane.showMessageDialog(this, "Error! A student with that email might already exist.");
            }
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (currentUser instanceof Admin) {
            // Админ видит всех студентов
            List<Student> students = tracker.getStudentManager().getAllStudents();
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                        student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        student.getJavaPoints(),
                        student.getDsaPoints(),
                        student.getDbPoints(),
                        student.getSpringPoints()
                });
            }
        } else if (currentUser instanceof Student) {
            // Студент видит только себя
            Student student = (Student) currentUser;
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getJavaPoints(),
                    student.getDsaPoints(),
                    student.getDbPoints(),
                    student.getSpringPoints()
            });
        }
    }

    private void addPoints() {
        // Любой пользователь может добавлять баллы, но можно ограничить функциональность, если нужно
        String studentIdStr = JOptionPane.showInputDialog(this, "Enter student ID:");
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid student ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String javaPointsStr = JOptionPane.showInputDialog(this, "Enter points for Java:");
        String dsaPointsStr = JOptionPane.showInputDialog(this, "Enter points for DSA:");
        String dbPointsStr = JOptionPane.showInputDialog(this, "Enter points for Databases:");
        String springPointsStr = JOptionPane.showInputDialog(this, "Enter points for Spring:");

        if (javaPointsStr == null || dsaPointsStr == null || dbPointsStr == null || springPointsStr == null) {
            return;
        }

        try {
            int javaPoints = Integer.parseInt(javaPointsStr);
            int dsaPoints = Integer.parseInt(dsaPointsStr);
            int dbPoints = Integer.parseInt(dbPointsStr);
            int springPoints = Integer.parseInt(springPointsStr);

            if (javaPoints < 0 || dsaPoints < 0 || dbPoints < 0 || springPoints < 0) {
                throw new NumberFormatException();
            }

            Student student = tracker.getStudentManager().findStudentById(studentId);
            if (student != null) {
                student.addPoints(javaPoints, dsaPoints, dbPoints, springPoints);

                for (Course course : Course.values()) {
                    if (student.hasCompleted(course) && !student.hasBeenNotified(course)) {
                        student.markNotified(course);
                        JOptionPane.showMessageDialog(
                                this,
                                String.format("Congratulations! Student %s %s has successfully completed the %s course!",
                                        student.getFirstName(), student.getLastName(), course.name()),
                                "Course Completed",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
                JOptionPane.showMessageDialog(this, "Points updated successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Student not found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid points input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findStudent() {
        String input = JOptionPane.showInputDialog(this, "Enter student ID (or type 'back' to cancel):");
        if (input == null || input.trim().isEmpty() || input.equalsIgnoreCase("back")) {
            return;
        }
        try {
            int studentId = Integer.parseInt(input.trim());
            Student student = tracker.getStudentManager().findStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, String.format("No student is found for id=%d.", studentId),
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String message = String.format(
                        "Student ID: %d\n" +
                                "Points:\n  - Java: %d\n  - DSA: %d\n  - Databases: %d\n  - Spring: %d\n" +
                                "Submissions:\n  - Java: %d\n  - DSA: %d\n  - Databases: %d\n  - Spring: %d",
                        student.getStudentId(),
                        student.getJavaPoints(),
                        student.getDsaPoints(),
                        student.getDbPoints(),
                        student.getSpringPoints(),
                        student.getJavaSubmissions(),
                        student.getDsaSubmissions(),
                        student.getDbSubmissions(),
                        student.getSpringSubmissions()
                );
                JOptionPane.showMessageDialog(this, message, "Student Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Incorrect ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortByCourse() {
        String[] courses = {"Java", "DSA", "Databases", "Spring"};
        String selectedCourse = (String) JOptionPane.showInputDialog(
                this,
                "Select course to sort by:",
                "Sort by Course",
                JOptionPane.QUESTION_MESSAGE,
                null,
                courses,
                courses[0]
        );
        if (selectedCourse == null) {
            return;
        }
        List<Student> students = tracker.getStudentManager().getAllStudents();
        students.sort((s1, s2) -> {
            int p1 = 0, p2 = 0;
            switch (selectedCourse) {
                case "Java":
                    p1 = s1.getJavaPoints();
                    p2 = s2.getJavaPoints();
                    break;
                case "DSA":
                    p1 = s1.getDsaPoints();
                    p2 = s2.getDsaPoints();
                    break;
                case "Databases":
                    p1 = s1.getDbPoints();
                    p2 = s2.getDbPoints();
                    break;
                case "Spring":
                    p1 = s1.getSpringPoints();
                    p2 = s2.getSpringPoints();
                    break;
            }
            if (p1 != p2) {
                return Integer.compare(p2, p1); // Descending by points
            } else {
                return Integer.compare(s1.getStudentId(), s2.getStudentId()); // Ascending by ID
            }
        });
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getJavaPoints(),
                    student.getDsaPoints(),
                    student.getDbPoints(),
                    student.getSpringPoints()
            });
        }
    }
}
