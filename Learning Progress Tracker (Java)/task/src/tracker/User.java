package tracker;

public abstract class User {
    protected String firstName;
    protected String lastName;
    protected String email;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public abstract String getRole();

}
