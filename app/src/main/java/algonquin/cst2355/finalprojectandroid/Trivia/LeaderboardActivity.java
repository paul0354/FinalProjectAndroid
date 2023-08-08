package algonquin.cst2355.finalprojectandroid.Trivia;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScore;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDao;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDatabase;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;


/**
 * LeaderboardActivity class responsible for displaying user scores on a leaderboard.
 * It fetches data from the database and sorts them in descending order based on scores.
 * The scores are then displayed on a RecyclerView.
 */
public class LeaderboardActivity extends AppCompatActivity {

    // Binding object for the activity layout.
    private ActivityTriviaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inflate the layout using ViewBinding.
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());

        // Set the content view of this activity.
        setContentView(R.layout.leaderboard_layout);

        // Instantiate database and dao objects.
        UserScoreDatabase userScoreDatabase = UserScoreDatabase.getDatabase(LeaderboardActivity.this);
        UserScoreDao userScoreDao = userScoreDatabase.getUserScoreDao();

        // Use a single-thread executor to execute database operations off the main thread.
        Executor executor = Executors.newSingleThreadExecutor();



        executor.execute(() -> {
            List<UserScore> userScores = userScoreDao.getAllUsers();

            // Sort the user scores in descending order based on calculated scores.
            userScores.sort(Comparator.comparingDouble(UserScore::calculateScore).reversed());

            // Initialize and set the RecyclerView to display the sorted user scores.
            RecyclerView recyclerView = findViewById(R.id.leaderboard_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new LeaderboardAdapter(userScores));
        });


    }


}
