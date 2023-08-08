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

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<UserScore> userScores;

    public LeaderboardAdapter(List<UserScore> userScores) {
        this.userScores = userScores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_list, parent, false);
        return new LeaderboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore score = userScores.get(position);
        holder.usernameTextView.setText(score.getName());
        holder.scoreTextView.setText(String.format("%s/%s", score.getScore(), score.getTotalQuestions()));
//        holder.rankTextView.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return userScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView scoreTextView;
        TextView usernameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            usernameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
