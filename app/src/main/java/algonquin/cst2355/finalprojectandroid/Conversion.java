package algonquin.cst2355.finalprojectandroid;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversion implements Parcelable {

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


    protected Conversion(Parcel in) {
        id = in.readInt();
        conversionResult = in.readString();
        timeSemt = in.readString();
        convertedCurrencies = in.readString();
    }

    public static final Creator<Conversion> CREATOR = new Creator<Conversion>() {
        @Override
        public Conversion createFromParcel(Parcel in) {
            return new Conversion(in);
        }

        @Override
        public Conversion[] newArray(int size) {
            return new Conversion[size];
        }
    };

    public String getConversionResult() {
        return conversionResult;
    }

    public String getTimeSemt() {
        return timeSemt;
    }

    public String getCurrencyConversion() {
        return convertedCurrencies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(conversionResult);
        dest.writeString(timeSemt);
    }
}
