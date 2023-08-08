package algonquin.cst2355.finalprojectandroid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * An interface for accessing and manipulating the bear image database.
 * @author Julia Paulson
 * @version 1.0
 */
@Dao
public interface BearImageDAO {
    /**
     * Adds a bear image to the database.
     * @param b A bear image.
     * @return long
     */
    @Insert
    long insertImage(BearImage b);

    /**
     * Retrieves a list of all bear images stored in the bear image database.
     * @return A list of all bear images stored in the database.
     */
    @Query("Select * from BearImage b")
    List<BearImage> getAllImages();

    /**
     * Deletes a bear image from the database.
     * @param b A bear image.
     */
    @Delete
    void deleteImage(BearImage b);
}
