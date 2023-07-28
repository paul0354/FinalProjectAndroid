package algonquin.cst2355.finalprojectandroid;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Conversion.class}, version = 1)
public abstract class ConversionDatabase extends RoomDatabase {

     public abstract ConversionDAO cDAO();

}
