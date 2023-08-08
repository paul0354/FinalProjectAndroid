package algonquin.cst2355.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.databinding.ActivitySavedFlightsBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FlightListBinding;

/**
 * This page allows users to view their saved flights and click on any of them to view more info
 *
 * @author Owen Austin
 * @version 1.0
 */
public class SavedFlightsActivity extends AppCompatActivity {

    /** For the layout and the items in it */
    ActivitySavedFlightsBinding binding;

    /** View model for the flight listings */
    FlightViewModel flightModel;

    /** Adapter for the recycle view */
    protected RecyclerView.Adapter adapt;

    /** Position of the clicked flight */
    int pos;

    /** Used for counting how many elements are in the array list, so jank */
    static int temp;

    /**
     * Sets the layout and recycler view for the saved flights list
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedFlightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Sets up the view model
        flightModel = new ViewModelProvider(this).get(FlightViewModel.class);

        //gets the value of the listings
        FlightActivity.savedFlights = flightModel.listings.getValue();

        //sets up the saved flights if there was nothing in the listing
        if(FlightActivity.savedFlights == null) {
            flightModel.listings.postValue(FlightActivity.savedFlights = new ArrayList<>());
            Executor t = Executors.newSingleThreadExecutor();
            t.execute(() -> {
                FlightActivity.savedFlights.addAll(FlightActivity.fDAO.getAllFlights());
                temp = FlightActivity.savedFlights.size();
                runOnUiThread(() -> binding.savedFlightsRecycler.setAdapter(adapt));
            });
        }

        //sets up the recycler view
        binding.savedFlightsRecycler.setAdapter(adapt = new RecyclerView.Adapter<SavedFlightRowHolder>() {
            @NonNull
            @Override
            public SavedFlightRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                FlightListBinding b = FlightListBinding.inflate(getLayoutInflater());
                return new SavedFlightRowHolder(b.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull SavedFlightRowHolder holder, int position) {
                holder.flightNum.setText("");
                holder.destCode.setText("");
                FlightInfo s = FlightActivity.savedFlights.get(position);
                holder.flightNum.setText(s.getFlightNum());
                holder.destCode.setText(s.getDestination());
            }

            @Override
            public int getItemCount() {return FlightActivity.savedFlights.size();}
        });

        binding.savedFlightsRecycler.setLayoutManager(new LinearLayoutManager(this));

        //sets up the fragment
        flightModel.listing.observe(this, (savedFlight) -> {

            SavedFlightFragment frag = new SavedFlightFragment(savedFlight);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.add(R.id.savedFragment, frag)
                    .replace(R.id.savedFragment, frag)
                    .addToBackStack(null)
                    .commit();

        });

    }

    /**
     * When back button is pressed, checks if something was removed from the database
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(FlightActivity.savedFlights.size() != temp) {
            FlightActivity.savedFlights.remove(pos);
            adapt.notifyItemRemoved(pos);
            Executor t = Executors.newSingleThreadExecutor();
            t.execute(() -> temp = FlightActivity.savedFlights.size());
        }

    }

    /**
     * This class gives functionality to the elements in the recyclerview
     */
    class SavedFlightRowHolder extends RecyclerView.ViewHolder {

        TextView flightNum;
        TextView destCode;

        public SavedFlightRowHolder(@NonNull View itemView) {
            super(itemView);

            flightNum = itemView.findViewById(R.id.flightNumber);
            destCode = itemView.findViewById(R.id.destination);

            itemView.setOnClickListener(clk -> {
                pos = getAbsoluteAdapterPosition();

                FlightInfo s = FlightActivity.savedFlights.get(pos);
                flightModel.listing.postValue(s);
            });

        }

    }
}