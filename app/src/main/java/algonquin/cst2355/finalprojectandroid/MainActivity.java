package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityMainBinding;
import algonquin.cst2355.finalprojectandroid.flight.FlightActivity;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bearButton.setOnClickListener(clk -> {
            Intent bearPage = new Intent(MainActivity.this, BearActivity.class);
            startActivity(bearPage);
        });

        binding.currencyButton.setOnClickListener(clk -> {
            Intent currencyPage = new Intent(MainActivity.this, CurrencyActivity.class);
            startActivity(currencyPage);
        });

        binding.flightButton.setOnClickListener(clk -> {
            Intent flightPage = new Intent(MainActivity.this, FlightActivity.class);
            startActivity(flightPage);
        });

        binding.triviaButton.setOnClickListener(clk -> {
            Intent triviaPage = new Intent(MainActivity.this, TriviaActivity.class);
            startActivity(triviaPage);
        });
    }
}