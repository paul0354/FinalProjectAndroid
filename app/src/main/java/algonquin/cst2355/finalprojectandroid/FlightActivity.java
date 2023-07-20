package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityCurrencyBinding;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityFlightBinding;

public class FlightActivity extends AppCompatActivity {
    ActivityFlightBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}