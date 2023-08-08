package algonquin.cst2355.finalprojectandroid;
/**
 * Name : Chawki Moulayat
 * Section : 23S_CST2335_022
 * Project : Final Project
 * Function : Currency Converter
 *
 * @Author : Chawki Moulayat
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
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
/**
 * The CurrencyActivity class represents the main activity of the currency conversion app.
 * It allows users to convert currencies, view saved conversions, and get updated conversions.
 */
public class CurrencyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , ConversionDetailsFragment.OnConversionDeletedListener  {

    /**
     * Represents the binding object for the CurrencyActivity layout.
     * It provides access to the views defined in the layout XML file.
     */
    ActivityCurrencyBinding binding;

    /**
     * A list to store the Conversion objects representing the saved conversions.
     * It is initialized as an empty ArrayList.
     */
    ArrayList<Conversion> conversions = new ArrayList<>();

    /**
     * An instance of the CurrencyActivityViewModel used to handle data related to currency conversions.
     */
    CurrencyActivityViewModel conversionModel;

    /**
     * The RecyclerView adapter used to display the saved conversions in the RecyclerView.
     */
    private RecyclerView.Adapter myAdapter;

    /**
     * The database instance for handling conversions database.
     */
    ConversionDatabase myDB;

    /**
     * The Data Access Object (DAO) for accessing the ConversionDatabase and performing database operations.
     */
    ConversionDAO myDAO;

    /**
     * Represents the currently displayed ConversionDetailsFragment in the activity.
     */
    ConversionDetailsFragment displayedFragment;

    /**
     * A list to store the available currencies used in the currencies spinner.
     */
    private ArrayList<String> currenciesList = new ArrayList<>();

    /**
     * The name of the SharedPreferences file used to store user preferences for this activity.
     */
    private static final String PREFS_NAME = "MyPrefsFile";

    /**
     * The key used to store and retrieve the last entered amount from SharedPreferences.
     */
    private static final String AMOUNT_KEY = "amountKey";

    /**
     * The SharedPreferences object for accessing and modifying user preferences for this activity.
     */
    private SharedPreferences sharedPreferences;

    /**
     * The base URL for the currency conversion API.
     */
    private static final String BASE_URL = "https://api.getgeoapi.com/v2/currency/convert";

    /**
     * The API key used for accessing the currency conversion API.
     */
    private static final String API_KEY = "1a9730ee2471b4251cc0d57ae0195921fa687677";

    /**
     * The RequestQueue used for making network requests to the currency conversion API.
     */
    private RequestQueue queue;
    /**
     * Initialize the options menu for the activity.
     *
     * @param menu The options menu in which the menu items are placed.
     * @return true to display the menu, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * Called when a menu item is selected from the options menu.
     *
     * @param item The selected menu item.
     * @return true to indicate that the event has been consumed and no further processing is needed, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_1) {
            Intent bearPage = new Intent(CurrencyActivity.this, BearActivity.class);
            startActivity(bearPage);
        } else if (item.getItemId() == R.id.item_2) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml(getString(R.string.help_currency),
                            Html.FROM_HTML_MODE_LEGACY))
                    .setTitle(getString(R.string.about_converter))
                    .create().show();
            return true;
        } else if (item.getItemId() == R.id.item_3) {
            Intent flight = new Intent(CurrencyActivity.this, FlightActivity.class);
            startActivity(flight);
        } else if (item.getItemId() == R.id.item_4) {
            Intent trivia = new Intent(CurrencyActivity.this, TriviaActivity.class);
            startActivity(trivia);
        }
        return true;
    }
    /**
     * Called when the activity is created. This method initializes the user interface and sets up various components
     * for the currency conversion application.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity, or null if there is no saved state.
     */
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
        // Creates a new instance of the RequestQueue to handle network requests using the Volley library.
        queue = Volley.newRequestQueue(this);
        binding.buttonConvert.setOnClickListener(click -> {
            String amountText = binding.editTextAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                String fromCurrency = binding.spinnerFromCurrency.getSelectedItem().toString();
                String toCurrency = binding.spinnerToCurrency.getSelectedItem().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Create a formatted conversion details string.
                String stringUrl = BASE_URL + "?format=json&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount + "&api_key=" + API_KEY;
                editor.putString(AMOUNT_KEY, amountText);
                editor.apply();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringUrl, null,
                        new Response.Listener<JSONObject>() {
                            /**

                             Callback method called when the API response is received successfully. It handles the processing of the

                             response JSON to obtain the currency conversion rate and updates the UI with the converted rate. It also

                             creates a new Conversion object representing the currency conversion result and adds it to the list of

                             conversions for display in the RecyclerView.

                             @param response The JSONObject containing the API response data.
                             */
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
                                    // Update the UI with the converted rate
                                    binding.editTextResult.setText(rateText);
                                    // Your existing code to add the conversion to the list
                                    String currentDateAndTime = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a").format(new Date());
                                    String convertedDetails = amountText + " " + fromCurrency + " corresponds to " + rateText + " " + toCurrency;
                                    Conversion newConversion = new Conversion(amountText, currentDateAndTime, convertedDetails, fromCurrency, toCurrency);
                                    // Add the new Conversion object to the list of conversions.
                                    conversions.add(newConversion);
                                    myAdapter.notifyItemInserted(conversions.size() - 1);
                                    String snackbarMessage = "Conversion successful!";
                                    Snackbar snackbar = Snackbar.make(binding.getRoot(), snackbarMessage, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    /**

                     Callback method called when there is an error in processing the API request or receiving the API response.

                     It handles the error by displaying a Toast message indicating that an error occurred during the currency conversion.

                     Additionally, it prints the stack trace of the error for debugging purposes.

                     @param error The VolleyError object representing the error encountered during the API request or response.
                     */
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
            /**

             Called when the RecyclerView needs to create a new ViewHolder for displaying items in the list.

             @param parent The ViewGroup in which the new View will be added after it is bound to an adapter position.

             @param viewType The type of the new View. This parameter is used if the RecyclerView has more than one type of item view.

             @return A new instance of the MyRowHolder class, which is a custom ViewHolder used to display items in the RecyclerView.
             */
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversion, parent, false);
                return new MyRowHolder(itemView);
            }
            /**

             Called by the RecyclerView to bind data to the views in the specified ViewHolder.

             @param holder The ViewHolder in which to bind the data.

             @param position The position of the item within the RecyclerView's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                if(conversions.isEmpty()){
                    return;
                }
                Conversion con = conversions.get(position);
                holder.textViewResultAmount.setText(con.getConvertedDetails());
                holder.textViewDate.setText(con.getTimeSemt());
            }
            /**

             Returns the total number of items in the data set held by the adapter.
             @return The total number of items in the data set.
             */
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
    /**

     Callback method invoked when a Conversion is deleted.

     @param deletedConversion The Conversion object that was deleted.
     */
    @Override
    public void onConversionDeleted(Conversion deletedConversion) {
        runOnUiThread(() -> {
            conversions.remove(deletedConversion);
            myAdapter.notifyDataSetChanged();
        });
    }
    /**
     * A ViewHolder class to represent individual rows of the RecyclerView.
     * It holds references to views for displaying conversion details.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView editTextResult;
        TextView editTextAmount;
        TextView textViewDate;
        TextView textViewResultAmount;
        private ConversionDetailsFragment displayedFragment;
        /**

         Constructor for the MyRowHolder class.

         @param itemView The View object that represents the layout of each item in the RecyclerView.
         */
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
    /**
     * Initializes and sets up the currencies spinner with available currencies.
     */
    private void setupCurrenciesSpinner() {
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
    /**

     Callback method triggered when an item in the AdapterView is selected.
     @param parent The AdapterView where the selection happened (e.g., Spinner).
     @param view The View within the AdapterView that was clicked (the selected item View).
     @param position The position of the selected item in the AdapterView.
     @param id The row id of the selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    /**

     Callback method triggered when nothing is selected in the AdapterView.
     @param parent The AdapterView where the selection is empty (e.g., Spinner).
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    /**
     * Loads and displays saved and updated conversions from the database into the RecyclerView.
     */
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
    /**
     * Deletes all saved conversions from the database and updates the RecyclerView.
     */
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
   /**
   * Performs the currency conversion using an API call and updates the given Conversion object.
   *
   * @param amount      The amount to convert.
   * @param fromCurrency The currency to convert from.
   * @param toCurrency   The currency to convert to.
   * @param conversion  The Conversion object to update with the result.
   * */
    private void updateConversion(double amount, String fromCurrency, String toCurrency, Conversion conversion) {
        // Prepare the API URL with parameters...
        String stringUrl = BASE_URL + "?format=json&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount + "&api_key=" + API_KEY;
        // Send a JSON request to the API to get the conversion rate.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringUrl, null,
                new Response.Listener<JSONObject>() {
                    /**
                     * Callback method to handle the response from a network request made using Volley.
                     *
                     * @param response The JSONObject response received from the network request.
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the response JSON
                            JSONObject rates = response.getJSONObject("rates");
                            JSONObject cadObject = rates.getJSONObject(toCurrency);
                            double rateForAmount = cadObject.getDouble("rate_for_amount");
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
            /**

             Callback method triggered when a Volley error response is received.
             @param error The VolleyError object containing information about the error.
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }
}
