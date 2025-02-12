package tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StudentManager {
    private final List<Student> students = new ArrayList<>();
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9]+$");
    private final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+([-' ][A-Za-z]+)*$");


    private boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches() && name.length() > 1;
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean addStudent(String input) {
        String[] parts = input.split("\\s+");

        if (parts.length < 3) {
            System.out.println("Incorrect credentials.");
            return false;
        }

        String email = parts[parts.length - 1];
        String firstName = parts[0];
        String lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1));


        if (!isValidName(firstName)) {
            System.out.println("Incorrect first name.");
            return false;
        }

        if (!isValidName(lastName)) {
            System.out.println("Incorrect last name.");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("Incorrect email.");
            return false;
        }

//        if (!validateUserInput(firstName, lastName, email)) {
//            return false;
//        }

        students.add(new Student(firstName, lastName, email));
        return true;
    }

    public int getStudentCount() {
        return students.size();
    }

    public List<Student> getStudents() {
        return students;
    }


//    public static boolean validateUserInput(String firstName, String lastName, String email) {
//        if (!isValidName(firstName)) {
//            System.out.println("Incorrect first name.");
//            return false;
//        }
//        if (!isValidName(lastName)) {
//            System.out.println("Incorrect last name.");
//            return false;
//        }
//        if (!isValidEmail(email)) {
//            System.out.println("Incorrect email.");
//            return false;
//        }
//        return true;
//    }
}
