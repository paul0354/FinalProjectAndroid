package algonquin.cst2355.finalprojectandroid;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.Conversion;
import algonquin.cst2355.finalprojectandroid.ConversionAdapter;
import algonquin.cst2355.finalprojectandroid.ConversionDAO;
import algonquin.cst2355.finalprojectandroid.ConversionDatabase;
import algonquin.cst2355.finalprojectandroid.ConversionDetailsFragment;
import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.data.CurrencyActivityViewModel;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityCurrencyBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FragmentItemDetailBinding;

public class CurrencyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ConversionAdapter.OnItemClickListener {

    ActivityCurrencyBinding binding;
    ArrayList<Conversion> conversions = new ArrayList<>();
    CurrencyActivityViewModel conversionModel;
    private ConversionAdapter myAdapter; // Change the type to ConversionAdapter
    ConversionDatabase myDB;
    ConversionDAO myDAO;
    ConversionDetailsFragment displayedFragment;

    private ArrayList<String> currenciesList = new ArrayList<>();
    private static final String PREFS_NAME = "MyPrefsFile"; // Unique name for your SharedPreferences
    private static final String AMOUNT_KEY = "amountKey"; // Key for storing the amount

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View rootView = binding.getRoot();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.spinnerToCurrency.setAdapter(adapter);
        binding.spinnerFromCurrency.setAdapter(adapter);
        binding.spinnerToCurrency.setOnItemSelectedListener(this);
        binding.spinnerFromCurrency.setOnItemSelectedListener(this);
        setupCurrenciesSpinner();
        binding.recyclerView.setAdapter(myAdapter);
        conversions = new ArrayList<>();
        myDB = Room.databaseBuilder(getApplicationContext(), ConversionDatabase.class, "conversion-database").fallbackToDestructiveMigration().build();
        myDAO = myDB.cDAO();
        conversionModel = new ViewModelProvider(this).get(CurrencyActivityViewModel.class);
        loadSavedConversions();
        if (conversions == null) {
            conversions = new ArrayList<>();
            conversionModel.conversions.postValue(conversions);
        }
        myAdapter = new ConversionAdapter(conversions, this); // Initialize the adapter here
        binding.recyclerView.setAdapter(myAdapter); // Set the adapter for the RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.buttonConvert.setOnClickListener(click -> {
            String amountText = binding.editTextAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                String fromCurrency = binding.spinnerFromCurrency.getSelectedItem().toString();
                String toCurrency = binding.spinnerToCurrency.getSelectedItem().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AMOUNT_KEY, amountText);
                editor.apply();
                double resultAmount = convertCurrency(amount, fromCurrency, toCurrency);
                String currentDateAndTime = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a").format(new Date());

                Conversion newConversion = new Conversion(String.valueOf(resultAmount), currentDateAndTime, "null");
                conversions.add(newConversion);
                myAdapter.notifyItemInserted(conversions.size() - 1);

                // Clear the edit text after conversion and notify adapter of data change
                binding.editTextAmount.setText("");
                myAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CurrencyActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
        String savedAmount = sharedPreferences.getString(AMOUNT_KEY, "");
        binding.editTextAmount.setText(savedAmount);
        binding.displayQueries.setOnClickListener(click -> {
            loadSavedConversions();
        });
        binding.ClearAll.setOnClickListener(click -> {
            deleteAllConversions();
        });
    }

    private void setupCurrenciesSpinner() {
        // Create a list of currencies to populate the spinners
        currenciesList.add("USD");
        currenciesList.add("CAD");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currenciesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerFromCurrency.setAdapter(spinnerAdapter);
        binding.spinnerToCurrency.setAdapter(spinnerAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Check which spinner triggered the selection
        if (parent.getId() == binding.spinnerFromCurrency.getId() || parent.getId() == binding.spinnerToCurrency.getId()) {
            // Get the selected currencies
            String fromCurrency = binding.spinnerFromCurrency.getSelectedItem().toString();
            String toCurrency = binding.spinnerToCurrency.getSelectedItem().toString();
            String amountText = binding.editTextAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                fromCurrency = binding.spinnerFromCurrency.getSelectedItem().toString();
                toCurrency = binding.spinnerToCurrency.getSelectedItem().toString();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle the case when nothing is selected in the spinners (if required)
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        double conversionRate = 2.0;

        if (fromCurrency.equals("USD") && toCurrency.equals("CAD")) {
            return amount * conversionRate;
        } else if (fromCurrency.equals("CAD") && toCurrency.equals("USD")) {
            return amount / conversionRate;
        } else {
            // Handle unsupported currency conversion here, if needed
            return amount;
        }
    }

    @Override
    public void onItemClick(Conversion selectedItem) {
        // Show the fragment ItemDetailFragment using the FragmentManager
        ConversionDetailsFragment fragment = ConversionDetailsFragment.newInstance(selectedItem);
        displayedFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentLocation, fragment)
                .commit();
    }

    private void loadSavedConversions() {
        Executor loadThread = Executors.newSingleThreadExecutor();
        loadThread.execute(() -> {
            List<Conversion> savedConversions = myDAO.getAllConversions();
            Log.d("CurrencyActivity", "Loaded " + savedConversions.size() + " conversions from the database.");
            runOnUiThread(() -> {
                conversions.clear();
                conversions.addAll(savedConversions);
                myAdapter.notifyDataSetChanged();
                Log.d("CurrencyActivity", "Posted " + savedConversions.size() + " conversions to the ViewModel.");
            });
        });
    }
    private void deleteAllConversions() {
        Executor deleteThread = Executors.newSingleThreadExecutor();
        deleteThread.execute(() -> {
            myDAO.deleteAllConversions(); // Delete all conversions from the database
            runOnUiThread(() -> {
                conversions.clear();
                myAdapter.notifyDataSetChanged();
            });
        });
    }
}
