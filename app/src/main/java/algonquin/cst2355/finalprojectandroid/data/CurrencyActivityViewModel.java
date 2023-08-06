package algonquin.cst2355.finalprojectandroid.data;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.Conversion;

/**
 * The CurrencyActivityViewModel class is a ViewModel that holds data related to the CurrencyActivity.
 * It provides LiveData objects to observe and update the data in the CurrencyActivity.
 */
public class CurrencyActivityViewModel extends ViewModel {
        /**
         * LiveData object that holds an ArrayList of Conversion objects.
         * This ArrayList represents the list of currency conversions.
         */
        public MutableLiveData<ArrayList<Conversion>> conversions = new MutableLiveData<>();
        /**
         * LiveData object that holds a single Conversion object.
         * This Conversion object represents the selected conversion.
         */
        public MutableLiveData<Conversion> selectedConversion = new MutableLiveData< >();
        /**
         * Constructs a new CurrencyActivityViewModel object.
         */
        public CurrencyActivityViewModel() {
                // Empty constructor
        }

}
