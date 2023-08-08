package algonquin.cst2355.finalprojectandroid.Trivia;

import static algonquin.cst2355.finalprojectandroid.R.id.leaderboard_toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import algonquin.cst2355.finalprojectandroid.TriviaActivity;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;

public class LeaderboardActivity extends AppCompatActivity {
    private ActivityTriviaBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(R.layout.leaderboard_layout);

        Toolbar toolbar = findViewById(leaderboard_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnCreateContextMenuListener(this);

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
    private void createHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LeaderboardActivity.this);
        builder.setTitle("Help");
        builder.setMessage("Enter Number of Question in text Field and select the Category");
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_help) {
            createHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trivia_toolbar, menu);
        return true;
    }

}
