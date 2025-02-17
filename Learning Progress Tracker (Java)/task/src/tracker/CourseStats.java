package tracker;

public class CourseStats {
    private int enrolled = 0;
    private int submissions = 0;
    private int totalPoints = 0;

    public int getEnrolled() {
        return enrolled;
    }

    public int getSubmissions() {
        return submissions;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void incrementEnrolled() {
        enrolled++;
    }

    public void addSubmissions(int count) {
        if (count > 0) {
            submissions += count;

        }
    }

    public void addPoints(int points) {
        if (points > 0) {
            totalPoints += points;
        }
    }

    double getAverage() {
        return submissions == 0 ? 0 : (double) totalPoints / submissions;
    }
}
