package algonquin.cst2355.finalprojectandroid.Trivia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {QuestionObj.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})  // This is required if you are storing List in Room
public abstract class QuestionDatabase extends RoomDatabase {

    public abstract QuestionDao questionDao();

    // Singleton pattern to avoid having multiple instances of the database opened at the same time
    private static QuestionDatabase INSTANCE;

    public static synchronized QuestionDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuestionDatabase.class, "question_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
