package tracker;

public class EmailTemplates {
    public static String generateCourseCompletionEmail(Student student, Course course) {
        return String.format("""
            To: %s
            Re: Your Learning Progress
            Hello, %s %s! You have accomplished our %s course!""",
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                course.getName());
    }
}
