package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import algonquin.cst2355.finalprojectandroid.R;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences mPref;
    int catId,amount;
    String Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Assuming you have this layout in your project

        mPref = this.getSharedPreferences("mpref", MODE_PRIVATE); // Changed this line

        Category = mPref.getString(Constant.CATEGORY,"");
        catId = mPref.getInt(Constant.CATID,0);
        amount = mPref.getInt(Constant.NUMBER,0);

        Thread timer=new Thread(){

            @Override
            public void run() {
                try {
                    sleep(2000);
                    //check pref have data or not
                    if(TextUtils.isEmpty(Category)) {
                        //redirecting screens
                        Intent intent = new Intent(SplashActivity.this,TriviaActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mPref.edit().putString(Constant.FLAG,"SPLASH").apply();
                        Intent intent = new Intent(SplashActivity.this,QuestionListActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    super.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}
