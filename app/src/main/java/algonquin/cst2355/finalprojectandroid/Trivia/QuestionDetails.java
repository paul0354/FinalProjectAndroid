package algonquin.cst2355.finalprojectandroid.Trivia;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionDetails implements Serializable {
    public String category;
    public String id;
    public String correctAnswer;
    public List<String> incorrectAnswers;
    public String question;
    public ArrayList<String> tags;
    public String type;
    public String difficulty;
    public ArrayList<Object> regions;
    public boolean isNiche;

    public boolean isCorrectAnswer;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList<Object> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Object> regions) {
        this.regions = regions;
    }

    public boolean isNiche() {
        return isNiche;
    }

    public void setNiche(boolean niche) {
        isNiche = niche;
    }

    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }
}
