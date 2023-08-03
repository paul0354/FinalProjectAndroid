package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.MyViewHolder>{

    private Context context;
    private List<UserResultModel> userResultModelList;

    public QuizResultAdapter(Context context, List<UserResultModel> userResultModelList1) {
        this.context = context;
        this.userResultModelList = userResultModelList1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_quiz_result, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txtuname.setText(userResultModelList.get(position).getName());
        holder.txtupoint.setText(userResultModelList.get(position).getUresult());
    }


    @Override
    public int getItemCount() {
        return userResultModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtuname,txtupoint;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtuname = (TextView) itemView.findViewById(R.id.uname);
            txtupoint = (TextView) itemView.findViewById(R.id.upoint);
        }
    }

}
