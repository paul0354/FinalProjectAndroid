package algonquin.cst2355.finalprojectandroid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversionDAO {

    @Insert
    long insertConversion (Conversion C);

    @Query("Select * from conversion")
    List<Conversion> getAllConversions();

    @Delete
    void deleteConversion (Conversion c);

    @Query("DELETE FROM conversion")
    void deleteAllConversions();
}
