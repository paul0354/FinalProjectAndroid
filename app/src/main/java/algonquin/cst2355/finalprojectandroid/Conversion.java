package algonquin.cst2355.finalprojectandroid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    public int id;
   @ColumnInfo(name="conversionResult")
    public String conversionResult;
   @ColumnInfo(name="TimeSent")
   public String timeSemt;
   @ColumnInfo(name="convertedCurrencies")
   public String convertedCurrencies;

    public Conversion(String conversionResult, String timeSemt, String currencyConversion) {
        this.conversionResult = conversionResult;
        this.timeSemt = timeSemt;
        this.convertedCurrencies = currencyConversion;
    }
    public Conversion() {
    }


    public String getConversionResult() {
        return conversionResult;
    }

    public String getTimeSemt() {
        return timeSemt;
    }

    public String getCurrencyConversion() {
        return convertedCurrencies;
    }
}
