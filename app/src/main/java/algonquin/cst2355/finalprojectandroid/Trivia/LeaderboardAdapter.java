package algonquin.cst2355.finalprojectandroid.Trivia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.Trivia.DB.UserScore;


/**
 * LeaderboardAdapter is an adapter class for RecyclerView to display user scores on a leaderboard.
 * It binds each user's score data to a view item in the RecyclerView.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    // List containing user scores to display in the RecyclerView.
    private final List<UserScore> userScores;

    /**
     * Constructor for the LeaderboardAdapter.
     * @param userScores List of user scores to display.
     */
    public LeaderboardAdapter(List<UserScore> userScores) {
        this.userScores = userScores;
    }


    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     * @param parent The ViewGroup into which the new view will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for this adapter.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the view for an individual item in the leaderboard.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list, parent, false);
        return new LeaderboardAdapter.ViewHolder(view);
    }

    /**
     * Binds the data of a particular user score to a ViewHolder.
     * @param holder The ViewHolder to be bound.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore score = userScores.get(position);

        // Bind the data to the ViewHolder's views.
        holder.usernameTextView.setText(score.getName());
        holder.scoreTextView.setText(String.format("%s/%s", score.getScore(), score.getTotalQuestions()));
        // holder.rankTextView.setText(String.valueOf(position + 1));
    }


    /**
     * Returns the total number of items in the dataset.
     * @return The number of items in the dataset.
     */
    @Override
    public int getItemCount() {
        return userScores.size();
    }

    /**
     * ViewHolder class for the RecyclerView items.
     * It references the views within the RecyclerView items to enable data binding.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView scoreTextView;
        TextView usernameTextView;


        /**
         * Constructor for the ViewHolder.
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            usernameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
