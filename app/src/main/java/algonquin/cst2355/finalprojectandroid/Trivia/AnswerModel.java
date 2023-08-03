package algonquin.cst2355.finalprojectandroid.Trivia;

public class AnswerModel {

    private String question;
    private String yourAnswer;
    private String correctAnswer;
    private boolean result;

    public AnswerModel(String question,String yourAnswer,String correctAnswer,boolean result){
        this.question = question;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.result = result;
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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
