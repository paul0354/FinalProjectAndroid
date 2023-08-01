package algonquin.cst2355.finalprojectandroid.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.BearImage;

public class BearImagesViewModel extends ViewModel {
    public MutableLiveData<ArrayList<BearImage>> images = new MutableLiveData<>();

    public MutableLiveData<BearImage> selectedImage = new MutableLiveData<>();
}
