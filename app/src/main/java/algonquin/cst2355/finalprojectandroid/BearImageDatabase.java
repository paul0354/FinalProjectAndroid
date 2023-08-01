package algonquin.cst2355.finalprojectandroid;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BearImage.class}, version=1)
public abstract class BearImageDatabase extends RoomDatabase {
    public abstract BearImageDAO biDAO();
}
