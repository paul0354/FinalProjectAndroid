package algonquin.cst2355.finalprojectandroid.Trivia;

import static algonquin.cst2355.finalprojectandroid.R.id.leaderboard_toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import algonquin.cst2355.finalprojectandroid.BearActivity;
import algonquin.cst2355.finalprojectandroid.CurrencyActivity;
import algonquin.cst2355.finalprojectandroid.FlightActivity;
import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScore;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDao;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDatabase;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;


/**
 * Represents the leaderboard activity screen that displays user scores in descending order.
 * This activity also provides an options menu to navigate to other features.
 *
 * @author Your Name
 * @version 1.0
 */
public class LeaderboardActivity extends AppCompatActivity {
    private ActivityTriviaBinding binding;


    /**
     * This method initializes the leaderboard activity. It sets up the toolbar,
     * and fetches and displays the leaderboard scores from the database.
     *
     * @param savedInstanceState Represents the state of the activity in case of restarts.
     */
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


    /**
     * Creates and displays a help dialog for the user, providing information about
     * how to use the leaderboard feature.
     */
    private void createHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LeaderboardActivity.this);
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
        } else if (id == R.id.toolbar_bear) {
            Intent intent = new Intent(LeaderboardActivity.this, BearActivity.class);
            startActivity(intent);
        } else if (id == R.id.toolbar_currency) {
            Intent intent = new Intent(LeaderboardActivity.this, CurrencyActivity.class);
            startActivity(intent);
        } else if (id == R.id.toolbar_flight) {
            Intent intent = new Intent(LeaderboardActivity.this, FlightActivity.class);
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
