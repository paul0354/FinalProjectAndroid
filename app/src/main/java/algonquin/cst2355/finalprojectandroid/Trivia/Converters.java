package algonquin.cst2355.finalprojectandroid.Trivia;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(list);
        return jsonArray.toString();
    }

    @TypeConverter
    public static List<String> toList(String listString) {
        if (listString == null) {
            return null;
        }

        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(listString);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
