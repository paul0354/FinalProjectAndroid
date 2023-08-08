package algonquin.cst2355.finalprojectandroid.Trivia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private final List<QuestionDetails> questionList;

    public QuestionAdapter(List<QuestionDetails> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trivia_question_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.optionsRadioGroup.removeAllViews();

        QuestionDetails question = questionList.get(position);
        holder.questionTextView.setText(question.getQuestion());
        List<String> options = new ArrayList<>();
        options.addAll(question.getIncorrectAnswers());
        options.add(question.getCorrectAnswer());
        Collections.shuffle(options);
        options.forEach(option -> {
            RadioButton radioButton = new RadioButton(holder.optionsRadioGroup.getContext());
            radioButton.setText(option);
            holder.optionsRadioGroup.addView(radioButton);
        });

        holder.optionsRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    String selectedOption = radioButton.getText().toString();
                    question.setCorrectAnswer(selectedOption.equals(question.getCorrectAnswer()));
                }
        );
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        RadioGroup optionsRadioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            optionsRadioGroup = itemView.findViewById(R.id.answersRadioGroup);
        }
    }
}

