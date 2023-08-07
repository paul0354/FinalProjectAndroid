package algonquin.cst2355.finalprojectandroid.flight;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.databinding.FlightDetailsLayoutBinding;

/**
 * Class used for displaying the flight info on a fragment
 *
 * @author Owen Austin
 * @version 1.0
 */
public class FlightDetailsFragment extends Fragment {

    /** Message selected by the user */
    FlightInfo selected;

    /** Constructor, sets the variable to the message selected */
    public FlightDetailsFragment(FlightInfo f) {selected = f;}

    /**
     * Creates the view of the fragement and adds functionality to it
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the details layout view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FlightDetailsLayoutBinding b = FlightDetailsLayoutBinding.inflate(inflater);

        //Displays the info in the fragment
        b.flightNum.setText(R.string.flight_flight + selected.getFlightNum());
        b.des.setText(R.string.flight_des + selected.getDestination());
        b.gate.setText(R.string.flight_gate + selected.getGate());
        b.delay.setText(R.string.flight_delay + selected.getDelay());
        b.terminal.setText(R.string.flight_ter + selected.getTerminal());

        //Sets what happens when you click the save to database button
        b.databaseButton.setOnClickListener(clk -> {

            Executor t = Executors.newSingleThreadExecutor();
            t.execute(() -> {

                selected.id = FlightActivity.fDAO.insertFlight(selected);
                FlightActivity.savedFlights.add(selected);

                    });

            //Displays that the save was successful
            Snackbar.make(b.frag, R.string.flight_add, Snackbar.LENGTH_LONG)
                    .setTextColor(Color.BLACK)
                    .show();

        });

        return b.getRoot();

    }

}
