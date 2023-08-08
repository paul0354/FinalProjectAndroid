package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserScore.class}, version = 1)
public abstract class UserScoreDatabase extends RoomDatabase {
    public abstract UserScoreDao getUserScoreDao();

    private static volatile UserScoreDatabase userScoreDatabase;

    public static UserScoreDatabase getDatabase(final Context context) {
        if (userScoreDatabase == null) {
            synchronized (UserScoreDatabase.class) {
                if (userScoreDatabase == null) {
                    userScoreDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    UserScoreDatabase.class, "user_score_database")
                            .build();
                }
            }
        }
        return userScoreDatabase;
    }

}
