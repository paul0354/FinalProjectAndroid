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

public class ConversionDetailsFragment extends Fragment {
    private Conversion selectedConversion;
    private OnConversionDeletedListener deletionListener;
    public ConversionDetailsFragment() {
    }

    public ConversionDetailsFragment(Conversion c) {
        selectedConversion = c;
    }
    public static ConversionDetailsFragment newInstance(Conversion conversion) {
        ConversionDetailsFragment fragment = new ConversionDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedConversion", conversion);
        fragment.setArguments(args);
        return fragment;
    }
    public interface OnConversionDeletedListener {
        void onConversionDeleted (Conversion deletedConversion);
    }

    public void setOnConversionDeletedListener(OnConversionDeletedListener listener){
        this.deletionListener = listener;
    }
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
    private void dismissFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
