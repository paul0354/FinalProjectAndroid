package algonquin.cst2355.finalprojectandroid.Trivia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the details of a trivia question, including its content, options, and other related data.
 */
public class QuestionDetails implements Serializable {

    // Class members to store the details of a trivia question.
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

    // Getter and Setter methods for each member variable.

    /**
     * @return the category of the trivia question.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the unique ID of the trivia question.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the correct answer for the trivia question.
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * @param correctAnswer the correct answer to set.
     */
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    /**
     * @return the list of incorrect answers for the trivia question.
     */
    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    /**
     * @param incorrectAnswers the list of incorrect answers to set.
     */
    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    /**
     * @return the actual question text.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the question text to set.
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * @return the tags associated with the trivia question.
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set.
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the type of the trivia question (e.g., multiple choice).
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the difficulty level of the trivia question.
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * @param difficulty the difficulty level to set.
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * @return the regions associated with the trivia question.
     */
    public ArrayList<Object> getRegions() {
        return regions;
    }

    /**
     * @param regions the regions to set.
     */
    public void setRegions(ArrayList<Object> regions) {
        this.regions = regions;
    }

    /**
     * @return a boolean indicating if the question is niche.
     */
    public boolean isNiche() {
        return isNiche;
    }

    /**
     * @param niche the niche status to set.
     */
    public void setNiche(boolean niche) {
        isNiche = niche;
    }

    /**
     * @return a boolean indicating if the user's answer is correct.
     */
    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    /**
     * @param correctAnswer a boolean to set if the user's answer is correct.
     */
    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }
}
