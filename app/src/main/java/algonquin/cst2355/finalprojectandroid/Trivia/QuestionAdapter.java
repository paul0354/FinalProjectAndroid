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


/**
 * QuestionAdapter is an adapter class for RecyclerView to display trivia questions.
 * Each question is presented with multiple choice answers.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private final List<QuestionDetails> questionList;

    /**
     * Constructor for the QuestionAdapter.
     *
     * @param questionList List of trivia questions to display.
     */
    public QuestionAdapter(List<QuestionDetails> questionList) {
        this.questionList = questionList;
    }


    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent The ViewGroup into which the new view will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for this adapter.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trivia_question_layout, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Binds the data of a particular trivia question to a ViewHolder.
     *
     * @param holder The ViewHolder to be bound.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Clear all radio buttons from the RadioGroup to ensure it's fresh for the next set of options.
        holder.optionsRadioGroup.removeAllViews();

        // Get the trivia question at the specified position.
        QuestionDetails question = questionList.get(position);

        // Set the text for the question.
        holder.questionTextView.setText(question.getQuestion());

        // Combine correct and incorrect answers and shuffle them to display in random order.
        List<String> options = new ArrayList<>();
        options.addAll(question.getIncorrectAnswers());
        options.add(question.getCorrectAnswer());
        Collections.shuffle(options);

        // add each shuffled option as a RadioButton in the RadioGroup.
        options.forEach(option -> {
            RadioButton radioButton = new RadioButton(holder.optionsRadioGroup.getContext());
            radioButton.setText(option);
            holder.optionsRadioGroup.addView(radioButton);
        });

        // Listener to determine if the selected answer is correct.
        holder.optionsRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    String selectedOption = radioButton.getText().toString();
                    question.setCorrectAnswer(selectedOption.equals(question.getCorrectAnswer()));
                }
        );
    }


    /**
     * Returns the total number of items in the dataset.
     *
     * @return The number of items in the dataset.
     */
    @Override
    public int getItemCount() {
        return questionList.size();
    }


    /**
     * ViewHolder class for the RecyclerView items.
     * It references the views within the RecyclerView items to enable data binding.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        RadioGroup optionsRadioGroup;


        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            optionsRadioGroup = itemView.findViewById(R.id.answersRadioGroup);
        }
    }
}

