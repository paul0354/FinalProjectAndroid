package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.data.BearImagesViewModel;
import algonquin.cst2355.finalprojectandroid.data.ImgSaver;
import algonquin.cst2355.finalprojectandroid.databinding.ActivityBearBinding;

public class BearActivity extends AppCompatActivity implements ImgSaver {

    RequestQueue queue = null;
    ActivityBearBinding binding;
    ArrayList<BearImage> images = new ArrayList<>();

    BearImagesViewModel bearModel;

    BearImageDAO bDAO;


    int currHeight;
    int currWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.bearToolbar);

        SharedPreferences prefs = getSharedPreferences("MyData",Context.MODE_PRIVATE);
        binding.heightInput.setText(prefs.getString("Height",""));
        binding.widthInput.setText(prefs.getString("Width",""));

        BearImageDatabase db = Room.databaseBuilder(getApplicationContext(),BearImageDatabase.class,"database-name").build();
        bDAO = db.biDAO();

        queue = Volley.newRequestQueue(this);
        bearModel = new ViewModelProvider(this).get(BearImagesViewModel.class);
        images = bearModel.images.getValue();

        if(images == null){
            bearModel.images.postValue(images = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                images.addAll(bDAO.getAllImages());
            });
        }

        binding.makeImageBtn.setOnClickListener(clk -> {
            if (generateImage(binding.heightInput.getText().toString(), binding.widthInput.getText().toString())){

                currHeight = Integer.parseInt(binding.heightInput.getText().toString());
                currWidth = Integer.parseInt(binding.widthInput.getText().toString());
            }
        });

        binding.saveImageBtn.setOnClickListener(clk -> {
            File file = new File(getFilesDir(), "currBear.png");
            if(file.exists()) {
                saveImage(file);
            }
        });

        binding.viewImagesBtn.setOnClickListener(clk -> {
            File file = new File(getFilesDir(),"currBear.png");
            if(file.exists()){
                Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
                BearImagesFragment bearImagesFragment = new BearImagesFragment(image);
                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction tx = fMgr.beginTransaction();

                tx.addToBackStack("null");
                tx.replace(R.id.bearListLocation,bearImagesFragment);
                tx.commit();
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Height",binding.heightInput.getText().toString());
        editor.putString("Width",binding.widthInput.getText().toString());
        editor.apply();
    }
    @Override
    public void saveImage(File f){
        Bitmap image = BitmapFactory.decodeFile(f.getAbsolutePath());
        boolean wasCropped = false;
        String imageName = "bear" + currHeight + "x" + currWidth + ".png";
        if (currHeight > 2048 || currWidth > 2048) {
            wasCropped = true;
        }
        FileOutputStream fOut;
        try {
            fOut = openFileOutput(imageName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG,100,fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BearImage bear = new BearImage(imageName,currWidth,currHeight,wasCropped);
        images.add(bear);
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() ->
        {
            bDAO.insertImage(bear);
        });
    }

    protected boolean generateImage(String height, String width){
        if((!height.isEmpty()) && (!width.isEmpty())
                && Integer.parseInt(height) > 0
                && Integer.parseInt(width) > 0){
            String url = "https://placebear.com/" + width + "/" + height;
            ImageRequest imgReq = new ImageRequest(url, new Response.Listener<Bitmap>() {
                Bitmap bmp = null;
                @Override
                public void onResponse(Bitmap bitmap) {
                    try{
                        bmp = bitmap;
                        bmp.compress(Bitmap.CompressFormat.PNG,100,BearActivity.this.openFileOutput("currBear.png", Activity.MODE_PRIVATE));
                    }catch(Exception e){

                    } finally {
                        runOnUiThread( () -> {
                            File file = new File(getFilesDir(),"currBear.png");
                            if(file.exists()) {
                                Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
                                binding.bearImage.setImageBitmap(image);
                            }
                        });
                    }
                }
            }, 2048, 2048, ImageView.ScaleType.CENTER_CROP, null, (error ) -> {

            });
            queue.add(imgReq);
            return true;
        }else{
            Toast.makeText(this,"You must supply a non-negative height and width",Toast.LENGTH_LONG).show();
            binding.bearImage.setImageResource(R.drawable.bear_icon);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bear_menu, menu);
        return true;
    }

}