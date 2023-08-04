package algonquin.cst2355.finalprojectandroid;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import algonquin.cst2355.finalprojectandroid.data.CurrencyActivityViewModel;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityCurrencyBinding;

public class CurrencyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , ConversionDetailsFragment.OnConversionDeletedListener  {

    ActivityCurrencyBinding binding;
    ArrayList<Conversion> conversions = new ArrayList<>();
    CurrencyActivityViewModel conversionModel;
    private RecyclerView.Adapter myAdapter;

    ConversionDatabase myDB;
    ConversionDAO myDAO;
     ConversionDetailsFragment displayedFragment;
    private ArrayList<String> currenciesList = new ArrayList<>();
    private static final String PREFS_NAME = "MyPrefsFile"; // Unique name for your SharedPreferences
    private static final String AMOUNT_KEY = "amountKey"; // Key for storing the amount

    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_1) {
            Intent bearPage = new Intent(CurrencyActivity.this, BearActivity.class);
            startActivity(bearPage);
        } else if (item.getItemId() == R.id.item_2) {
            Intent main = new Intent(CurrencyActivity.this, MainActivity.class);
            startActivity(main);
        } else if (item.getItemId() == R.id.item_3) {
            Intent flight = new Intent(CurrencyActivity.this, FlightActivity.class);
            startActivity(flight);
        } else if (item.getItemId() == R.id.item_4) {
            Intent trivia = new Intent(CurrencyActivity.this, TriviaActivity.class);
            startActivity(trivia);
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);
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
        myDB = Room.databaseBuilder(getApplicationContext(), ConversionDatabase.class, "conversion-database").fallbackToDestructiveMigration().build();
        myDAO = myDB.cDAO();
        conversionModel = new ViewModelProvider(this).get(CurrencyActivityViewModel.class);
        loadSavedConversions();
        conversions = conversionModel.conversions.getValue();
        if (conversions == null) {
            conversions = new ArrayList<>();
            conversionModel.conversions.postValue(conversions);
        }
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
                binding.editTextResult.setText(String.valueOf(resultAmount));
                Conversion newConversion = new Conversion(String.valueOf(resultAmount), currentDateAndTime, "null");
                conversions.add(newConversion);
                myAdapter.notifyItemInserted(conversions.size() - 1);
                myAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CurrencyActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversion, parent, false);
                return new MyRowHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                Conversion con = conversions.get(position);
                //holder.editTextAmount.setText(con.getConversionResult());
                holder.textViewDate.setText(con.getTimeSemt());
                holder.textViewResultAmount.setText(con.getConversionResult());
            }

            @Override
            public int getItemCount() {
                return conversions.size();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String savedAmount = sharedPreferences.getString(AMOUNT_KEY, "");
        binding.editTextAmount.setText(savedAmount);
        binding.displayQueries.setOnClickListener(click -> {
            loadSavedConversions();
        });
        binding.ClearAll.setOnClickListener(click -> {
            deleteAllConversions();
        });
        conversionModel.selectedConversion.observe(this, (newConversionValue) -> {
            if (newConversionValue != null) {
                binding.fragmentLocation.setVisibility(View.VISIBLE);
                displayedFragment = ConversionDetailsFragment.newInstance(newConversionValue);
                displayedFragment.setOnConversionDeletedListener(this); // Set the activity as the listener
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentLocation, displayedFragment)
                        .commit();
            }
        });
    }

    @Override
    public void onConversionDeleted(Conversion deletedConversion) {
        Log.d("CurrencyActivity", "onConversionDeleted() called");
        runOnUiThread(() -> {
            conversions.remove(deletedConversion);
            myAdapter.notifyDataSetChanged();
        });
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView editTextResult;
        TextView editTextAmount;
        TextView textViewDate;
        TextView textViewResultAmount;
        private ConversionDetailsFragment displayedFragment;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(clk -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Conversion selectedConversion = conversions.get(position);
                    displayedFragment = ConversionDetailsFragment.newInstance(selectedConversion);
                    displayedFragment.setOnConversionDeletedListener(CurrencyActivity.this);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentLocation, displayedFragment)
                            .commit();
                }
//                if (position != RecyclerView.NO_POSITION) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CurrencyActivity.this);
//                    builder.setTitle("Question");
//                    builder.setNegativeButton("No", (dialog, cl) -> {
//                    });
//                    builder.setPositiveButton("Yes", (dialog, cl) -> {
//                        Conversion c = conversions.get(position);
//                        Executor thread1 = Executors.newSingleThreadExecutor();
//                        thread1.execute(() -> {
//                            myDAO.deleteConversion(c);
//                            conversions.remove(position);
//                            runOnUiThread(() -> {
//                                myAdapter.notifyDataSetChanged();
//                            });
//                        });
//                        Snackbar.make(editTextResult, "You deleted the conversion #" + position, Snackbar.LENGTH_LONG)
//                                .setAction("Undo", click -> {
//                                    Executor thread2 = Executors.newSingleThreadExecutor();
//                                    thread2.execute(() -> {
//                                        myDAO.insertConversion(c);
//                                        conversions.add(position, c);
//                                        runOnUiThread(() -> {
//                                            myAdapter.notifyDataSetChanged();
//                                        });
//                                    });
//                                })
//                                .show();
//                    }).create().show();
//                  }
            });
            editTextResult = itemView.findViewById(R.id.editTextResult);
            editTextAmount = itemView.findViewById(R.id.editTextAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewResultAmount = itemView.findViewById(R.id.textViewResultAmount);
        }
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

    private void loadSavedConversions() {
        Executor loadThread = Executors.newSingleThreadExecutor();
        loadThread.execute(() -> {
            List<Conversion> savedConversions = myDAO.getAllConversions();
            Log.d("CurrencyActivity", "Loaded " + savedConversions.size() + " conversions from the database.");
            runOnUiThread(() -> {
                conversions.clear();
                conversions.addAll(savedConversions);
                if (myAdapter == null) {

                    binding.recyclerView.setAdapter(null);
                } else {
                    myAdapter.notifyDataSetChanged();
                }
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
