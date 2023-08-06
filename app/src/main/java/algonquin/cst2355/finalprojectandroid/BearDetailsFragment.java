package algonquin.cst2355.finalprojectandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;

import algonquin.cst2355.finalprojectandroid.databinding.BearDetailsLayoutBinding;

public class BearDetailsFragment extends Fragment {

    BearImage selected;
    public BearDetailsFragment(BearImage b) {
        selected = b;
    }

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
        String byteMsg = getString(R.string.size_in_bytes) + selected.getByteSize() + ".";
        binding.byteSize.setText(byteMsg);
        binding.deleteBtn.setOnClickListener(clk -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setMessage(getString(R.string.del_bear_alert) + selected.getImageName())
                    .setTitle("!!!")
                    .setNegativeButton(getString(R.string.no), (dialog, cl) -> {})
                    .setPositiveButton(getString(R.string.yes),(dialog, cl) -> {
                        Bundle result = new Bundle();
                        result.putBoolean("bundleKey",true);
                        getParentFragmentManager().setFragmentResult("requestKey",result);
                        getParentFragmentManager().popBackStackImmediate();
                    })
                    .create().show();
        });
        return binding.getRoot();
    }
}
