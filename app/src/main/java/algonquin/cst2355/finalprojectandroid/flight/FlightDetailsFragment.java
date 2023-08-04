package algonquin.cst2355.finalprojectandroid.flight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import algonquin.cst2355.finalprojectandroid.databinding.FlightDetailsLayoutBinding;

public class FlightDetailsFragment extends Fragment {

    FlightInfo selected;

    public FlightDetailsFragment(FlightInfo f) {selected = f;}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FlightDetailsLayoutBinding b = FlightDetailsLayoutBinding.inflate(inflater);

        b.flightNum.setText(selected.getFlightNum());
        b.des.setText(selected.getDestination());
        b.gate.setText(selected.getGate());
        b.delay.setText(selected.getDelay());
        b.terminal.setText(selected.getTerminal());

        return b.getRoot();

    }

}
