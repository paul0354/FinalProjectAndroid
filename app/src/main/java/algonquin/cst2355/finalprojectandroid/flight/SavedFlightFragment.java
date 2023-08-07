package algonquin.cst2355.finalprojectandroid.flight;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.databinding.FlightDetailsLayoutBinding;
import algonquin.cst2355.finalprojectandroid.databinding.SavedFlightsLayoutBinding;

public class SavedFlightFragment extends Fragment {

    FlightInfo selected;

    public SavedFlightFragment(FlightInfo f) {
        selected = f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        SavedFlightsLayoutBinding b = SavedFlightsLayoutBinding.inflate(inflater);

        b.savedFlightNum.setText("Flight " + selected.getFlightNum());
        b.savedDes.setText("Destination airport = " + selected.getDestination());
        b.savedGate.setText("Gate = " + selected.getGate());
        b.savedDelay.setText("Delay = " + selected.getDelay());
        b.savedTerminal.setText("Terminal = " + selected.getTerminal());

        b.deleteButton.setOnClickListener(clk -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Would you like to remove this from your saved flights?")
                    .setTitle("Question:")
                    .setPositiveButton("Yes", (dialog, cl) -> {
                        Executor t = Executors.newSingleThreadExecutor();
                        t.execute(() -> {
                            FlightActivity.fDAO.deleteFlight(selected);
                        });
                        //FlightActivity.savedFlights.remove(selected.getId());
                        //SavedFlightsActivity.adapt.notifyItemRemoved((int)selected.getId());
                        Snackbar.make(b.getRoot(), "Flight removed.", Snackbar.LENGTH_LONG)
                                .setTextColor(Color.BLACK)
                                .show();
                    })
                    .setNegativeButton("No", (dialog, cl) -> {})
                    .create().show();


        });

        return b.getRoot();

    }

//    private reset r;
//
//    public interface reset{
//        void resetInterface();
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if(r != null) {
//            r.resetInterface();
//        }
//    }
}
