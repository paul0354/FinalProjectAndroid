package algonquin.cst2355.finalprojectandroid.data;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.Conversion;


public class CurrencyActivityViewModel extends ViewModel {
        public MutableLiveData<ArrayList<Conversion>> conversions = new MutableLiveData<>();
        public MutableLiveData<Conversion> selectedConversion = new MutableLiveData< >();

}
