package algonquin.cst2355.finalprojectandroid;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import algonquin.cst2355.finalprojectandroid.databinding.FragmentItemDetailBinding;
import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 * A fragment that displays the details of a conversion and provides options to save, delete, or dismiss the fragment.
 */
public class ConversionDetailsFragment extends Fragment {
    /**
     * Represents the selected conversion that is currently being displayed in the ConversionDetailsFragment.
     * It holds the Conversion object for which the details are being shown.
     */
    private Conversion selectedConversion;
    /**
     * A listener interface to be implemented by the parent class
     * This allows the parent class to handle the deletion event and perform appropriate actions.
     */
    private OnConversionDeletedListener deletionListener;
    /**
     * Default constructor for the ConversionDetailsFragment.
     */
    public ConversionDetailsFragment() {
    }
    /**
     * Static method to create a new instance of the ConversionDetailsFragment with the specified Conversion object.
     *
     * @param conversion The Conversion object to be displayed in the fragment.
     * @return A new instance of the ConversionDetailsFragment with the given Conversion object.
     */
    public static ConversionDetailsFragment newInstance(Conversion conversion) {
        ConversionDetailsFragment fragment = new ConversionDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedConversion", conversion);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Interface to handle the extends deletion of a conversion from the fragment to the deletion from the recycler view.
     */
    public interface OnConversionDeletedListener {
        /**
         * Callback method to be invoked when a conversion is deleted.
         *
         * @param deletedConversion The Conversion object that was deleted.
         */
        void onConversionDeleted (Conversion deletedConversion);
    }
    /**
     * Sets the listener for deletion events.
     *
     * @param listener The OnConversionDeletedListener to be set.
     */
    public void setOnConversionDeletedListener(OnConversionDeletedListener listener){
        this.deletionListener = listener;
    }
    /**
     * Called when the fragment's view is created.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentItemDetailBinding binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        selectedConversion = getArguments().getParcelable("selectedConversion");
        if (selectedConversion != null) {
            binding.textViewResultAmount.setText(selectedConversion.getConvertedDetails());
            binding.textViewTimeStamp.setText(selectedConversion.getTimeSemt());
            binding.buttonSave.setOnClickListener(v -> saveConversionToDatabase());
            binding.dismiss.setOnClickListener(v -> dismissFragment());
            binding.buttonDelete.setOnClickListener(v -> deleteConversionFromDatabase());
        }
        return binding.getRoot();
    }
    /**
     * Saves the selected conversion to the database.
     */
    private void saveConversionToDatabase() {
        Conversion newConversion = new Conversion(selectedConversion.conversionAmount, selectedConversion.timeSemt, selectedConversion.convertedDetails, selectedConversion.currencyFrom, selectedConversion.currencyTo);
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(new Runnable() {
            @Override
            public void run() {
                ConversionDAO myDAO = ConversionDatabase.getInstance(requireContext()).cDAO();
                List<Conversion> allConversions = myDAO.getAllConversions();
                for (Conversion conversion : allConversions) {
                    if (conversion.getConversionAmount().equals(newConversion.getConversionAmount()) &&
                            conversion.getTimeSemt().equals(newConversion.getTimeSemt())) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Conversion is already saved.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
                long insertedId = myDAO.insertConversion(newConversion);
                if (insertedId != -1) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Conversion saved!", Toast.LENGTH_SHORT).show();
                            dismissFragment();
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Failed to save conversion", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    /**
     * Deletes the selected conversion from the database and notifies the listener of the deletion event.
     */
    private void deleteConversionFromDatabase () {
        if (selectedConversion != null) {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                ConversionDAO myDAO = ConversionDatabase.getInstance(requireContext()).cDAO();
                myDAO.deleteConversion(selectedConversion);
                if (deletionListener != null) {
                    deletionListener.onConversionDeleted(selectedConversion);
                }
                if (isAdded()) { // Check if the fragment is attached to the activity
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Conversion deleted!", Toast.LENGTH_SHORT).show();
                        dismissFragment(); // Move the dismissFragment() call to the UI thread
                    });
                }
            });
        }
    }
    /**
     * Dismisses the fragment from the parent FragmentManager.
     */
    private void dismissFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
