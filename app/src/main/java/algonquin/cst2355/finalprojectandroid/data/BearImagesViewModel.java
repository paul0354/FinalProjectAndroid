package algonquin.cst2355.finalprojectandroid.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.BearImage;

/**
 * A class representing the live and mutable viewModel of the bear images list.
 */
public class BearImagesViewModel extends ViewModel {
    /**
     * Creates a mutable list of bear images.
     */
    public MutableLiveData<ArrayList<BearImage>> images = new MutableLiveData<>();

    /**
     * Creates a mutable bear image.
     */
    public MutableLiveData<BearImage> selectedImage = new MutableLiveData<>();

}
