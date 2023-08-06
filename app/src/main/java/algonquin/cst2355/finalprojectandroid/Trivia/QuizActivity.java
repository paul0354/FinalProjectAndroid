package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;

public class QuizActivity extends AppCompatActivity {

    private QuizActivityViewModel quizActivityViewModel;
    private QuizAdapter quizAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize ViewModel and Adapter
        quizActivityViewModel = new ViewModelProvider(this).get(QuizActivityViewModel.class);
        quizAdapter = new QuizAdapter(new ArrayList<>());

        // Observe changes in questions data
        quizActivityViewModel.getQuestions().observe(this, questionObjs -> quizAdapter.updateQuestions(questionObjs));

        // Initialize RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Fetch the questions
        quizActivityViewModel.fetchTriviaQuestions(requestQueue, 10, 1, "easy");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(quizAdapter);
    }
}
