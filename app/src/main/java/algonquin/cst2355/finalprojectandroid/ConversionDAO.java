package algonquin.cst2355.finalprojectandroid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Update
    void updateConversion(Conversion conversion);
    @Query("SELECT EXISTS(SELECT 1 FROM conversion WHERE currencyFrom = :fromCurrency AND currencyTo = :toCurrency AND conversionAmount = :amount LIMIT 1)")
    boolean conversionExists(String fromCurrency, String toCurrency, double amount);
}
