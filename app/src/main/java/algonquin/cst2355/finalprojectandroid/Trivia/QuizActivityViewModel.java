package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class QuizActivityViewModel extends ViewModel {

    private MutableLiveData<List<QuestionObj>> questionsLiveData = new MutableLiveData<>();

    public LiveData<List<QuestionObj>> getQuestions() {
        return questionsLiveData;
    }

    public void fetchTriviaQuestions(RequestQueue requestQueue, int amount, int category, String difficulty) {
        String url = "https://opentdb.com/api.php?amount=" + amount + "&category=" + category + "&difficulty=" + difficulty;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<QuestionObj> questionList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String question = jsonObject.getString("question");
                            String correctAnswer = jsonObject.getString("correct_answer");
                            JSONArray incorrectAnswersArray = jsonObject.getJSONArray("incorrect_answers");
                            List<String> incorrectAnswers = new ArrayList<>();
                            for (int j = 0; j < incorrectAnswersArray.length(); j++) {
                                incorrectAnswers.add(incorrectAnswersArray.getString(j));
                            }
                            questionList.add(new QuestionObj(question, correctAnswer, incorrectAnswers));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    questionsLiveData.setValue(questionList);
                }, error -> {
                    // Handle error
                });

        requestQueue.add(jsonObjectRequest);
    }
}
