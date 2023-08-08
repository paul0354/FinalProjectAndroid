package algonquin.cst2355.finalprojectandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import algonquin.cst2355.finalprojectandroid.Trivia.LeaderboardActivity;
import algonquin.cst2355.finalprojectandroid.Trivia.QuestionsActivity;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;


/**
 * Represents the Trivia activity screen that allows users to select trivia categories
 * and specify the number of questions they want. This activity also provides an options menu
 * with various features and navigation options.
 *
 * @author Dharti
 * @version 1.0
 */
public class TriviaActivity extends AppCompatActivity {
    private ActivityTriviaBinding binding;
    private EditText numberOfQuestions;

    private SharedPreferences sharedPreferences;


    /**
     * Initializes the Trivia activity. This method sets up UI components, event listeners,
     * and retrieves previous user preferences.
     *
     * @param savedInstanceState Represents the state of the activity in case of restarts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Set up toolbar
        Toolbar toolbar = binding.triviatool;
        setSupportActionBar(toolbar);

        toolbar.setOnCreateContextMenuListener(this);

        numberOfQuestions = binding.editTextNumber;
        Button foodDrink = binding.button1;
        Button filmTv = binding.button2;
        FloatingActionButton leaderboard = binding.leaderboardButton;


        // Load previous user preferences
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // Set up onClick event for the Food and Drink category button
        foodDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                String category = "food_and_drink";
                @SuppressLint("CommitPrefEdits")
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String numOfQuestions = numberOfQuestions.getText().toString();
                editor.putString("numOfQuestions", numOfQuestions);
                editor.apply();
                Intent intent = new Intent(TriviaActivity.this, QuestionsActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        filmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                String numOfQuestions = numberOfQuestions.getText().toString();
                String category = "film_and_tv";
                Intent intent = new Intent(TriviaActivity.this, QuestionsActivity.class);
                intent.putExtra("numOfQuestions", numOfQuestions);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TriviaActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Creates and displays a help dialog for the user, providing information about
     * how to use the trivia feature.
     */
    private void createHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TriviaActivity.this);
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
            Intent intent = new Intent(TriviaActivity.this, BearActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.toolbar_currency)  {
            Intent intent = new Intent(TriviaActivity.this, CurrencyActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.toolbar_flight)  {
            Intent intent = new Intent(TriviaActivity.this, FlightActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hides the soft keyboard.
     *
     * @param view The current focused view, which would receive the soft keyboard input.
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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