package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityCurrencyBinding;

public class CurrencyActivity extends AppCompatActivity {

    ActivityCurrencyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}