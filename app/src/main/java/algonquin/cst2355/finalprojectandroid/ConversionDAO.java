package algonquin.cst2355.finalprojectandroid;
/**
 * Name : Chawki Moulayat
 * Section : 23S_CST2335_022
 * Project : Final Project
 * Function : Currency Converter
 *
 * @Author : Chawki Moulayat
 */
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
/**
 * The ConversionDAO interface defines the Data Access Object (DAO) for the Conversion class.
 * It provides methods for performing CRUD (Create, Read, Update, Delete) operations on the Conversion objects
 * in the database.
 */
@Dao
public interface ConversionDAO {
    /**
     * Inserts a new Conversion object into the database.
     *
     * @param C The Conversion object to be inserted.
     * @return The row ID of the inserted Conversion.
     */
    @Insert
    long insertConversion (Conversion C);
    /**
     * Retrieves all Conversion objects from the database.
     *
     * @return A List of Conversion objects containing all the conversions in the database.
     */
    @Query("Select * from conversion")
    List<Conversion> getAllConversions();
    /**
     * Deletes a specific Conversion object from the database.
     *
     * @param c The Conversion object to be deleted.
     */
    @Delete
    void deleteConversion (Conversion c);
    /**
     * Deletes all Conversion objects from the database.
     */
    @Query("DELETE FROM conversion")
    void deleteAllConversions();
    /**
     * Updates a specific Conversion object in the database.
     *
     * @param conversion The Conversion object to be updated.
     */
    @Update
    void updateConversion(Conversion conversion);
    /**
     * Checks if a specific Conversion object exists in the database based on its currencyFrom, currencyTo, and conversionAmount.
     *
     * @param fromCurrency The source currency of the conversion.
     * @param toCurrency   The target currency of the conversion.
     * @param amount       The amount to be converted.
     * @return true if the Conversion object exists in the database, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM conversion WHERE currencyFrom = :fromCurrency AND currencyTo = :toCurrency AND conversionAmount = :amount LIMIT 1)")
    boolean conversionExists(String fromCurrency, String toCurrency, double amount);
}
