package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityFlightBinding;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityTriviaBinding;

public class TriviaActivity extends AppCompatActivity {
    ActivityTriviaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}