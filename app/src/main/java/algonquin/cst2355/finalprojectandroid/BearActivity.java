package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityBearBinding;

public class BearActivity extends AppCompatActivity {

    ActivityBearBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}