package algonquin.cst2355.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import algonquin.cst2355.finalprojectandroid.data.FlightViewModel;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityFlightBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FlightListBinding;

/**
 * This page allows the user to search for flights after entering a source airport code.
 * The user can save the flights from a fragment, and view them from the toolbar.
 *
 * @author Owen Austin
 * @version 1.0
 */
public class FlightActivity extends AppCompatActivity {

    /** For the layout and items in the layout */
    ActivityFlightBinding binding;

    /** Used to save the airport search term for next time */
    SharedPreferences prefs;

    /** Editor for SharedPreferences */
    SharedPreferences.Editor edt;

    /** Adapter used for the RecyclerView, allows refreshing of the RecyclerView */
    private RecyclerView.Adapter adpt;

    /** Access to the flight database */
    static FlightInfoDAO fDAO;

    /** Array list that holds the flights found when searching */
    ArrayList<FlightInfo> flights = new ArrayList<>();

    /** Array list used to hold the saved flights, used in SavedFlightsActivity */
    static ArrayList<FlightInfo> savedFlights = new ArrayList<>();

    /** View model for the flight listings */
    FlightViewModel flightModel;

    /** RequestQueue for the searching of the flights */
    RequestQueue queue = null;

    /** AirportCode entered by the user */
    protected String airportCode;

    /**
     * Sets up the layout, onClickListners, JSONRequest, Fragment, database, and SharedPreferences
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //sets up internet stuff
        queue = Volley.newRequestQueue(this);

        //Sets up the database and view model
        flightModel = new ViewModelProvider(this).get(FlightViewModel.class);
        FlightDatabase db = Room.databaseBuilder(getApplicationContext(), FlightDatabase.class, "database-name") .fallbackToDestructiveMigration().build();
        fDAO = db.cmDAO();

        //Sets up the saved flights array list
        savedFlights = new ArrayList<>();
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> savedFlights.addAll(fDAO.getAllFlights()));

        //Sets up shared pref
        prefs = getSharedPreferences("FlightData", Context.MODE_PRIVATE);
        edt = prefs.edit();

        //sets input to what is saved
        binding.inputCode.setText(prefs.getString("FlightData", ""));

        //Sets up the toolbar
        setSupportActionBar(binding.flightToolbar);

        //sets what happens when search button is clicked
        binding.searchFlight.setOnClickListener(clk -> {

            //clears what's already on screen
            flights.clear();
            adpt.notifyDataSetChanged();

            //saves flight code when clicked
            edt.putString("FlightData", binding.inputCode.getText().toString());
            edt.apply();

            //gets the input from user and encodes it
            airportCode = binding.inputCode.getText().toString();
            String encode = "";
            try {
                encode = URLEncoder.encode(airportCode, "UTF-8"); //encodes string
            }
            catch(UnsupportedEncodingException e) {e.printStackTrace();}

            //checks if input is an airport code
            if(encode.length() != 3) {

                Toast.makeText(this,
                        R.string.flight_invalid,
                        Toast.LENGTH_SHORT).show();

            }
            else {
                //lets the user know that flights are being searched for
                Toast.makeText(this,
                        getString(R.string.flight_searching) + " " + binding.inputCode.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                String api = "70ecb37f100c89920ba0342196798895";
                String url = "http://api.aviationstack.com/v1/flights?access_key="
                        + api + "&dep_iata=" + encode;

                //sends a JSONRequest to avaiationstack
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        (response) ->{

                    try{

                        JSONArray data = response.getJSONArray("data");

                        //loops through the JSONRequest, adds flights to the RecyclerView
                        for(int i = 0; i < data.length(); i++) {

                            JSONObject dep = data.getJSONObject(i).getJSONObject("departure");
                            JSONObject arr = data.getJSONObject(i).getJSONObject("arrival");

                            //String fN = data.getJSONObject(i).getJSONObject("flight").getString("number") + " " + getString(R.string.flight_joiner) + " " + airportCode.toUpperCase();
                            String fN = data.getJSONObject(i).getJSONObject("flight").getString("number");
                            if(fN.equals("null")) { fN = "N/A"; }
                            fN = fN + " " + getString(R.string.flight_joiner) + " " + airportCode.toUpperCase();
                            String des = arr.getString("iata");
                            if(des.equals("null")) { des = "N/A";}
                            String ter = dep.getString("terminal");
                            if(ter.equals("null")) { ter = "N/A"; }
                            String gat = dep.getString("gate");
                            if(gat.equals("null")) { gat = "N/A"; }
                            String del = dep.getString("delay");
                            if(del.equals("null")) { del = "N/A"; }

                            FlightInfo f = new FlightInfo(fN, des, ter, gat, del);
                            flights.add(f);
                            adpt.notifyItemInserted(flights.size()-1);

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                        }, (error) -> {});
                queue.add(request);
            }
        });

        //Sets the adapter up for the RecyclerView
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

        //When an item is clicked in the recyclerview, creates a fragment with more info
        flightModel.listing.observe(this, (newFlightValue) -> {

            FlightDetailsFragment flightFragment = new FlightDetailsFragment(newFlightValue);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.add(R.id.fragmentLocation, flightFragment)
                    .replace(R.id.fragmentLocation, flightFragment)
                    .addToBackStack(null)
                    .commit();
        });

    }

    /**
     * Saves the input from the user when this activity is paused
     */
    @Override
    protected void onPause() { //for saving airport code

        super.onPause();
        edt.putString("FlightData", binding.inputCode.getText().toString());
        edt.apply();

    }

    /**
     * Creates the options menu from a given menu
     *
     * @param menu The options menu in which you place your items.
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.flight_toolbar, menu);
        return true;
    }

    /**
     * Gives functionality to each of the items in the menu
     *
     * @param item The menu item that was selected.
     *
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.savedFlights) { //Goes to the saved flights activity

            Intent saved = new Intent(FlightActivity.this, SavedFlightsActivity.class);
            startActivity(saved);

        }
        else if(item.getItemId()==R.id.flightHelp) { //Opens a help dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(FlightActivity.this);
            builder.setMessage(R.string.flight_help)
                    .setTitle(R.string.flight_help_title)
                    .setPositiveButton(R.string.flight_confirm, (dialog, cl) -> {})
                    .create().show();

        }
        else if(item.getItemId()==R.id.flightToBear) { //Goes to the Bear Activity

            Intent bear = new Intent(FlightActivity.this, BearActivity.class);
            startActivity(bear);

        }
        else if(item.getItemId()==R.id.flightToCurrency) { //Goes to the Currency Activity

            Intent currency = new Intent(FlightActivity.this, CurrencyActivity.class);
            startActivity(currency);

        }
        else if(item.getItemId()==R.id.flightToTrivia) { //Goes to the Trivia Activity

            Intent trivia = new Intent(FlightActivity.this, TriviaActivity.class);
            startActivity(trivia);

        }

        return true;

    }

    /**
     * This class gives functionality to  the elements in the recyclerview
     */
    class FlightRowHolder extends RecyclerView.ViewHolder {

        /** Where the flight number goes in the recyclerview */
        TextView flightNum;

        /** Where the destination code goes in the recyclerview */
        TextView destCode;

        /**
         * Adds on click functionality to the elements in a recyclerview
         *
         * @param itemView the specific element in the recyclerview
         */
        public FlightRowHolder(@NonNull View itemView) {
            super(itemView);

            //pairs the flightNum and destCode to the ids of the recyclerview
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