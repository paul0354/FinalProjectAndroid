package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder>{

    private Context context;
    private List<QuestionModel> questionModelList;

    List<AnswerModel> answerModelList = new ArrayList<>();

    public QuestionListAdapter(Context context, List<QuestionModel> questionModelList) {
        this.context = context;
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_question, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        System.out.println("Question "+questionModelList.get(position).getQuestion());

        int qno = position + 1;
        String question =  String.valueOf(qno) + " " + questionModelList.get(position).getQuestion();

        holder.txtQuestion.setText(question);

        List<String> optionList = new ArrayList<>();
        optionList.add(questionModelList.get(position).getCorrectAnswer());
        optionList.addAll(questionModelList.get(position).getIncorrectAnswers());

        addItem();

        OptionAdapter optionAdapter = new OptionAdapter(context,optionList,questionModelList.get(position),holder.getAdapterPosition(),questionModelList,answerModelList);
        holder.recyclerOption.setAdapter(optionAdapter);

        answerModelList = optionAdapter.getArrayList();
    }

    public void addItem() {
        AnswerModel addMorePojo = new AnswerModel();
        addMorePojo.setQuestion("");
        addMorePojo.setCorrectAnswer("");
        addMorePojo.setYourAnswer("");
        addMorePojo.setResult(false);

        answerModelList.add(addMorePojo);
    }

    public List<AnswerModel> getArrayList() {
        return answerModelList;
    }


    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtQuestion;
        RecyclerView recyclerOption;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQuestion = (TextView) itemView.findViewById(R.id.qus_title);

            recyclerOption = (RecyclerView) itemView.findViewById(R.id.options);
            recyclerOption.setLayoutManager(new GridLayoutManager(context,2));
            recyclerOption.setNestedScrollingEnabled(false);
        }
    }
}
