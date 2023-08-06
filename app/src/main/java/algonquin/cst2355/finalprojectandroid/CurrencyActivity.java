package algonquin.cst2355.finalprojectandroid;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.snackbar.Snackbar;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import algonquin.cst2355.finalprojectandroid.data.CurrencyActivityViewModel;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityCurrencyBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String BASE_URL = "https://api.getgeoapi.com/v2/currency/convert";
    private static final String API_KEY = "1a9730ee2471b4251cc0d57ae0195921fa687677";
    private RequestQueue queue;

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
            View parentView = findViewById(android.R.id.content); // Get the root view of the activity
            Snackbar snackbar = Snackbar.make(parentView, "This function is provided by Chawki.", Snackbar.LENGTH_LONG);
            snackbar.show();
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
        myDB = Room.databaseBuilder(getApplicationContext(), ConversionDatabase.class, "conversion-database")
                .fallbackToDestructiveMigration().build();
        myDAO = myDB.cDAO();
        conversionModel = new ViewModelProvider(this).get(CurrencyActivityViewModel.class);
        conversions = conversionModel.conversions.getValue();
        if (conversions == null) {
            conversions = new ArrayList<>();
            conversionModel.conversions.postValue(conversions);
        }

        queue = Volley.newRequestQueue(this);
        binding.buttonConvert.setOnClickListener(click -> {
            String amountText = binding.editTextAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                String fromCurrency = binding.spinnerFromCurrency.getSelectedItem().toString();
                String toCurrency = binding.spinnerToCurrency.getSelectedItem().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String stringUrl = BASE_URL + "?format=json&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount + "&api_key=" + API_KEY;
                editor.putString(AMOUNT_KEY, amountText);
                editor.apply();
                // String currentDateAndTime = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a").format(new Date());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Parse the response JSON
                                    JSONObject rates = response.getJSONObject("rates");
                                    JSONObject currencyObject = rates.getJSONObject(toCurrency);
                                    double rateForAmount = currencyObject.getDouble("rate_for_amount");
                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    rateForAmount = Double.parseDouble(decimalFormat.format(rateForAmount));
                                    String rateText = String.valueOf(rateForAmount);
                                    binding.editTextResult.setText(rateText);
                                    String currentDateAndTime = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a").format(new Date());
                                    String convertedDetails = amountText + " " + fromCurrency + " corresponds to " + rateText + " " + toCurrency;
                                    Conversion newConversion = new Conversion(amountText, currentDateAndTime, convertedDetails, fromCurrency, toCurrency);
                                    conversions.add(newConversion);
                                    myAdapter.notifyItemInserted(conversions.size() - 1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(CurrencyActivity.this, "Error occurred during conversion.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                queue.add(request);
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
                if(conversions.isEmpty()){
                    return;
                }
                Conversion con = conversions.get(position);
                holder.textViewResultAmount.setText(con.getConvertedDetails());
                holder.textViewDate.setText(con.getTimeSemt());
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
        currenciesList.add("EUR");
        currenciesList.add("DZD");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currenciesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerFromCurrency.setAdapter(spinnerAdapter);
        binding.spinnerToCurrency.setAdapter(spinnerAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void loadSavedConversions() {
        Executor loadThread = Executors.newSingleThreadExecutor();
        loadThread.execute(() -> {
            List<Conversion> savedConversions = myDAO.getAllConversions();
            runOnUiThread(() -> {
                conversions.clear();
                if (!savedConversions.isEmpty()) {
                    for (Conversion savedConversion : savedConversions) {
                        double amount = Double.parseDouble(savedConversion.getConversionAmount());
                        String fromCurrency = savedConversion.getCurrencyFrom();
                        String toCurrency = savedConversion.getCurrencyTo();
                        updateConversion(amount, fromCurrency, toCurrency, savedConversion);
                        conversions.add(savedConversion);
                    }
                    myAdapter.notifyDataSetChanged(); // Notify the adapter once after all conversions are added
                } else {
                    myAdapter.notifyDataSetChanged(); // Notify the adapter to clear the RecyclerView when no conversions are present
                }
            });
        });
    }
    private void deleteAllConversions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear All Conversions")
                .setMessage("Are you sure you want to delete all saved conversions?")
                .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Execute the delete operation on a separate thread
                        Executor deleteThread = Executors.newSingleThreadExecutor();
                        deleteThread.execute(() -> {
                            myDAO.deleteAllConversions(); // Delete all conversions from the database
                            runOnUiThread(() -> {
                                conversions.clear();
                                myAdapter.notifyDataSetChanged();
                            });
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the delete operation
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void updateConversion(double amount, String fromCurrency, String toCurrency, Conversion conversion) {
        String stringUrl = BASE_URL + "?format=json&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount + "&api_key=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the response JSON
                            JSONObject rates = response.getJSONObject("rates");
                            JSONObject currencyObject = rates.getJSONObject(toCurrency);
                            double rateForAmount = currencyObject.getDouble("rate_for_amount");
                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                            rateForAmount = Double.parseDouble(decimalFormat.format(rateForAmount));
                            String rateText = String.valueOf(rateForAmount);

                            // Update the converted details for the conversion
                            String convertedDetails = amount + " " + fromCurrency + " corresponds to " + rateText + " " + toCurrency;
                            conversion.setConversionDetails(convertedDetails);

                            // Save the updated conversion to the database using Executor
                            Executor updateThread = Executors.newSingleThreadExecutor();
                            updateThread.execute(() -> {
                                myDAO.updateConversion(conversion);
                            });

                            // Notify the adapter to update the RecyclerView with the updated conversion
                            runOnUiThread(() -> {
                                myAdapter.notifyDataSetChanged();
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

}
