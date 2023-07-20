package algonquin.cst2355.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2355.finalprojectandroid.databinding.ActivityBearBinding;

public class BearActivity extends AppCompatActivity {

    RequestQueue queue = null;
    ActivityBearBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);

        binding.makeImageBtn.setOnClickListener(clk -> {
            if (generateImage(binding.heightInput.getText().toString(), binding.widthInput.getText().toString())){
                runOnUiThread( () -> {
                    File file = new File(getFilesDir(),"currBear.png");
                    if(file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
                        binding.bearImage.setImageBitmap(image);
                    }
                });
            }
        });

        binding.saveImageBtn.setOnClickListener(clk -> {

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
}