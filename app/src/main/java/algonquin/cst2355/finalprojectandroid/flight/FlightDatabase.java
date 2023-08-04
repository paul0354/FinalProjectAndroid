package algonquin.cst2355.finalprojectandroid.flight;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FlightInfo.class}, version = 1)
public abstract class FlightDatabase extends RoomDatabase {

    public abstract FlightInfoDAO cmDAO();

}
