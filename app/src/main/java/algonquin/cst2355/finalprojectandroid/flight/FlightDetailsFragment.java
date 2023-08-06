package algonquin.cst2355.finalprojectandroid.flight;

import android.app.AlertDialog;
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

import algonquin.cst2355.finalprojectandroid.databinding.FlightDetailsLayoutBinding;

public class FlightDetailsFragment extends Fragment {

    FlightInfo selected;

    public FlightDetailsFragment(FlightInfo f) {selected = f;}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FlightDetailsLayoutBinding b = FlightDetailsLayoutBinding.inflate(inflater);

        b.flightNum.setText("Flight " + selected.getFlightNum());
        b.des.setText("Destination airport = " + selected.getDestination());
        b.gate.setText("Gate = " + selected.getGate());
        b.delay.setText("Delay = " + selected.getDelay());
        b.terminal.setText("Terminal = " + selected.getTerminal());

        b.databaseButton.setOnClickListener(clk -> {

            Executor t = Executors.newSingleThreadExecutor();
            t.execute(() -> {

                selected.id = FlightActivity.fDAO.insertFlight(selected);
                FlightActivity.savedFlights.add(selected);

                    });

            //text is not appearing
            Snackbar.make(b.frag, "Flight added to the database.", Snackbar.LENGTH_LONG)
                    .setTextColor(Color.BLACK)
                    .show();

        });

        return b.getRoot();

    }

}
