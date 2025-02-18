package tracker;

import java.util.HashSet;
import java.util.Set;

public class Student extends User {
    private final int studentId;
    private int javaPoints;
    private int dsaPoints;
    private int dbPoints;
    private int springPoints;
    private int javaSubmissions = 0;
    private int dsaSubmissions = 0;
    private int dbSubmissions = 0;
    private int springSubmissions = 0;

    private final Set<String> notifiedCourses = new HashSet<>();

    public int getJavaSubmissions() {
        return javaSubmissions;
    }

    public int getDsaSubmissions() {
        return dsaSubmissions;
    }

    public int getDbSubmissions() {
        return dbSubmissions;
    }

    public int getSpringSubmissions() {
        return springSubmissions;
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

    public int getStudentId() {
        return studentId;
    }

    public Student(int studentId, String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.studentId = studentId;
        this.javaPoints = 0;
        this.dsaPoints = 0;
        this.dbPoints = 0;
        this.springPoints = 0;
    }
    public void addPoints(int java, int dsa, int db, int spring) {
        if (java < 0 || dsa < 0 || db < 0 || spring < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.javaPoints += java;
        javaSubmissions++;
        this.dsaPoints += dsa;
        dsaSubmissions++;
        this.dbPoints += db;
        dbSubmissions++;
        this.springPoints += spring;
        springSubmissions++;
    }

    public int getPointsForCourse(Course course) {
        switch (course) {
            case JAVA: return javaPoints;
            case DSA: return dsaPoints;
            case DATABASES: return dbPoints;
            case SPRING: return springPoints;
            default: return 0;
        }
    }

    public boolean hasCompleted(Course course) {
        return getPointsForCourse(course) >= course.getTargetPoints();
    }

    public boolean hasBeenNotified(Course course) {
        return notifiedCourses.contains(course.getName());
    }

    public void markNotified(Course course) {
        notifiedCourses.add(course.getName());
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

