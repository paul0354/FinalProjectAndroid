package algonquin.cst2355.finalprojectandroid.Trivia;

public class UserResultModel {

    int id;
    String uid;
    String name;
    String uresult;

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public int getId() {
        return id;
    }

    public String getUresult() {
        return uresult;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUresult(String uresult) {
        this.uresult = uresult;
    }
}
