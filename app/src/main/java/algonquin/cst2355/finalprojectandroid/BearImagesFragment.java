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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import algonquin.cst2355.finalprojectandroid.data.BearImagesViewModel;
import algonquin.cst2355.finalprojectandroid.data.ImgSaver;
import algonquin.cst2355.finalprojectandroid.databinding.BearImageRowBinding;
import algonquin.cst2355.finalprojectandroid.databinding.BearImagesLayoutBinding;

public class BearImagesFragment extends Fragment {
    BearImagesViewModel bearModel;
    ArrayList<BearImage> images;
    BearImagesLayoutBinding binding;
    Bitmap currImage;

    RecyclerView.Adapter myAdapter;

    private ImgSaver imgSaver;

    public BearImagesFragment(Bitmap b){
        currImage = b;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        binding = BearImagesLayoutBinding.inflate(inflater);
        binding.currentImage.setImageBitmap(currImage);
        bearModel = new ViewModelProvider(getActivity()).get(BearImagesViewModel.class);
        if(images == null){
            images = bearModel.images.getValue();
            binding.bearImagesList.setAdapter(myAdapter);
        }

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
            File file = new File(getActivity().getFilesDir(),"currBear.png");
            if(file.exists()){
                imgSaver.saveImage(file);
            }
            myAdapter.notifyItemInserted(myAdapter.getItemCount()-1);
            binding.bearImagesList.smoothScrollToPosition(myAdapter.getItemCount()-1);
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
            height = itemView.findViewById(R.id.height);
            width = itemView.findViewById(R.id.width);
            bear = itemView.findViewById(R.id.bearImage);
        }
    }
}
