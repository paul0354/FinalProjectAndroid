package algonquin.cst2355.finalprojectandroid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BearImageDAO {
    @Insert
    long insertImage(BearImage b);

    @Query("Select * from BearImage b")
    List<BearImage> getAllImages();

    @Delete
    void deleteImage(BearImage b);
}
