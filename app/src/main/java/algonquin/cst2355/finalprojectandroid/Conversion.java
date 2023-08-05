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
   @ColumnInfo(name="conversionAmount")
    public String conversionAmount;
   @ColumnInfo(name="TimeSent")
   public String timeSemt;
   @ColumnInfo(name="convertedDetails")
   public String convertedDetails;

    @ColumnInfo(name = "currencyFrom")
    public String currencyFrom;

    @ColumnInfo(name = "currencyTo")
    public String currencyTo;
    public Conversion(String conversionResult, String timeSemt, String convertedDetails) {
        this.conversionAmount = conversionResult;
        this.timeSemt = timeSemt;
        this.convertedDetails = convertedDetails;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public Conversion(String conversionAmount, String timeSemt, String convertedDetails, String currencyFrom, String currencyTo) {
        this.conversionAmount = conversionAmount;
        this.timeSemt = timeSemt;
        this.convertedDetails = convertedDetails;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    public Conversion() {
    }


    protected Conversion(Parcel in) {
        id = in.readInt();
        conversionAmount = in.readString();
        timeSemt = in.readString();
        convertedDetails = in.readString();
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

    public String getConvertedDetails() {
        return convertedDetails;
    }
    public void setConversionDetails(String conversionDetails) {
        this.convertedDetails = convertedDetails;
    }

    public String getConversionAmount() {
        return conversionAmount;
    }

    public String getTimeSemt() {
        return timeSemt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(conversionAmount);
        dest.writeString(timeSemt);
    }

}
