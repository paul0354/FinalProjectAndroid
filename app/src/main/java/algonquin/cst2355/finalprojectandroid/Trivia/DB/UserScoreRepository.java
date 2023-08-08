package algonquin.cst2355.finalprojectandroid.Trivia.DB;


import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserScoreRepository {
    UserScoreDatabase userScoreDatabase;
    UserScoreDao userDao;

    private LiveData<List<UserScore>> userScores;

    public UserScoreRepository(Application application) {
        userScoreDatabase = UserScoreDatabase.getDatabase(application);
        userDao = userScoreDatabase.getUserScoreDao();
        userScores = userDao.getLeaderboardUsers();
    }

    public void insert(UserScore userScore) {
        userDao.save(userScore);
    }

    public LiveData<List<UserScore>> getUserScores() {
        return userScores;
    }

}
