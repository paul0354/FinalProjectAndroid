package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.MyViewHolder>{

    private Context context;
    private List<String> optionList;
    QuestionModel questionModelList;
    int selectedPosition = -1;
    int tempPosition;
    List<AnswerModel> answerModels;
    int questionPosition;
    List<QuestionModel> nquestionModelList;
    public OptionAdapter(Context context, List<String> optionList1,QuestionModel questionModelList,int questionPosition,List<QuestionModel> nquestionModelList1,List<AnswerModel> answerModels) {
        this.context = context;
        this.optionList = optionList1;
        this.questionModelList = questionModelList;
        this.questionPosition = questionPosition;
        this.nquestionModelList = nquestionModelList1;
        this.answerModels = answerModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_option, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.radioButton.setText(optionList.get(position));

        holder.radioButton.setChecked(position == selectedPosition);

    }


    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public List<AnswerModel> getArrayList() {
        return answerModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButton = (RadioButton) itemView.findViewById(R.id.selectOption);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();

                    answerModels.get(questionPosition).setQuestion(questionModelList.getQuestion());
                    answerModels.get(questionPosition).setCorrectAnswer(questionModelList.getCorrectAnswer());
                    answerModels.get(questionPosition).setYourAnswer(optionList.get(selectedPosition));

                    if(questionModelList.getCorrectAnswer().equals(optionList.get(selectedPosition))){
                        System.out.println("Answer is Correct");
                        answerModels.get(questionPosition).setResult(true);
                    } else {
                        System.out.println("Answer is Wrong");
                        answerModels.get(questionPosition).setResult(false);
                    }

                    notifyDataSetChanged();
                }
            });
        }
    }

}
