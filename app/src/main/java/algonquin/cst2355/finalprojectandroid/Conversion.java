package algonquin.cst2355.finalprojectandroid;
/**
 * Name : Chawki Moulayat
 * Section : 23S_CST2335_022
 * Project : Final Project
 * Function : Currency Converter
 *
 * @Author : Chawki Moulayat
 */
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * The Conversion class represents a conversion object that holds information related to a currency conversion.
 * It implements the Parcelable interface to allow easy transfer of objects between components.
 */
@Entity
public class Conversion implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    public int id;
   @ColumnInfo(name="conversionAmount")
    protected String conversionAmount;
   @ColumnInfo(name="TimeSent")
   protected String timeSemt;
   @ColumnInfo(name="convertedDetails")
   protected String convertedDetails;

    @ColumnInfo(name = "currencyFrom")
    protected String currencyFrom;

    @ColumnInfo(name = "currencyTo")
    protected String currencyTo;

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }
    /**
     * Constructs a new Conversion object with the given conversion amount, time sent, converted details, currency from, and currency to.
     *
     * @param conversionAmount The amount to be converted.
     * @param timeSemt         The time when the conversion was sent.
     * @param convertedDetails The details of the currency conversion.
     * @param currencyFrom     The source currency of the conversion.
     * @param currencyTo       The target currency of the conversion.
     */
    public Conversion(String conversionAmount, String timeSemt, String convertedDetails, String currencyFrom, String currencyTo) {
        this.conversionAmount = conversionAmount;
        this.timeSemt = timeSemt;
        this.convertedDetails = convertedDetails;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }
    /**
     * Default constructor for the Conversion class.
     */
    public Conversion() {
    }

    /**
     * Constructs a new Conversion object from a Parcel object.
     *
     * @param in The Parcel object containing the Conversion object data.
     */
    protected Conversion(Parcel in) {
        id = in.readInt();
        conversionAmount = in.readString();
        timeSemt = in.readString();
        convertedDetails = in.readString();
    }
    /**
     * Creator for the Conversion class, used to create new Conversion objects from a Parcel.
     */
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
    /**
     * Gets the converted details of the conversion.
     *
     * @return The details of the currency conversion.
     */
    public String getConvertedDetails() {
        return convertedDetails;
    }
    /**
     * Sets the converted details of the conversion.
     *
     * @param conversionDetails The details of the currency conversion to set.
     */
    public void setConversionDetails(String conversionDetails) {
        this.convertedDetails = convertedDetails;
    }
    /**
     * Gets the conversion amount.
     *
     * @return The amount to be converted.
     */
    public String getConversionAmount() {
        return conversionAmount;
    }
    /**
     * Gets the time when the conversion was sent.
     *
     * @return The time when the conversion was sent.
     */
    public String getTimeSemt() {
        return timeSemt;
    }
    /**
     * Implementing the Parcelable interface method for describing the contents of the Conversion object.
     *
     * @return An integer value (0 in this case).
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * Implementing the Parcelable interface method for writing the Conversion object data to a Parcel.
     *
     * @param dest  The Parcel to write the data to.
     * @param flags Additional flags about how the object should be written (not used in this case).
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(conversionAmount);
        dest.writeString(timeSemt);
    }

}
