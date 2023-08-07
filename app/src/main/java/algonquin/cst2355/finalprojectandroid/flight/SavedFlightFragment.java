package algonquin.cst2355.finalprojectandroid.flight;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.R;
import algonquin.cst2355.finalprojectandroid.databinding.SavedFlightsLayoutBinding;

/**
 * Fragment containing the info of a saved flight. Allows user to remove flight from the saved flights.
 * This is honestly one of the worse things I have ever coded. I wish I did this differently.
 *
 * @author Owen Austin
 * @version 1.0
 */
public class SavedFlightFragment extends Fragment {

    /** Message selected by user */
    FlightInfo selected;

    /**
     * Constructor, sets the message variable to the one clicked by the user
     */
    public SavedFlightFragment(FlightInfo f) {
        selected = f;
    }

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
     * @return the saved flights layout view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        SavedFlightsLayoutBinding b = SavedFlightsLayoutBinding.inflate(inflater);

        b.savedFlightNum.setText(R.string.flight_flight + selected.getFlightNum());
        b.savedDes.setText(R.string.flight_des + selected.getDestination());
        b.savedGate.setText(R.string.flight_gate + selected.getGate());
        b.savedDelay.setText(R.string.flight_delay + selected.getDelay());
        b.savedTerminal.setText(R.string.flight_ter + selected.getTerminal());

        //Allows the user to delete the flight from their saved flights
        //So jank
        b.deleteButton.setOnClickListener(clk -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.flight_prompt)
                    .setTitle(R.string.flight_question)
                    .setPositiveButton(R.string.flight_yes, (dialog, cl) -> {
                        Executor t = Executors.newSingleThreadExecutor();
                        t.execute(() -> {
                            FlightActivity.fDAO.deleteFlight(selected);
                        });
                        SavedFlightsActivity.temp--;
                        Snackbar.make(b.getRoot(), R.string.flight_remove, Snackbar.LENGTH_LONG)
                                .setTextColor(Color.BLACK)
                                .show();
                    })
                    .setNegativeButton(R.string.flight_no, (dialog, cl) -> {})
                    .create().show();


        });

        return b.getRoot();

    }

}
