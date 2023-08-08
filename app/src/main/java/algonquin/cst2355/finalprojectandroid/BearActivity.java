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

/**
 * Program to generate, save, and view bear images in a database. Contains functionality to
 * request a bear image from a server, display this image to the user, save the image to a database,
 * and launch a fragment which allows users to view all images, view additional details about the
 * images, and delete images from the database. Contains a toolbar which can be used to navigate to
 * other app activities, as well as used to display information about how to use this GUI. Contains
 * functionality to store data entered by the user for use future application sessions. Provides
 * users with feedback if they enter inappropriate data.
 */
public class BearActivity extends AppCompatActivity implements ImgSaver {
    /**
     * A fragment manager for Bear Activity, used to launch a new fragment.
     */
    FragmentManager fMgr;
    /**
     * A request queue for the Bear Image Generator. Used to queue requests to the server.
     */
    RequestQueue queue = null;
    /**
     * View binding for the Bear Image Generator's GUI.
     */
    ActivityBearBinding binding;
    /**
     * An array of bear images.
     */
    ArrayList<BearImage> images = new ArrayList<>();

    /**
     * A view model for accessing bear images.
     */
    BearImagesViewModel bearModel;

    /**
     * The database access object for manipulating the bear image database.
     */
    BearImageDAO bDAO;

    /**
     * The height of the currently generated bear image.
     */
    private int currHeight;

    /**
     * The width of the currently generated bear image.
     */
    private int currWidth;

    /**
     * Whether or not a bear image has been generated since opening the app.
     */
    private boolean hasBearBeenGenerated;

    /**
     * Contains functionality to allow user to generate, save, and view all bear images.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.bearToolbar);

        SharedPreferences prefs = getSharedPreferences("MyData",Context.MODE_PRIVATE);
        binding.heightInput.setText(prefs.getString("Height",""));
        binding.widthInput.setText(prefs.getString("Width",""));

        BearImageDatabase db = Room.databaseBuilder(getApplicationContext(),BearImageDatabase.class,"database-name").fallbackToDestructiveMigration().build();
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

        fMgr = getSupportFragmentManager();

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
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.addToBackStack("bearimgs");
            tx.replace(R.id.bearListLocation,bearImagesFragment);
            tx.commit();
        });
    }

    /**
     * Stores the height and width currently entered in the editable text fields on pause.
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Height",binding.heightInput.getText().toString());
        editor.putString("Width",binding.widthInput.getText().toString());
        editor.apply();
    }

    /**
     * Saves the currently generated bear image to the users device and to the image database.
     * Uses a semi-unique name to preserve space on users device (multiple copies of the same image
     * will overwrite each other upon save, rather than be saved alongside each other). Displays a
     * message to the user upon successfully saving the image. Does not permit a user to save an
     * image if they have not yet generated an image.
     * @param f The image file to be saved into the database.
     */
    @Override
    public boolean saveImage(File f){
        if(!hasBearBeenGenerated) {
            Toast toast = Toast.makeText(this, getString(R.string.no_gen_bear_toast), Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
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
        Toast toast = Toast.makeText(this,R.string.save_success_toast,Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    /**
     * Generates an image of a bear using user inputted parameters to request an image from a
     * public server. Does not permit requests for images which are greater than or equal to 5000px
     * tall or wide, as these will crash the application due to limited storage for bitmaps. Prevents
     * server requests and provides feedback to user if supplied height and/or width exceed this
     * limit, are left empty, or are negative numbers. Upon response from the server, stores the
     * requested image as "currBear.png" for further use in application.
     * @param height A height provided by the user.
     * @param width A width provide by the user.
     * @return A boolean result representing whether the image was generated or not.
     */
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
                        e.printStackTrace();
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

    /**
     * Creates the toolbar navigation menu for the Bear Image Generator.
     * @param menu The options menu in which you place your items.
     * @return boolean result for creation of menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bear_menu, menu);
        return true;
    }

    /**
     * Provides functionality for selected menu items.
     * @param item The menu item that was selected.
     * @return a boolean result for the selection of a menu item.
     */
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
            return true;
        }
        return false;
    }

    /**
     * Hides the keyboard from the user. Called when user clicks on Generate or View All buttons.
     * @param view The current view
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Provides support for removing fragments from the back stack.
     */

    @Override
    public void onBackPressed() {
        if (fMgr.getBackStackEntryCount() > 0 ){
            fMgr.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}