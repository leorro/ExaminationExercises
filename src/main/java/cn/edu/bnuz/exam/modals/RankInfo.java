package cn.edu.bnuz.exam.modals;

public class RankInfo {
    private String studentName;
    private String exerciseName;
    private String exerciseScore;

    public RankInfo(String studentName, String exerciseName, String exerciseScore) {
        this.studentName = studentName;
        this.exerciseName = exerciseName;
        this.exerciseScore = exerciseScore;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseScore() {
        return exerciseScore;
    }

    public void setExerciseScore(String exerciseScore) {
        this.exerciseScore = exerciseScore;
    }
}
