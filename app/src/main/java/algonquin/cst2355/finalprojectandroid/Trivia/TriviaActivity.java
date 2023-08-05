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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2355.finalprojectandroid.R;

/*
    Main activity for the trivia functionality of your app, handles user interactions related to category selection and input of number of questions for a trivia game, it fetches
    categories from an API, validates user input, and navigates to the next screen if inputs are valid. If inputs are not valid, it provides feedback for the user to correct them.
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
    private void VerifyData() {

        String strNumber = editNumber.getText().toString().trim();

        if (strNumber.isEmpty()) {
            setEditTextError(editNumber);
            return;
        }

        int intNumber = Integer.parseInt(strNumber);

        if ("Select Category".equals(spinner_category.getSelectedItem())) {
            spinner_category.requestFocus();
            Toast.makeText(this, "Please select category first", Toast.LENGTH_SHORT).show();
        } else if (intNumber > 50) {
            setEditTextError(editNumber);
        } else {
            mPref.edit().putInt(Constant.CATID, catId).putInt(Constant.NUMBER, intNumber).putString(Constant.CATEGORY, category).putString(Constant.FLAG, "Trivia").apply();

            Intent intent = new Intent(TriviaActivity.this, QuestionListActivity.class);
            startActivity(intent);
        }
    }

    private void setEditTextError(EditText editText) {
        editText.requestFocus();
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
    }


    // This method fetches all category of trivia questions from the API
    private void FetchAllCategory() {
        //fetch all category of trivia questions from the API
        progressing.setVisibility(View.VISIBLE); // Show the loading spinner or progress bar

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://opentdb.com/api_category.php";


        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {
                        // Dismiss progress
                        progressing.setVisibility(View.GONE);

                        List<String> labels = new ArrayList<String>();
                        labels.add("Select Category");

                        // Parse your response
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                CategoryModel categoryModel = new CategoryModel();

                                categoryModel.setName(jsonObject.getString("name"));
                                categoryModel.setId(jsonObject.getInt("id"));  // Replace with your JSON key

                                categoryList.add(categoryModel);
                                labels.add(jsonObject.getString("name"));  // Replace with your JSON key

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(TriviaActivity.this, android.R.layout.simple_spinner_item, labels);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_category.setAdapter(spinnerAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressing.setVisibility(View.GONE);
                Toast.makeText(TriviaActivity.this, "Failure while fetching data !! try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

    private int getSelectedCatId(String Category) {
        for (CategoryModel categoryModel : categoryList) {
            if (Category.equals(categoryModel.getName())) {
                return categoryModel.getId();
            }
        }
        return -1;
    }

    private void FetchQuestions(int amount, int category, String type) {
        progressing.setVisibility(View.VISIBLE); // Show the loading spinner or progress bar

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        //construct url
        String url = "https://opentdb.com/api.php?amount=" + amount + "&category=" + category + "&type=" + type;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {

                        progressing.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressing.setVisibility(View.GONE);
                Toast.makeText(TriviaActivity.this, "Failure while fetching data !! try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }
}