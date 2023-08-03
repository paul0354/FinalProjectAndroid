package algonquin.cst2355.finalprojectandroid.Trivia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryModel {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("trivia_categories")
    private List<CategoryModel> triviaCategories;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryModel> getTriviaCategories() {
        return triviaCategories;
    }

    public void setTriviaCategories(List<CategoryModel> triviaCategories) {
        this.triviaCategories = triviaCategories;
    }
}
