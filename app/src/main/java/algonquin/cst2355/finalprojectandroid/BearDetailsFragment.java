package algonquin.cst2355.finalprojectandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;

import algonquin.cst2355.finalprojectandroid.databinding.BearDetailsLayoutBinding;

/**
 * A fragment containing additional details about a bear image object, as well
 * as the GUI functionality to delete the bear image from the images list and the
 * database. Alerts the user and prompts for confirmation before any such deletion occurs.
 */
public class BearDetailsFragment extends Fragment {
    /**
     * The selected bear image.
     */
    BearImage selected;

    /**
     * Creates a new bear details fragment using the selected bear image.
     * @param b The selected bear image.
     */
    public BearDetailsFragment(BearImage b) {
        selected = b;
    }

    /**
     * Creates the displayed view of the details for the selected bear image, and provides
     * users with the option to delete the currently displayed bear image.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View the view of the bear detail fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);

        BearDetailsLayoutBinding binding = BearDetailsLayoutBinding.inflate(getLayoutInflater());

        File file = new File(getActivity().getFilesDir(), selected.getImageName());
        if(file.exists()){
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.imageView.setImageBitmap(bmp);
        }
        binding.imgName.setText(selected.getImageName());
        binding.imgWidth.setText(String.valueOf(selected.getWidth()));
        binding.imgHeight.setText(String.valueOf(selected.getHeight()));
        String byteMsg = getString(R.string.size_in_bytes) + " " + selected.getByteSize() + ".";
        binding.byteSize.setText(byteMsg);
        binding.deleteBtn.setOnClickListener(clk -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setMessage(getString(R.string.del_bear_alert) + " " + selected.getImageName())
                    .setTitle("!!!")
                    .setNegativeButton(getString(R.string.no), (dialog, cl) -> {})
                    .setPositiveButton(getString(R.string.yes),(dialog, cl) -> {
                        Bundle result = new Bundle();
                        result.putBoolean("bundleKey",true);
                        FragmentManager fMgr = getParentFragmentManager();
                        fMgr.setFragmentResult("requestKey",result);
                        fMgr.popBackStackImmediate();
                    })
                    .create().show();
        });
        return binding.getRoot();
    }
}
