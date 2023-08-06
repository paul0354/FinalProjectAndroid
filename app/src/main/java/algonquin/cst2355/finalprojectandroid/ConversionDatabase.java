package algonquin.cst2355.finalprojectandroid;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
/**
 * ConversionDatabase is a Room database class that serves as the database holder for the Conversion objects.
 * It defines the entities and the version of the database. It also provides a DAO (Data Access Object) to
 * interact with the database. The class follows the Singleton pattern to ensure only one instance of the database
 * is created and used throughout the application.
 */
@Database(entities = {Conversion.class}, version = 2 , exportSchema = false)
public abstract class ConversionDatabase extends RoomDatabase {
     /**
      * The name of the database.
      */
     private static final String DATABASE_NAME = "conversion-database";
     /**
      * Returns the DAO (Data Access Object) for interacting with the Conversion objects in the database.
      *
      * @return The ConversionDAO object.
      */
     public abstract ConversionDAO cDAO();
     /**
      * The singleton instance of the ConversionDatabase class.
      */
     private static ConversionDatabase instance;
     /**
      * Creates and returns the singleton instance of the ConversionDatabase class.
      * If the instance is not yet created, it builds the database and returns the instance.
      * If the instance is already created, it returns the existing instance.
      *
      * @param context The application context used to create the database instance.
      * @return The singleton instance of ConversionDatabase.
      */
     public static synchronized ConversionDatabase getInstance(Context context) {
          if (instance == null) {
               instance = Room.databaseBuilder(context.getApplicationContext(),
                               ConversionDatabase.class, DATABASE_NAME)
                       .build();
          }
          return instance;
     }
}
