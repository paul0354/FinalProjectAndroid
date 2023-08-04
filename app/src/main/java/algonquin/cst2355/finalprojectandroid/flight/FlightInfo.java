package algonquin.cst2355.finalprojectandroid.flight;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FlightInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "flightNumber")
    String flightNum;

    @ColumnInfo(name = "destination")
    String destination;

    @ColumnInfo(name = "terminal")
    String terminal;

    @ColumnInfo(name = "gate")
    String gate;

    @ColumnInfo(name = "delay")
    String delay;

    public FlightInfo(){}

    public FlightInfo(String f, String d, String t, String g, String y){
        this.flightNum = f;
        this.destination = d;
        this.terminal = t;
        this.gate = g;
        this.delay = y;
    }

    long getId() {return id;}
    String getFlightNum() {return flightNum;}
    String getDestination() {return destination;}
    String getTerminal() {return terminal;}
    String getGate() {return gate;}
    String getDelay() {return delay;}

}
