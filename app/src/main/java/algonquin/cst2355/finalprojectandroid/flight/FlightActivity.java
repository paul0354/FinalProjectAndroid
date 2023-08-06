package algonquin.cst2355.finalprojectandroid.flight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.BearActivity;
import algonquin.cst2355.finalprojectandroid.CurrencyActivity;
import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.TriviaActivity;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityFlightBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FlightListBinding;

/*
TODO: Fix the colors of the toolbar
TODO: New activity shows the saved flights
TODO: Add deletion functionality
TODO: Refactor the strings to strings.xml and add a new language
TODO: Javadoc
TODO: Make it look nice lol
TODO: Anything I'm missing
 */

public class FlightActivity extends AppCompatActivity {
    ActivityFlightBinding binding;
    SharedPreferences prefs;
    SharedPreferences.Editor edt;
    private RecyclerView.Adapter adpt;
    static FlightInfoDAO fDAO;
    ArrayList<FlightInfo> flights = new ArrayList<>();
    static ArrayList<FlightInfo> savedFlights = new ArrayList<>();
    FlightViewModel flightModel;
    RequestQueue queue = null;
    protected String airportCode;
    //for logging
    private static final String TAG = "FlightActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Logging, to check that everything works
        Log.w(TAG, "In onCreate() - loading widgets");

        //sets up internet stuff
        queue = Volley.newRequestQueue(this);

        //Sets up the database and view model
        flightModel = new ViewModelProvider(this).get(FlightViewModel.class);
        //savedFlights = flightModel.listings.getValue();
        FlightDatabase db = Room.databaseBuilder(getApplicationContext(), FlightDatabase.class, "database-name").build();
        fDAO = db.cmDAO();

        //gets saved messages
        //if(savedFlights == null) {
            Log.w(TAG, "savedFlights is not null");
            //flightModel.listings.postValue(savedFlights = new ArrayList<>());
            savedFlights = new ArrayList<>();
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                savedFlights.addAll(fDAO.getAllFlights());
            });
        //}

        //Sets up shared pref
        prefs = getSharedPreferences("FlightData", Context.MODE_PRIVATE);
        edt = prefs.edit();

        //sets input to what is saved
        binding.inputCode.setText(prefs.getString("FlightData", ""));

        setSupportActionBar(binding.flightToolbar);

        //sets what happens when search button is clicked
        binding.searchFlight.setOnClickListener(clk -> {

            Log.w(TAG, "Button has been clicked");

            //saves flight code when clicked
            edt.putString("FlightData", binding.inputCode.getText().toString());
            edt.apply();

            airportCode = binding.inputCode.getText().toString(); //gets input
            String encode = "";
            try {
                encode = URLEncoder.encode(airportCode, "UTF-8"); //encodes string
            }
            catch(UnsupportedEncodingException e) {e.printStackTrace();}

            if(encode.length() != 3) { //checks if input is an airport code

                Toast.makeText(this,
                        "Invalid airport code.",
                        Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this,
                        "Searching Airport " + binding.inputCode.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                String api = "70ecb37f100c89920ba0342196798895";
                String url = "http://api.aviationstack.com/v1/flights?access_key="
                        + api + "&dep_iata=" + encode;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        (response) ->{

                    Log.w(TAG, "In JsonObjectRequest() - request went through.");

                    try{

                        JSONArray data = response.getJSONArray("data");

                        for(int i = 0; i < data.length(); i++) {

                            JSONObject dep = data.getJSONObject(i).getJSONObject("departure");
                            JSONObject arr = data.getJSONObject(i).getJSONObject("arrival");

                            String fN = data.getJSONObject(i).getJSONObject("flight").getString("number");
                            String des = arr.getString("iata");
                            String ter = dep.getString("terminal");
                            String gat = dep.getString("gate");
                            String del = dep.getString("delay");

                            Log.w(TAG, "Flight " + i + "has destination " + des);

                            FlightInfo f = new FlightInfo(fN, des, ter, gat, del);
                            flights.add(f);
                            adpt.notifyItemInserted(flights.size()-1);

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                        }, (error) -> {int i = 0;});
                queue.add(request);
            }
        });

        //gets the airport code from shared prefs

        //for recyclerview
        binding.flights.setAdapter(adpt = new RecyclerView.Adapter<FlightRowHolder>() {
            @NonNull
            @Override
            public FlightRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                FlightListBinding b = FlightListBinding.inflate(getLayoutInflater());
                return new FlightRowHolder(b.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull FlightRowHolder holder, int position) {
                holder.flightNum.setText("");
                holder.destCode.setText("");
                FlightInfo obj = flights.get(position);
                holder.flightNum.setText(obj.getFlightNum());
                holder.destCode.setText(obj.getDestination());
            }

            @Override
            public int getItemCount() {
                return flights.size();
            }
        });

        //for recyclerview
        binding.flights.setLayoutManager(new LinearLayoutManager(this));

        //for the fragment view
        flightModel.listing.observe(this, (newFlightValue) -> {

            int size = savedFlights.size();

            FlightDetailsFragment flightFragment = new FlightDetailsFragment(newFlightValue);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.add(R.id.fragmentLocation, flightFragment)
                    .replace(R.id.fragmentLocation, flightFragment)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    protected void onPause() { //for saving airport code

        super.onPause();
        edt.putString("FlightData", binding.inputCode.getText().toString());
        edt.apply();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.flight_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.savedFlights) {

            Intent saved = new Intent(FlightActivity.this, SavedFlightsActivity.class);
            startActivity(saved);

        }
        else if(item.getItemId()==R.id.flightHelp) {

            AlertDialog.Builder builder = new AlertDialog.Builder(FlightActivity.this);
            builder.setMessage("To use this app, type an airport code into the searchbar. " +
                            "When you click the search button, a list of flights from that " +
                            "airport will appear, along with their destination airport.\n\n" +
                            "To save a flight, click on one of the flights in the list, then click " +
                            "on the Save To Database button.\n\n" +
                            "To view your saved flights, click on the three dots, then click on the " +
                            "View Saved Flights button.\n\n" +
                            "To delete a saved flight, click on one of your saved flights, then " +
                            "click on the Remove From Database button.")
                    .setTitle("How To Use The Flight Tracker")
                    .setPositiveButton("OK", (dialog, cl) -> {})
                    .create().show();

        }
        else if(item.getItemId()==R.id.flightToBear) {

            Intent bear = new Intent(FlightActivity.this, BearActivity.class);
            startActivity(bear);

        }
        else if(item.getItemId()==R.id.flightToCurrency) {

            Intent currency = new Intent(FlightActivity.this, CurrencyActivity.class);
            startActivity(currency);

        }
        else if(item.getItemId()==R.id.flightToTrivia) {

            Intent trivia = new Intent(FlightActivity.this, TriviaActivity.class);
            startActivity(trivia);

        }

        return true;

    }

    class FlightRowHolder extends RecyclerView.ViewHolder {

        TextView flightNum;
        TextView destCode;

        public FlightRowHolder(@NonNull View itemView) {
            super(itemView);

            flightNum = itemView.findViewById(R.id.flightNumber);
            destCode = itemView.findViewById(R.id.destination);

            itemView.setOnClickListener(clk -> {
                int pos = getAbsoluteAdapterPosition();

                FlightInfo s = flights.get(pos);
                flightModel.listing.postValue(s);
            });

        }

    }

}