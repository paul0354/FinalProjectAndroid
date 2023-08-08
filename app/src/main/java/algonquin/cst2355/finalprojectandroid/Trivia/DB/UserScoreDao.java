package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserScoreDao {

    @Query("SELECT * FROM user_score")
    List<UserScore> getAllUsers();

//    @Query("SELECT * FROM user_score order by score DESC")
//    List<UserScore> getLeaderboardUsers();

    @Query("SELECT * from user_score ORDER By name Asc")
    LiveData<List<UserScore>> getLeaderboardUsers();

    @Insert
    void save(UserScore userScore);

    @Delete
    void delete(UserScore userScore);
}
