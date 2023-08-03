package algonquin.cst2355.finalprojectandroid.Trivia;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    String BASE_URL = "https://opentdb.com/";

    @GET("api_category.php")
    Call<CategoryModel> FETCHALLCATEGORY();

    @GET("api.php")
    Call<QuestionModel> FetchQuestions(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("type") String type

    );

}
