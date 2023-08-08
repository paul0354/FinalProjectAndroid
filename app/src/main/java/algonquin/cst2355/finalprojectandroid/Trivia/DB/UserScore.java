package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents the UserScore entity in the Room database, containing details
 * about a user's performance in a trivia game.
 */
@Entity(tableName = "user_score")
public class UserScore {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "score")
    private int score;

    @ColumnInfo(name = "total_questions")
    private int totalQuestions;

    /**
     * Gets the unique identifier of the UserScore record.
     *
     * @return The ID of the record.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the UserScore record.
     *
     * @param id The ID to be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the score achieved by the user.
     *
     * @return The user's score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score achieved by the user.
     *
     * @param score The score to be set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the total number of questions attempted by the user.
     *
     * @return The total number of questions.
     */
    public int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * Sets the total number of questions attempted by the user.
     *
     * @param totalQuestions The total number of questions to be set.
     */
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    /**
     * Calculates and returns the percentage score of the user.
     *
     * @return The calculated percentage score.
     */
    public double calculateScore() {
        // Avoid division by zero
        if (totalQuestions == 0) return 0;
        return (double) score / totalQuestions * 100; // multiplying by 100 to get percentage
    }
}
