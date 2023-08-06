package algonquin.cst2355.finalprojectandroid.Trivia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuestionViewHolder> {

    private List<QuestionObj> questions;

    public QuizAdapter(List<QuestionObj> questions) {
        this.questions = new ArrayList<>();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item_layout, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuestionObj currentQuestion = questions.get(position);
        holder.questionTextView.setText(currentQuestion.getQuestionString());
        // you may want to set other views in your item layout as well
    }

    public void updateQuestions(List<QuestionObj> newQuestions) {
        this.questions = newQuestions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView questionTextView;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            // initialize other views in your item layout if any
        }
    }
}
