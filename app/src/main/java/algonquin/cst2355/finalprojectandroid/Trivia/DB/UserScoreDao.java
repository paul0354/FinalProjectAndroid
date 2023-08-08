package algonquin.cst2355.finalprojectandroid.Trivia.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for the UserScore entity.
 * It provides methods to perform database operations on the UserScore table.
 */
@Dao
public interface UserScoreDao {

    /**
     * Retrieves all users' scores from the database.
     *
     * @return A list of all UserScore objects in the database.
     */
    @Query("SELECT * FROM user_score")
    List<UserScore> getAllUsers();

    // This method can be used to fetch users based on their scores in descending order
    // @Query("SELECT * FROM user_score order by score DESC")
    // List<UserScore> getLeaderboardUsers();

    /**
     * Retrieves all users' scores from the database, sorted by their names in ascending order.
     *
     * @return LiveData containing a list of UserScore objects sorted by name.
     */
    @Query("SELECT * from user_score ORDER By name Asc")
    LiveData<List<UserScore>> getLeaderboardUsers();

    /**
     * Inserts a new UserScore record into the database.
     *
     * @param userScore The UserScore object to be saved.
     */
    @Insert
    void save(UserScore userScore);

    /**
     * Deletes a specific UserScore record from the database.
     *
     * @param userScore The UserScore object to be deleted.
     */
    @Delete
    void delete(UserScore userScore);
}
