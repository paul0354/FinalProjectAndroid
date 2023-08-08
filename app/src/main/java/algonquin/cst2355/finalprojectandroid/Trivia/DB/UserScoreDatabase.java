package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * The Room Database class for storing user scores.
 * It serves as an access point to the underlying SQLite database.
 * The database is only created once and then reused to avoid the overhead of opening a connection every time.
 */
@Database(entities = {UserScore.class}, version = 1)
public abstract class UserScoreDatabase extends RoomDatabase {

    /**
     * Returns an instance of the UserScoreDao to perform CRUD operations.
     *
     * @return UserScoreDao object
     */
    public abstract UserScoreDao getUserScoreDao();

    // Singleton instance of the database
    private static volatile UserScoreDatabase userScoreDatabase;

    /**
     * Gets the singleton instance of the UserScoreDatabase.
     * If no instance exists, a new one is created. Otherwise, the existing instance is returned.
     *
     * @param context the application context
     * @return UserScoreDatabase singleton instance
     */
    public static UserScoreDatabase getDatabase(final Context context) {
        // If no instance of the database exists
        if (userScoreDatabase == null) {
            // Synchronized block to avoid multiple threads accessing this method concurrently
            synchronized (UserScoreDatabase.class) {
                // Check again in case another thread initialized the database before the current thread entered the synchronized block
                if (userScoreDatabase == null) {
                    // Create a new instance of the database
                    userScoreDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    UserScoreDatabase.class, "user_score_database")
                            .build();
                }
            }
        }
        return userScoreDatabase;
    }
}
