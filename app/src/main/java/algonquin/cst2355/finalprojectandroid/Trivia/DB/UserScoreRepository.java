package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

/**
 * A repository class abstracting access to multiple data sources, providing a clean API for data access to the rest of the application.
 * It fetches data from the Room database using the DAO.
 */
public class UserScoreRepository {

    private final UserScoreDatabase userScoreDatabase;   // Instance of the database
    private final UserScoreDao userDao;                  // DAO to interact with the database
    private final LiveData<List<UserScore>> userScores;  // LiveData containing the list of user scores

    /**
     * Constructor initializes the database and DAO.
     *
     * @param application the context of the caller, usually an Android component.
     */
    public UserScoreRepository(Application application) {
        // Get an instance of the database
        userScoreDatabase = UserScoreDatabase.getDatabase(application);
        // Initialize the DAO
        userDao = userScoreDatabase.getUserScoreDao();
        // Fetch the leaderboard users
        userScores = userDao.getLeaderboardUsers();
    }

    /**
     * Inserts a UserScore object into the database.
     *
     * @param userScore the user score to be inserted.
     */
    public void insert(UserScore userScore) {
        userDao.save(userScore);
    }

    /**
     * Fetches the list of user scores from the database.
     *
     * @return LiveData containing a list of user scores.
     */
    public LiveData<List<UserScore>> getUserScores() {
        return userScores;
    }
}
