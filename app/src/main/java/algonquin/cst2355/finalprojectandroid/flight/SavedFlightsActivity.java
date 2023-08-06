package algonquin.cst2355.finalprojectandroid.flight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.databinding.ActivitySavedFlightsBinding;
import algonquin.cst2355.finalprojectandroid.databinding.FlightListBinding;

public class SavedFlightsActivity extends AppCompatActivity {

    ActivitySavedFlightsBinding binding;

    FlightViewModel flightModel;

    private RecyclerView.Adapter adpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedFlightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        flightModel = new ViewModelProvider(this).get(FlightViewModel.class);

        binding.savedFlightsRecycler.setAdapter(adpt = new RecyclerView.Adapter<SavedFlightRowHolder>() {
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

    }

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
            });

        }

    }
}