package algonquin.cst2355.finalprojectandroid.Trivia;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;


import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import algonquin.cst2355.finalprojectandroid.R;


public class QuestionListActivity extends AppCompatActivity {

    String flag,category;
    SharedPreferences mPref;
    int amount,catId;
    RecyclerView recyclerQuestions;
    RelativeLayout progressing;
    MaterialButton buttonResult;
    QuestionListAdapter questionListAdapter;
    DatabaseHelper databaseHelper;
    AlertDialog dialog,dialog1;

    List<UserResultModel> userResultModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        databaseHelper = new DatabaseHelper(QuestionListActivity.this);

        mPref = this.getSharedPreferences("myPreferences", MODE_PRIVATE);

        flag = mPref.getString(Constant.FLAG,"");
        category = mPref.getString(Constant.CATEGORY,"");
        //catId = mPref.getInt(Constant.CATID,0);
        //amount = mPref.getInt(Constant.NUMBER,0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.questiontool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        progressing = (RelativeLayout) findViewById(R.id.progress);
        recyclerQuestions = (RecyclerView) findViewById(R.id.question_data);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(QuestionListActivity.this));
        recyclerQuestions.setNestedScrollingEnabled(false);

        buttonResult = (MaterialButton) findViewById(R.id.submit_result);

        FetchAllQuestions();

        buttonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyResult();
            }
        });
    }


    private void VerifyResult(){

        //check for result data
        List<AnswerModel> answerModels = questionListAdapter.getArrayList();

        if (amount == answerModels.size()) {
            OpenNamePopup(answerModels);
        } else {
            Toast.makeText(this, "Please answer all question", Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenNamePopup(List<AnswerModel> answerModels){

        //dialouge for entering name

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionListActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialogue_name, null);
        builder.setView(view1);

        final ImageView imgclose = (ImageView) view1.findViewById(R.id.close);
        TextInputLayout inputLayout = (TextInputLayout) view1.findViewById(R.id.rl1);
        EditText editName = (EditText) view1.findViewById(R.id.name);
        MaterialButton materialButton = (MaterialButton) view1.findViewById(R.id.submit_details);

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strname = editName.getText().toString().trim();

                if (TextUtils.isEmpty(strname)){
                    editName.requestFocus();
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError("Please enter your name");
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
                    long id = System.currentTimeMillis();
                    String myID = String.valueOf(id);
                    //this is for countng total marks
                    int totalMarks = 0;

                    if (answerModels != null && !answerModels.isEmpty()) {
                        for (int i = 0; i < answerModels.size(); i++) {

                            if(answerModels.get(i).isCorrect()){
                                totalMarks = totalMarks + 1;
                            }
                        }
                    }
                    //function of inserting quiz result
                    boolean statusResult = databaseHelper.insertQuizResult(myID,strname, String.valueOf(totalMarks));
                    if (statusResult) {
                        showResultDialouge(answerModels,myID,totalMarks);
                    } else {
                        databaseHelper.closeDB();
                        Toast.makeText(QuestionListActivity.this, "Result not added!!! try again", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void showResultDialouge(List<AnswerModel> answerModels,String myID,int totalMarks){

        //this dialouge for showing result

        FetchAllUserResult();

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionListActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialogue_user_result, null);
        builder.setView(view1);

        final ImageView imgclose = (ImageView) view1.findViewById(R.id.close);

        RecyclerView recycler_result = (RecyclerView) view1.findViewById(R.id.recycle_result);
        recycler_result.setLayoutManager(new LinearLayoutManager(QuestionListActivity.this));

        //set adapter for quiz result
        QuizResultAdapter quizResultAdapter = new QuizResultAdapter(QuestionListActivity.this,userResultModelList);
        recycler_result.setAdapter(quizResultAdapter);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        if (answerModels != null && !answerModels.isEmpty()) {
            for (int i = 0; i < answerModels.size(); i++) {

                String correctAnswer = answerModels.get(i).getCorrectAnswer();
                String yourAnswer = answerModels.get(i).getYourAnswer();
                String question = answerModels.get(i).getQuestion();

                //function of inserting quiz result by question
                boolean statusAnswer = databaseHelper.insertUserAnswer(myID,category,question,yourAnswer,correctAnswer,String.valueOf(totalMarks));
                if (statusAnswer){
                    databaseHelper.closeDB();
                } else {
                    databaseHelper.closeDB();
                    Toast.makeText(QuestionListActivity.this, "Result not added!!! try again", Toast.LENGTH_SHORT).show();
                }
            }
        }


        dialog1 = builder.create();
        dialog1.show();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
    }

    private void FetchAllUserResult(){

        //fetching user result
        List<UserResultModel> userResultModels = databaseHelper.GetUserResult();
        if (userResultModels!=null && !userResultModels.isEmpty()){
            userResultModelList.addAll(userResultModels);
        }
        databaseHelper.closeDB();
    }

    private void FetchAllQuestions(){

        String url = Constant.API_URL;

        //that function is using for fetching questions
        progressing.setVisibility(View.VISIBLE);

        // Create a new RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a new JsonArrayRequest
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressing.setVisibility(View.GONE);

                        List<QuestionModel> questionModelList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject questionObject = response.getJSONObject(i);

                                QuestionModel questionModel = new QuestionModel();

                                questionModel.setCategory(questionObject.getString("category"));
                                questionModel.setType(questionObject.getString("type"));
                                questionModel.setDifficulty(questionObject.getString("difficulty"));
                                questionModel.setQuestion(questionObject.getString("question"));
                                questionModel.setCorrectAnswer(questionObject.getString("correct_answer"));

                                JSONArray incorrectAnswersArray = questionObject.getJSONArray("incorrect_answers");
                                List<String> incorrectAnswers = new ArrayList<>();
                                for (int j = 0; j < incorrectAnswersArray.length(); j++) {
                                    incorrectAnswers.add(incorrectAnswersArray.getString(j));
                                }
                                questionModel.setIncorrectAnswers(incorrectAnswers);

                                questionModelList.add(questionModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //set adapter for questions
                        questionListAdapter = new QuestionListAdapter(QuestionListActivity.this, questionModelList);
                        recyclerQuestions.setAdapter(questionListAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
        public void onErrorResponse(VolleyError error) {
            progressing.setVisibility(View.GONE);
            Toast.makeText(QuestionListActivity.this, "Failure while fetching data !! try again later.", Toast.LENGTH_SHORT).show();
        }
    }
    );

    // Add JsonArrayRequest to the RequestQueue
    requestQueue.add(jsonArrayRequest);
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPref.edit().putString(Constant.FLAG,"").apply();
        if (flag.equals("SPLASH")){
            Intent intent = new Intent(QuestionListActivity.this,TriviaActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        } else if (dialog1 != null && dialog1.isShowing()){
            dialog1.dismiss();
        }
    }
}