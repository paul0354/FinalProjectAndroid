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

public class LeaderboardActivity extends AppCompatActivity {
    private ActivityTriviaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(R.layout.leaderboard_layout);

        UserScoreDatabase userScoreDatabase = UserScoreDatabase.getDatabase(LeaderboardActivity.this);
        UserScoreDao userScoreDao = userScoreDatabase.getUserScoreDao();
        Executor executor = Executors.newSingleThreadExecutor();



        executor.execute(() -> {
            List<UserScore> userScores = userScoreDao.getAllUsers();
            userScores.sort(Comparator.comparingDouble(UserScore::calculateScore).reversed());
            RecyclerView recyclerView = findViewById(R.id.leaderboard_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new LeaderboardAdapter(userScores));
        });


    }


}
