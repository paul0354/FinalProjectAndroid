package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.view.Menu;
import android.view.MenuItem;

import algonquin.cst2355.finalprojectandroid.R;

public class TriviaActivity extends AppCompatActivity {

    RelativeLayout progressing;
    Spinner spinner_category;
    List<CategoryModel> categoryList;
    int catId;

    MaterialButton buttonSearch;
    TextInputLayout inputLayout;
    EditText editNumber;
    SharedPreferences mPref;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        mPref = PreferenceManager.getDefaultSharedPreferences(TriviaActivity.this);

        Toolbar toolbar = findViewById(R.id.triviatool);
        setSupportActionBar(toolbar);

        progressing = findViewById(R.id.progress);
        spinner_category = findViewById(R.id.spincat);
        inputLayout = findViewById(R.id.rl1);
        editNumber = findViewById(R.id.number);
        buttonSearch = findViewById(R.id.search_question);

        categoryList = new ArrayList<>();
        FetchAllCategory();

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner_category.getSelectedItem().equals("Select Category")) {
                } else {
                    category = spinner_category.getSelectedItem().toString().trim();
                    catId = getSelectedCatId(category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyData();
            }
        });
    }

    private void VerifyData() {

        String strNumber = editNumber.getText().toString().trim();

        if (strNumber.isEmpty()) {
            setEditTextError(editNumber, "Please enter a number");
            return;
        }

        int intNumber = Integer.parseInt(strNumber);

        if (spinner_category.getSelectedItem().equals("Select Category")) {
            spinner_category.requestFocus();
            Toast.makeText(this, "Please select a category first", Toast.LENGTH_SHORT).show();
        } else if (intNumber > 50) {
            setEditTextError(editNumber, "The number should be less than or equal to 50");
        } else {
            mPref.edit().putInt(Constant.CATID, catId).putInt(Constant.NUMBER, intNumber).putString(Constant.CATEGORY, category).putString(Constant.FLAG, "Trivia").apply();

            Intent intent = new Intent(TriviaActivity.this, QuestionListActivity.class);
            startActivity(intent);
        }
    }

    private void setEditTextError(EditText editText, String message) {
        editText.requestFocus();
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(message);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            // Display a Toast message with the text "Help"
            Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void FetchAllCategory() {
        progressing.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://opentdb.com/api_category.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {
                        progressing.setVisibility(View.GONE);

                        List<String> labels = new ArrayList<>();
                        labels.add("Select Category");

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                CategoryModel categoryModel = new CategoryModel();

                                categoryModel.setName(jsonObject.getString("name"));
                                categoryModel.setId(jsonObject.getInt("id"));

                                categoryList.add(categoryModel);
                                labels.add(jsonObject.getString("name"));

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
                Toast.makeText(TriviaActivity.this, "Error while fetching data. Please try again later.", Toast.LENGTH_SHORT).show();
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
}
