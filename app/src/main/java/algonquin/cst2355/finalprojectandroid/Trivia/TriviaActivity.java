package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import algonquin.cst2355.finalprojectandroid.R;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;

public class TriviaActivity extends AppCompatActivity {
    ArrayList<QuestionObj> getQuestions;
    static QuizActivityViewModel quizModel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    protected ActivityTriviaBinding variableBinding;
    private ArrayList<QuestionObj> questions = new ArrayList<>();
    private RecyclerView.Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        variableBinding = ActivityTriviaBinding.inflate(getLayoutInflater());
        View view = variableBinding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        variableBinding.button1.setOnClickListener(clk -> {
            fetchQuestions(9); // 9 is the category for General Knowledge, replace it with your desired category number
        });

        variableBinding.button2.setOnClickListener(clk -> {
            fetchQuestions(18); // 18 is the category for Computers, replace it with your desired category number
        });
    }

    private void fetchQuestions(int category) {
        String numQuestions = variableBinding.editTextNumber.getText().toString();
        editor.putString("userInputKey", numQuestions);
        editor.apply();
        String url = "https://opentdb.com/api.php?amount=" + numQuestions + "&category=" + category + "&type=multiple";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Network Response", "Response received!");
                        try {
                            JSONArray results = response.getJSONArray("results");
                            int length = results.length();

                            for (int i = 0; i < length; i++) {
                                JSONObject question = results.getJSONObject(i);
                                String questionString = question.getString("question");
                                String correctAnswer = question.getString("correct_answer");
                                JSONArray incorrectAnswers = question.getJSONArray("incorrect_answers");
                                int incorrectLength = incorrectAnswers.length();
                                ArrayList<String> incorrectTexts = new ArrayList<>();
                                for (int j = 0; j < incorrectLength; j++) {
                                    incorrectTexts.add(incorrectAnswers.getString(j));
                                }
                                int q = 0;
                                questions.add(new QuestionObj(questionString, correctAnswer, incorrectTexts));
                            }
                            Intent intent = new Intent(TriviaActivity.this, QuizActivity.class);
                            intent.putExtra("questions", questions);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Parsing Error", "Error parsing JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Network Error", "Error response: " + error.toString());
                    }
                });

       // MySingleton.getInstance(this).addToRequestQueue(request); // I assumed you have a MySingleton class for handling Volley RequestQueue
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selection
        return super.onOptionsItemSelected(item);
    }
}
