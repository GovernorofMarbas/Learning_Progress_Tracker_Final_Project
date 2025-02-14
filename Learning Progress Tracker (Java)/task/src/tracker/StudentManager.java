package tracker;

import java.util.*;
import java.util.regex.Pattern;

public class StudentManager {
    private final List<Student> students = new ArrayList<>();
    private final Set<String> emails = new HashSet<>();

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9]+$");
    private final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+([-' ][A-Za-z]+)*$");

    private static int nextId = 10000;

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

        String email = parts[parts.length - 1].trim();
        String firstName = parts[0].trim();
        String lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1));
        lastName = lastName.trim();

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

        if (emails.contains(email)) {
            System.out.println("This email is already taken.");
            return false;
        }
        emails.add(email);


        students.add(new Student(nextId, firstName, lastName, email));
        nextId++;
        return true;
    }

//    public static Student addPoints() {
//
//    }

    public int getStudentCount() {
        return students.size();
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }
}
