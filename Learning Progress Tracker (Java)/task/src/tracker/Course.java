package tracker;

public enum Course {
    JAVA("Java", 600),
    DSA("DSA", 400),
    DATABASES("Databases", 480),
    SPRING("Spring", 550);
    private final String name;
    private final int targetPoints;
    private String description;
    private int enrolledStudents;

    Course(String name, int targetPoints, String description, int enrolledStudents) {
        this.name = name;
        this.targetPoints = targetPoints;
        this.description = description;
        this.enrolledStudents = enrolledStudents;
    }

    Course(String name, int targetPoints) {
        this.name = name;
        this.targetPoints = targetPoints;
    }

    public String getDescription() {
        return description;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public String getName() {
        return name;
    }


    public int getTargetPoints() {
        return targetPoints;
    }


}

