package algonquin.cst2355.finalprojectandroid.flight;

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

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.databinding.ActivitySavedFlightsBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FlightListBinding;

public class SavedFlightsActivity extends AppCompatActivity {

    ActivitySavedFlightsBinding binding;

    FlightViewModel flightModel;

    static protected RecyclerView.Adapter adapt;
    //ArrayList<FlightInfo> saved = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedFlightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        flightModel = new ViewModelProvider(this).get(FlightViewModel.class);

        FlightActivity.savedFlights = flightModel.listings.getValue();

        if(FlightActivity.savedFlights == null) {
            flightModel.listings.postValue(FlightActivity.savedFlights = new ArrayList<>());
            Executor t = Executors.newSingleThreadExecutor();
            t.execute(() -> {
                FlightActivity.savedFlights.addAll(FlightActivity.fDAO.getAllFlights());
                runOnUiThread(() -> binding.savedFlightsRecycler.setAdapter(adapt));
            });
        }

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

        flightModel.listing.observe(this, (savedFlight) -> {

            SavedFlightFragment frag = new SavedFlightFragment(savedFlight);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.add(R.id.savedFragment, frag)
                    .replace(R.id.savedFragment, frag)
                    .addToBackStack(null)
                    .commit();

//            adapt.notifyDataSetChanged();
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        adapt.notifyDataSetChanged();
//
//    }

    class SavedFlightRowHolder extends RecyclerView.ViewHolder {

        TextView flightNum;
        TextView destCode;

        public SavedFlightRowHolder(@NonNull View itemView) {
            super(itemView);

            flightNum = itemView.findViewById(R.id.flightNumber);
            destCode = itemView.findViewById(R.id.destination);

            itemView.setOnClickListener(clk -> {
                int pos = getAbsoluteAdapterPosition();

                FlightInfo s = FlightActivity.savedFlights.get(pos);
                flightModel.listing.postValue(s);
//                adapt.notifyDataSetChanged();
            });

        }

    }
}