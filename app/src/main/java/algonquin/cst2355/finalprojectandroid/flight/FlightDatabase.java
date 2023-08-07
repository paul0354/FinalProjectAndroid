package algonquin.cst2355.finalprojectandroid.flight;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Abstract class defining the database for the flight tracker
 *
 * @author Owen Austin
 * @version 1.0
 */
@Database(entities = {FlightInfo.class}, version = 1)
public abstract class FlightDatabase extends RoomDatabase {

    /**
     * Gets the DAO for the flight tracker
     * @return DAO for the flight database
     */
    public abstract FlightInfoDAO cmDAO();

}
