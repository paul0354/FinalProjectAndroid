package algonquin.cst2355.finalprojectandroid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Both the info displayed and the info saved in the database
 *
 * @author Owen Austin
 * @version 1.0
 */
@Entity
public class FlightInfo {

    /** ID of the flight, used only for the database */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    /** The flight number (with source airport code) */
    @ColumnInfo(name = "flightNumber")
    String flightNum;

    /** The destination airport code */
    @ColumnInfo(name = "destination")
    String destination;

    /** The terminal that you need to board at */
    @ColumnInfo(name = "terminal")
    String terminal;

    /** The gate that you need to board at */
    @ColumnInfo(name = "gate")
    String gate;

    /** The delay of the flight */
    @ColumnInfo(name = "delay")
    String delay;

    /**
     * No arg constructor
     */
    public FlightInfo(){}

    /**
     * Parameterized constructor
     * @param f Flight number
     * @param d Destination code
     * @param t Terminal
     * @param g Gate
     * @param y Delay
     */
    public FlightInfo(String f, String d, String t, String g, String y){
        this.flightNum = f;
        this.destination = d;
        this.terminal = t;
        this.gate = g;
        this.delay = y;
    }

    /**
     * Gets the id of the message
     * @return the id of the message
     */
    long getId() {return id;}

    /**
     * Gets the flight number
     * @return the flight number
     */
    String getFlightNum() {return flightNum;}

    /**
     * Gets the destination code
     * @return the destination code
     */
    String getDestination() {return destination;}

    /**
     * Gets the terminal
     * @return the terminal
     */
    String getTerminal() {return terminal;}

    /**
     * Gets the gate
     * @return the gate
     */
    String getGate() {return gate;}

    /**
     * Gets the delay
     * @return the delay
     */
    String getDelay() {return delay;}

}
