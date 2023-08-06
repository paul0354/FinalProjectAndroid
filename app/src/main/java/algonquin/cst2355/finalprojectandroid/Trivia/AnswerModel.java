package algonquin.cst2355.finalprojectandroid.Trivia;

public class AnswerModel {

    private String question;
    private String yourAnswer;
    private String correctAnswer;
    private boolean isCorrect;

    public AnswerModel(String question,String yourAnswer,String correctAnswer,boolean result){
        this.question = question;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    public AnswerModel(){

    }

    public String getYourAnswer() {
        return yourAnswer;
    }

    public void setYourAnswer(String yourAnswer) {
        this.yourAnswer = yourAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) { // renamed from isCorrect to setIsCorrect
        this.isCorrect = isCorrect;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
