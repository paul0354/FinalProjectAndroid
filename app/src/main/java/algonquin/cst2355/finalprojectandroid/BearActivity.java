package algonquin.cst2355.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
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

    FragmentManager fMgr;
    RequestQueue queue = null;
    ActivityBearBinding binding;
    ArrayList<BearImage> images = new ArrayList<>();

    BearImagesViewModel bearModel;

    BearImageDAO bDAO;


    int currHeight;
    int currWidth;

    boolean hasBearBeenGenerated;


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

        binding.makeImageBtn.setOnClickListener(v -> {
            hideKeyboard(v);
            if (generateImage(binding.heightInput.getText().toString(), binding.widthInput.getText().toString())){

                currHeight = Integer.parseInt(binding.heightInput.getText().toString());
                currWidth = Integer.parseInt(binding.widthInput.getText().toString());
                hasBearBeenGenerated = true;
            }
        });

        binding.saveImageBtn.setOnClickListener(clk -> {
            File file = new File(getFilesDir(), "currBear.png");
            if(file.exists()) {
                saveImage(file);
            }
        });

        binding.viewImagesBtn.setOnClickListener(v -> {
            hideKeyboard(v);
            BearImagesFragment bearImagesFragment = new BearImagesFragment(hasBearBeenGenerated);
            fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.addToBackStack("null");
            tx.replace(R.id.bearListLocation,bearImagesFragment);
            tx.commit();
            /*
            File file = new File(getFilesDir(),"currBear.png");
            if(file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
                BearImagesFragment bearImagesFragment = new BearImagesFragment(image);
                fMgr = getSupportFragmentManager();
                FragmentTransaction tx = fMgr.beginTransaction();

                tx.addToBackStack("null");
                tx.replace(R.id.bearListLocation, bearImagesFragment);
                tx.commit();
            }
             */
            /*
                        Bitmap image;
            image = BitmapFactory.decodeResource(getResources(), R.drawable.bear_icon);
            if(hasBearBeenGenerated){
                File file = new File(getFilesDir(), "currBear.png");
                if (file.exists()) {
                    image = BitmapFactory.decodeFile(file.getAbsolutePath());
                }
            }
             */
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
        String imageName = "bear" + currWidth + "x" +  currHeight + ".png";
        FileOutputStream fOut;
        try {
            fOut = openFileOutput(imageName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG,100,fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BearImage bear = new BearImage(imageName,currWidth,currHeight);
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
            int givenHeight = Integer.parseInt(height);
            int givenWidth = Integer.parseInt(width);
            if(givenWidth >= 5000 || givenHeight >=5000){
                Toast.makeText(this,getString(R.string.size_too_big_toast),Toast.LENGTH_LONG).show();
                return false;
            }
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
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, (error ) -> {

            });
            queue.add(imgReq);
            return true;
        }else{
            Toast.makeText(this,getString(R.string.provide_size_toast),Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItem = item.getItemId();
        if(selectedItem == R.id.bMenuCurrency){
            Intent currencyPage = new Intent(BearActivity.this, CurrencyActivity.class);
            startActivity(currencyPage);
            return true;
        }else if(selectedItem == R.id.bMenuTrivia){
            Intent triviaPage = new Intent(BearActivity.this, TriviaActivity.class);
            startActivity(triviaPage);
            return true;
        }else if(selectedItem == R.id.bMenuFlight){
            Intent flightPage = new Intent(BearActivity.this, FlightActivity.class);
            startActivity(flightPage);
            return true;
        }else if(selectedItem == R.id.bMenuHelp){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml(getString(R.string.help_blurb),
                            Html.FROM_HTML_MODE_LEGACY))
                    .setTitle(getString(R.string.about_bear))
                    .create().show();
            /*
            builder.setMessage(Html.fromHtml("If you would like to make a new bear image, please type " +
                            "a width and height in the text fields, then click " +
                            "on <i>Generate Image</i>.<br><b>Height and/or width cannot exceed 5000.</b><br><br>" +
                            "If you would like to save the generated image, please click <i>Save Image</i>.<br><br>" +
                            "If you would like to view all saved images, please click <i>View All Images.</i><br><br>" +
                            "<u>Within View All Images:</u><br>Tap any image to see additional details about it, or to be " +
                            "given the option to delete it from the list.",
                            Html.FROM_HTML_MODE_LEGACY))
                    .setTitle("About The Bear Image Generator")
                    .create().show();

             */
            return true;
        }
        return false;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (fMgr.getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}