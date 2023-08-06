package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert
    void insert(QuestionObj question);

    @Update
    void update(QuestionObj question);

    @Delete
    void delete(QuestionObj question);

    @Query("SELECT * FROM questionobj") // By default Room uses the Class name as the table name
    List<QuestionObj> getAllQuestions();
}
