package algonquin.cst2355.finalprojectandroid.flight;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FlightInfoDAO {

    @Insert
    long insertFlight(FlightInfo f);

    @Query("Select * From FlightInfo")
    List<FlightInfo> getAllFlights();

    @Delete
    void deleteFlight(FlightInfo f);

}
