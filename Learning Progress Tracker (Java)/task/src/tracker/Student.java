package tracker;

public class Student extends User {
    private final int studentId;
    private int javaPoints;
    private int dsaPoints;
    private int dbPoints;
    private int springPoints;

    public Student(int studentId, String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.studentId = studentId;
        this.javaPoints = 0;
        this.dsaPoints = 0;
        this.dbPoints = 0;
        this.springPoints = 0;
    }

    public void addPoints(int java, int dsa, int db, int spring) {
        this.javaPoints += java;
        this.dsaPoints += dsa;
        this.dbPoints += db;
        this.springPoints += spring;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getJavaPoints() {
        return javaPoints;
    }

    public int getDsaPoints() {
        return dsaPoints;
    }

    public int getDbPoints() {
        return dbPoints;
    }

    public int getSpringPoints() {
        return springPoints;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return String.format(getRole() + "{id=%d, name=%s %s, email=%s, java=%d, dsa=%d, db=%d, spring=%d}",
                studentId, getFirstName(), getLastName(), getEmail(),
                javaPoints, dsaPoints, dbPoints, springPoints);
    }
}

