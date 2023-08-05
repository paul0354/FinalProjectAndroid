package algonquin.cst2355.finalprojectandroid;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

@Database(entities = {Conversion.class}, version = 2 , exportSchema = false)
public abstract class ConversionDatabase extends RoomDatabase {

     private static final String DATABASE_NAME = "conversion-database";

     public abstract ConversionDAO cDAO();
     private static ConversionDatabase instance;

     public static synchronized ConversionDatabase getInstance(Context context) {
          if (instance == null) {
               instance = Room.databaseBuilder(context.getApplicationContext(),
                               ConversionDatabase.class, DATABASE_NAME)
                       .build();
          }
          return instance;
     }
}
