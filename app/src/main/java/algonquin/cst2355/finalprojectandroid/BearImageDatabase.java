package algonquin.cst2355.finalprojectandroid;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Represents a bear images database.
 * @author Julia Paulson
 * @version 1.0
 */
@Database(entities = {BearImage.class}, version=2)
public abstract class BearImageDatabase extends RoomDatabase {
    /**
     * Creates a bear image database access object.
     * @return BearImagesDAO A bear images database access object.
     */
    public abstract BearImageDAO biDAO();
}
