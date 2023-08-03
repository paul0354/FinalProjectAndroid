package algonquin.cst2355.finalprojectandroid.Trivia;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_quiz_data";
    public static final String QUIZ_RESULT_TABLE = "quiz_result";
    public static final String USER_ANSWER_TABLE = "user_answer";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table "+ QUIZ_RESULT_TABLE +"(id INTEGER PRIMARY KEY,uid varchar, uname varchar,uresult varchar)"
            );

            db.execSQL(
                    "create table "+ USER_ANSWER_TABLE +"(id INTEGER PRIMARY KEY,uid varchar,category varchar, question varchar,youranswer varchar,correctanswer varchar,uresult varchar)"
            );
        } catch (SQLiteException e) {
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+QUIZ_RESULT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+USER_ANSWER_TABLE);
        onCreate(db);
    }

    public boolean insertQuizResult(String uid, String name,String result) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("uname", name);
        contentValues.put("uresult", result);

        db.replace(QUIZ_RESULT_TABLE, null, contentValues);
        return true;
    }

    public boolean insertUserAnswer(String uid, String category,String question,String yourAnswer,String correctAnswer,String result) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("category", category);
        contentValues.put("question", question);
        contentValues.put("youranswer", yourAnswer);
        contentValues.put("correctanswer", correctAnswer);
        contentValues.put("uresult", result);

        db.replace(USER_ANSWER_TABLE, null, contentValues);
        return true;
    }

    @SuppressLint("Range")
    public List<UserResultModel> GetUserResult() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<UserResultModel> userResultModelList = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from "+QUIZ_RESULT_TABLE+" order by uresult DESC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {

            UserResultModel userResultModel = new UserResultModel();
            userResultModel.setId(res.getInt(res.getColumnIndex("id")));
            userResultModel.setUid(res.getString(res.getColumnIndex("uid")));
            userResultModel.setName(res.getString(res.getColumnIndex("uname")));
            userResultModel.setUresult(res.getString(res.getColumnIndex("uresult")));

            userResultModelList.add(userResultModel);

            res.moveToNext();
        }
        return userResultModelList;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
