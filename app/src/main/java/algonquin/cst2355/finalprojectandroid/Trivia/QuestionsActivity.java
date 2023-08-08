package algonquin.cst2355.finalprojectandroid.Trivia;

import static algonquin.cst2355.finalprojectandroid.R.id.question_toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import algonquin.cst2355.finalprojectandroid.BearActivity;
import algonquin.cst2355.finalprojectandroid.CurrencyActivity;
import algonquin.cst2355.finalprojectandroid.FlightActivity;
import algonquin.cst2355.finalprojectandroid.MainActivity;
import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScore;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDao;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDatabase;
import algonquin.cst2355.finalprojectandroid.TriviaActivity;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;


/**
 * Represents the Questions activity screen that displays trivia questions based on
 * user's category selection and collects their answers. Users can also view their scores
 * and submit them to the leaderboard.
 *
 * @author Your Name
 * @version 1.0
 */
public class QuestionsActivity extends AppCompatActivity {

    private ActivityTriviaBinding binding;
    private List<QuestionDetails> questionList;
    private RecyclerView recyclerView;

    UserScoreDao userScoreDao;

    /**
     * Initializes the Questions activity. This method sets up UI components, fetches trivia
     * questions based on user preferences, and handles events for submitting scores.
     *
     * @param savedInstanceState Represents the state of the activity in case of restarts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_questions);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(question_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnCreateContextMenuListener(this);

        String category = getIntent().getStringExtra("category");



        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);


        EditText numOfQuestions = binding.editTextNumber;
        numOfQuestions.setText(sharedPreferences.getString("numOfQuestions", "2"));

        int numberOfQuestions = Integer.parseInt(numOfQuestions.getText().toString());

        fetchQuestions(category, numberOfQuestions);

        Button submitButton = findViewById(R.id.submit_result);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int score = calculateScore(questionList);
                createDialog(score);
            }
        });

    }

    /**
     * Fetches trivia questions using the provided category and number of questions. The
     * fetched questions are then displayed in a RecyclerView.
     *
     * @param category The trivia category chosen by the user.
     * @param numberOfQuestions The number of questions specified by the user.
     */
    private void fetchQuestions(String category, int numberOfQuestions) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://the-trivia-api.com/api/questions?categories=" + category + "&limit=" + numberOfQuestions;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                questionList =
                        gson.fromJson(response, new TypeToken<List<QuestionDetails>>() {
                        }.getType());

                recyclerView = findViewById(R.id.question_data);
                QuestionAdapter questionAdapter = new QuestionAdapter(questionList);
                recyclerView.setAdapter(questionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(QuestionsActivity.this));
            }
        }, error -> System.out.println("Unable to find questions"));
        queue.add(stringRequest);
    }
    /**
     * Calculates the user's score based on their answers to the trivia questions.
     *
     * @param questions List of questions along with the user's answers.
     * @return int The user's score.
     */
    private int calculateScore(List<QuestionDetails> questions) {
        AtomicInteger score = new AtomicInteger();
        questions.forEach(question -> {
            if (question.isCorrectAnswer){
                score.getAndIncrement();
            }
        });
        return score.get();
    }
    /**
     * Creates and displays a dialog showing the user's score and prompting them to
     * submit their score with a name. Upon submission, the score is saved in the database.
     *
     * @param score The user's score.
     */
    private void createDialog(int score) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(QuestionsActivity.this);

        EditText editText = new EditText(QuestionsActivity.this);
        editText.setHint("Enter Name");
        dialogBuilder.setTitle("Score");
        dialogBuilder.setMessage("Your score: " + score);
        dialogBuilder.setView(editText);
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UserScoreDatabase userScoreDatabase = UserScoreDatabase.getDatabase(QuestionsActivity.this);
                userScoreDao = userScoreDatabase.getUserScoreDao();

                Executor executor = Executors.newSingleThreadExecutor();

                executor.execute(() -> {
                    UserScore userScore = new UserScore();
                    userScore.setName(editText.getText().toString());
                    userScore.setScore(score);
                    userScore.setTotalQuestions(questionList.size());
                    userScoreDao.save(userScore);
                });

                Toast.makeText(QuestionsActivity.this, "Name : " + editText.getText().toString() + " | Your score: " + score, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        dialogBuilder.show();
    }

    /**
     * Creates and displays a help dialog for the user, providing information about
     * how to answer the trivia questions and submit scores.
     */
    private void createHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
        builder.setTitle("Help");
        builder.setMessage("Enter Number of Question in text Field and select the Category");
        builder.create().show();
    }
    /**
     * Handles toolbar menu item selections. This includes launching other activities
     * or displaying the help dialog based on the selected menu item.
     *
     * @param item The menu item that was selected.
     * @return boolean Returns true if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_help) {
            createHelpDialog();
            return true;
        }else if (id == R.id.toolbar_bear)  {
            Intent intent = new Intent(QuestionsActivity.this, BearActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.toolbar_currency)  {
            Intent intent = new Intent(QuestionsActivity.this, CurrencyActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.toolbar_flight)  {
            Intent intent = new Intent(QuestionsActivity.this, FlightActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
    /**
     * Inflates the toolbar menu.
     *
     * @param menu The options menu in which items are placed.
     * @return boolean Returns true if the menu should be displayed.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trivia_toolbar, menu);
        return true;
    }

}
