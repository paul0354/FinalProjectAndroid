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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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

public class BearImagesFragment extends Fragment {
    BearImagesViewModel bearModel;
    ArrayList<BearImage> images;
    BearImagesLayoutBinding binding;
    Bitmap currImage;

    RecyclerView recyclerView;

    RecyclerView.Adapter myAdapter;

    private ImgSaver imgSaver;

    int position;

    boolean hasImageBeenGenerated;

    public BearImagesFragment(boolean hasImageBeenGenerated){
        this.hasImageBeenGenerated = hasImageBeenGenerated;
    }
    public BearImagesFragment(Bitmap b){
        currImage = b;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        binding = BearImagesLayoutBinding.inflate(inflater);
        if(hasImageBeenGenerated){
            File file = new File(getActivity().getFilesDir(), "currBear.png");
            Bitmap currBear = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.currentImage.setImageBitmap(currBear);
        }

        bearModel = new ViewModelProvider(getActivity()).get(BearImagesViewModel.class);
        if(images == null){
            images = bearModel.images.getValue();
            binding.bearImagesList.setAdapter(myAdapter);
        }
        BearImageDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), BearImageDatabase.class,"database-name").build();
        BearImageDAO biDAO = db.biDAO();

        binding.bearImagesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

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
            if(!hasImageBeenGenerated){
                Toast toast = Toast.makeText(this.getContext(),getString(R.string.no_gen_bear_toast),Toast.LENGTH_LONG);
                toast.show();
            }else{
                File file = new File(getActivity().getFilesDir(),"currBear.png");
                if(file.exists()){
                    imgSaver.saveImage(file);
                }
                myAdapter.notifyItemInserted(myAdapter.getItemCount()-1);
                Toast toast = Toast.makeText(this.getContext(),R.string.save_success_toast,Toast.LENGTH_SHORT);
                toast.show();
                binding.bearImagesList.smoothScrollToPosition(myAdapter.getItemCount()-1);
            }
        });

        bearModel.selectedImage.observe(getViewLifecycleOwner(),(newImageValue)-> {
            if(newImageValue != null) {
                BearDetailsFragment bearDetails = new BearDetailsFragment(newImageValue);
                FragmentManager fMgr = getParentFragmentManager();
                FragmentTransaction tx = fMgr.beginTransaction();

                fMgr.setFragmentResultListener("requestKey", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        Boolean result = bundle.getBoolean("bundleKey");
                        if(result){
                            binding.bearImagesList.smoothScrollToPosition(position);
                            BearImage removedImg = images.get(position);
                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> {
                                biDAO.deleteImage(removedImg);

                                getActivity().runOnUiThread( () -> binding.bearImagesList.setAdapter((myAdapter)));
                            });
                            images.remove(position);
                            myAdapter.notifyItemRemoved(position);
                            Snackbar.make(binding.currentText,getString(R.string.del_bear_sb) + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        images.add(position,removedImg);
                                        thread.execute(() -> {
                                            biDAO.insertImage(removedImg);
                                            getActivity().runOnUiThread( () -> binding.bearImagesList.setAdapter(myAdapter));
                                        });
                                        myAdapter.notifyItemInserted(position);
                                        binding.bearImagesList.smoothScrollToPosition(position);
                                    }).show();
                        }
                    }
                });

                tx.addToBackStack("Placeholder");
                tx.add(R.id.imgDetailsLocation,bearDetails);
                tx.commit();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            imgSaver = (ImgSaver) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView height;
        TextView width;
        ImageView bear;
        public MyRowHolder(@NonNull View itemView){
            super(itemView);

            itemView.setOnClickListener(clk -> {
               position = getAbsoluteAdapterPosition();
               BearImage selected = images.get(position);

               bearModel.selectedImage.postValue(selected);
            });
            height = itemView.findViewById(R.id.height);
            width = itemView.findViewById(R.id.width);
            bear = itemView.findViewById(R.id.bearImage);
        }
    }
}
