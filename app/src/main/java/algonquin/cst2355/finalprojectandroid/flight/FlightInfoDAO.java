package algonquin.cst2355.finalprojectandroid.flight;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * DAO interface for the flight tracker
 *
 * @author Owen Austin
 * @version 1.0
 */
@Dao
public interface FlightInfoDAO {

    /**
     * Inserts a flight into the database
     * @param f the info of the flight
     * @return the id in the database
     */
    @Insert
    long insertFlight(FlightInfo f);

    /**
     * Gets all flights in the database
     * @return the list of all flights saved
     */
    @Query("Select * From FlightInfo")
    List<FlightInfo> getAllFlights();

    /**
     * Deletes a flight from the database
     * @param f the flight info to be deleted
     */
    @Delete
    void deleteFlight(FlightInfo f);

}
