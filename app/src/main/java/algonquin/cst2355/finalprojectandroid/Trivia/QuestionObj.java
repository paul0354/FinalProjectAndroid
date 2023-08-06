package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class QuestionObj implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String questionString;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public QuestionObj(String questionString, String correctAnswer, List<String> incorrectAnswers) {
        this.questionString = questionString;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}