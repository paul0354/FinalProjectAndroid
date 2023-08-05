package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/*
    Main activity for the trivia functionality of your app
 */
public class TriviaActivity extends AppCompatActivity {

   /*
   Declaring variables with UI elements
    */
    RelativeLayout progressing;
    Spinner spinner_category;
    List<CategoryModel> categoryList;
    int catId;
    String[] title;

    MaterialButton buttonSearch; // Button to start the search
    TextInputLayout inputLayout; // Layout for the input field
    EditText editNumber; // Number of questions
    SharedPreferences mPref; // To store data across app restart
    String category; // Selected category name



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        // Initialize shared preferences
        mPref = PreferenceManager.getDefaultSharedPreferences(TriviaActivity.this);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.triviatool);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setSupportActionBar(toolbar);


        // Link UI elements with their corresponding views
        progressing = (RelativeLayout) findViewById(R.id.progress);
        spinner_category = (Spinner) findViewById(R.id.spincat);
        inputLayout = (TextInputLayout) findViewById(R.id.rl1);
        editNumber = (EditText) findViewById(R.id.number);
        buttonSearch = (MaterialButton) findViewById(R.id.search_question);


        // Initialize category list
        categoryList = new ArrayList<>();

        FetchAllCategory();


        // Set a listener for the spinner to update the selected category when it changes
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner_category.getSelectedItem() == "Select Category") {

                } else {
                    category = spinner_category.getSelectedItem().toString().trim();
                    catId = getSelectedCatId(category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //  Set a click listener for the search button to validate input when clicked
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyData();
            }
        });
    }


    // Method to validate user input before search
    private void VerifyData(){

        String strNumber = editNumber.getText().toString().trim();
        int intNumber = Integer.parseInt(strNumber);

        if(spinner_category.getSelectedItem() == "Select Category"){
            spinner_category.requestFocus();
            Toast.makeText(this, "Please select category first", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(strNumber)){
            editNumber.requestFocus();
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Please enter number is max to 50");
            inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setErrorEnabled(false);
                    inputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else if(intNumber > 50){
            editNumber.requestFocus();
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Please enter number is max to 50");
            inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setErrorEnabled(false);
                    inputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            mPref.edit().putInt(Constant.CATID,catId).apply();
            mPref.edit().putInt(Constant.NUMBER,intNumber).apply();
            mPref.edit().putString(Constant.CATEGORY,category).apply();
            mPref.edit().putString(Constant.FLAG,"Trivia").apply();

            Intent intent = new Intent(TriviaActivity.this,QuestionListActivity.class);
            startActivity(intent);
        }
    }

    private void FetchAllCategory(){
        //fetch all category of trivia questions from the API
        progressing.setVisibility(View.VISIBLE); // Show the loading spinner or progress bar


        // create a gson object that can be used to convert objects to their JSON representation
        Gson gson = new GsonBuilder()
                .setLenient() // Configuration option to enable more relaxed parsing rules.
                .create();

        // Create a new Retrofit instance. Retrofit is a type-safe  HTTP client for android and JAVA.
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(RetrofitInterface.BASE_URL) // Set the API base URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Add converter factory for serialization and deserialization of objects
                .build(); // Build the retrofit instance.

        // Create an implementation of hte API endpoints defined by the RetrofitInterface interface.
        RetrofitInterface apiService=retrofit.create(RetrofitInterface.class);

        Call<CategoryModel> call = apiService.FETCHALLCATEGORY();
        call.enqueue(new Callback<CategoryModel>() {
            @Override
            public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                if(response.body().getTriviaCategories() != null){
                    progressing.setVisibility(View.GONE);

                    title = new String[response.body().getTriviaCategories().size()];
                    List<String> lables = new ArrayList<String>();

                    categoryList = response.body().getTriviaCategories();

                    lables.add("Select Category");
                    for (int i = 0; i < categoryList.size(); i++) {
                        title[i] = categoryList.get(i).getName();
                        lables.add(categoryList.get(i).getName());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(TriviaActivity.this, android.R.layout.simple_spinner_item, lables);
                    // Drop down layout style - list view with radio button
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    spinner_category.setAdapter(spinnerAdapter);

                } else {
                    progressing.setVisibility(View.GONE);
                    Toast.makeText(TriviaActivity.this,"Failure while fetching data !! try again later.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryModel> call, Throwable t) {
                progressing.setVisibility(View.GONE);
                Toast.makeText(TriviaActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getSelectedCatId(String Category) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (Category.equals(categoryList.get(i).getName())) {
                return categoryList.get(i).getId();
            }
        }
        return -1;
    }
}