package algonquin.cst2355.finalprojectandroid.Trivia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import algonquin.cst2355.finalprojectandroid.R;


public class ScoreFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        // Initialize your RecyclerView and set its adapter here
        Bundle bundle = getArguments();
        if (bundle != null) {
            QuestionObj selectedQuestion = (QuestionObj) bundle.getSerializable("selectedQuestion");
            if (selectedQuestion != null) {
                // Use selectedQuestion to display question, correct answer, incorrect answers
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
    }
}