package algonquin.cst2355.finalprojectandroid.Trivia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.util.concurrent.Executors;
import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScore;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScoreDatabase;

/**
 * Adapter class for displaying user scores on a leaderboard using RecyclerView.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    // List containing user scores to be displayed
    private final List<UserScore> userScores;

    /**
     * Constructor initializes the list of user scores.
     *
     * @param userScores list of user scores.
     */
    public LeaderboardAdapter(List<UserScore> userScores) {
        this.userScores = userScores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for leaderboard list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list, parent, false);
        return new LeaderboardAdapter.ViewHolder(view, userScores);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind user score data to the view
        UserScore score = userScores.get(position);
        holder.usernameTextView.setText(score.getName());
        holder.scoreTextView.setText(String.format("%s/%s", score.getScore(), score.getTotalQuestions()));
    }

    @Override
    public int getItemCount() {
        return userScores.size();
    }

    /**
     * ViewHolder class to hold and recycle views for efficient scrolling.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView scoreTextView;
        TextView usernameTextView;
        FloatingActionButton deleteButton;

        // Database instance for performing operations
        UserScoreDatabase userScoreDatabase;

        public ViewHolder(View itemView, List<UserScore> userScores) {
            super(itemView);

            // Initialize views from the item layout
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            usernameTextView = itemView.findViewById(R.id.nameTextView);
            deleteButton = itemView.findViewById(R.id.floatingActionDeleteButton);

            // Get database instance
            userScoreDatabase = UserScoreDatabase.getDatabase(itemView.getContext());

            // Set click listener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        UserScore score = userScores.get(position);

                        // Delete the item from the database in a background thread
                        if (userScoreDatabase != null) {
                            Executors.newSingleThreadExecutor().execute(() -> userScoreDatabase.getUserScoreDao().delete(score));
                        }

                        // Remove the item from the list and update the UI
                        userScores.remove(position);
                        notifyItemRemoved(position);

                        // Show feedback message using Snackbar
                        Snackbar.make(v, "Deleted User : " + score.getName(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
