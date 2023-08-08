package algonquin.cst2355.finalprojectandroid;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.data.BearImagesViewModel;
import algonquin.cst2355.finalprojectandroid.data.ImgSaver;
import algonquin.cst2355.finalprojectandroid.databinding.BearImageRowBinding;
import algonquin.cst2355.finalprojectandroid.databinding.BearImagesLayoutBinding;

/**
 * Represents a list of all bear images in a database, with a GUI for saving the currently generated
 * image to the database. Updates the list upon user actions (adding/removing items from list).
 * @author Julia Paulson
 * @version 1.0
 */
public class BearImagesFragment extends Fragment {
    /**
     * The view model for bear images.
     */
    BearImagesViewModel bearModel;
    /**
     * A list of all bear images.
     */
    ArrayList<BearImage> images;
    /**
     * The view binding for the bear images fragment.
     */
    BearImagesLayoutBinding binding;

    /**
     * The adapter for the recycler view holding the bear images.
     */
    RecyclerView.Adapter myAdapter;

    /**
     * An image saver item, used to save images to the bear image list.
     */
    private ImgSaver imgSaver;

    /**
     * The currently selected position of the bear image list.
     */
    private int position;

    /**
     * Whether or not a bear image has been generated during this launch of the Bear Image Generator.
     */
    boolean hasImageBeenGenerated;

    boolean hasImageBeenSelected;

    BearImageDAO biDAO;

    FragmentManager fMgr;


    /**
     * Creates a new BearImages fragment and establishes whether a bear image has been generated
     * during this launch of the Bear Image Generator.
     * @param hasImageBeenGenerated Whether an image has been generated during this launch of app
     */
    public BearImagesFragment(boolean hasImageBeenGenerated){
        this.hasImageBeenGenerated = hasImageBeenGenerated;
    }

    /**
     * Creates the view of the bear images lists. Checks whether a bear image has been generated
     * during this launch of the Bear Image Generator, and if so, displays the most recently
     * generated bear image to the user in the top panel of the GUI.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View the view of the bear images fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        binding = BearImagesLayoutBinding.inflate(inflater);
        if(hasImageBeenGenerated){
            File file = new File(getActivity().getFilesDir(), "currBear.png");
            Bitmap currBear = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.currentImage.setImageBitmap(currBear);
        }
        hasImageBeenSelected = false;
        bearModel = new ViewModelProvider(getActivity()).get(BearImagesViewModel.class);
        if(images == null){
            images = bearModel.images.getValue();
            binding.bearImagesList.setAdapter(myAdapter);
        }
        BearImageDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), BearImageDatabase.class,"database-name").build();
        biDAO = db.biDAO();

        binding.bearImagesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        fMgr = getParentFragmentManager();

        binding.bearImagesList.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                BearImageRowBinding binding = BearImageRowBinding.inflate(getLayoutInflater());
                return new MyRowHolder(binding.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {

                holder.height.setText("");
                holder.width.setText("");
                BearImage obj = images.get(position);

                File file = new File(getActivity().getFilesDir(), obj.getImageName());
                if(file.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.bear.setImageBitmap(bmp);
                }
                holder.height.setText(String.valueOf(obj.getHeight()));
                holder.width.setText(String.valueOf(obj.getWidth()));
            }

            @Override
            public int getItemCount() {
                return images.size();
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });

        binding.saveBtn.setOnClickListener(clk -> {
            File file = new File(getActivity().getFilesDir(),"currBear.png");
            if(file.exists()){
                if(imgSaver.saveImage(file)){
                    myAdapter.notifyItemInserted(myAdapter.getItemCount()-1);
                    binding.bearImagesList.smoothScrollToPosition(myAdapter.getItemCount()-1);
                }
            }
        });

        bearModel.selectedImage.observe(getViewLifecycleOwner(), (newImageValue) -> {
            if (newImageValue != null && hasImageBeenSelected) {  //hasImageBeenSelected used to prevent childFragment launching on replacement of ViewAll fragment
                BearDetailsFragment bearDetails = new BearDetailsFragment(newImageValue);
                fMgr.setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
                    Boolean result = bundle.getBoolean("bundleKey");
                    if (result) {
                        binding.bearImagesList.smoothScrollToPosition(position);
                        BearImage removedImg = images.get(position);
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            biDAO.deleteImage(removedImg);

                            getActivity().runOnUiThread(() -> binding.bearImagesList.setAdapter((myAdapter)));
                        });
                        images.remove(position);
                        myAdapter.notifyItemRemoved(position);
                        Snackbar.make(binding.currentText, getString(R.string.del_bear_sb) + position, Snackbar.LENGTH_LONG)
                                .setAction("Undo", clk -> {
                                    images.add(position, removedImg);
                                    thread.execute(() -> {
                                        biDAO.insertImage(removedImg);
                                        getActivity().runOnUiThread(() -> binding.bearImagesList.setAdapter(myAdapter));
                                    });
                                    myAdapter.notifyItemInserted(position);
                                    binding.bearImagesList.smoothScrollToPosition(position);
                                }).show();
                    }
                });
                FragmentTransaction tx = fMgr.beginTransaction();
                tx.addToBackStack("Placeholder");
                tx.replace(R.id.imgDetailsLocation, bearDetails);
                tx.commit();
            }
        });
        return binding.getRoot();
    }

    /**
     * Allows use of Image Saver methods defined in parent activity.
     * @param context The context of the fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            imgSaver = (ImgSaver) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    /**
     * Row holder for the recyclerView used to display the list of bear images.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView height;
        TextView width;
        ImageView bear;

        /**
         * Creates a new row holder, sets the display values for height, width,
         * and bear image, and establishes the relationship between user click
         * and selection of a bear image object.
         * @param itemView
         */
        public MyRowHolder(@NonNull View itemView){
            super(itemView);

            itemView.setOnClickListener(clk -> {
               position = getAbsoluteAdapterPosition();
               BearImage selected = images.get(position);
               bearModel.selectedImage.postValue(selected);
               hasImageBeenSelected=true;
            });
            height = itemView.findViewById(R.id.height);
            width = itemView.findViewById(R.id.width);
            bear = itemView.findViewById(R.id.bearImage);
        }
    }
}
