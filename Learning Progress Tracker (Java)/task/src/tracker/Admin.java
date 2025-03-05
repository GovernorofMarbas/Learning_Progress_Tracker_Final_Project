package tracker;

import java.util.List;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public String getRole() {
        return "Admin";
    }


    public List<Student> viewAllStudents(StudentManager studentManager) {
        return studentManager.getAllStudents();
    }
}
