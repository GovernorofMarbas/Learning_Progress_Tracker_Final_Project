package tracker;

public class Student extends User {
    public Student(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public String getRole() {
        return "Student";
    }


    @Override
    public String toString() {
        return getRole() + "{" +
                "email='" + getEmail() + '\'' +
                ", lastname='" + getLastName() + '\'' +
                ", firstname='" + getFirstName() + '\'' +
                '}';
    }
}

