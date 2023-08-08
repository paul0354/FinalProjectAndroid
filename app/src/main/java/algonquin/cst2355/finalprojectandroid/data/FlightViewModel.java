package algonquin.cst2355.finalprojectandroid.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.FlightInfo;

/**
 * View model for the flight activity
 *
 * @author Owen Austin
 * @version 1.0
 */
public class FlightViewModel extends ViewModel {

    /** MutableLiveData containing the list of flight info */
    public MutableLiveData<ArrayList<FlightInfo>> listings = new MutableLiveData<>();

    /** MutableLiveData containing a single flight */
    public MutableLiveData<FlightInfo> listing = new MutableLiveData<>();

}
