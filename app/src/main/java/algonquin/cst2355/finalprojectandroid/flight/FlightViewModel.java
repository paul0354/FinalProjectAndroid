package algonquin.cst2355.finalprojectandroid.flight;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class FlightViewModel extends ViewModel {

    public MutableLiveData<ArrayList<FlightInfo>> listings = new MutableLiveData<>();

    public MutableLiveData<FlightInfo> listing = new MutableLiveData<>();

}
