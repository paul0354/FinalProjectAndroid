package algonquin.cst2355.finalprojectandroid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BearImage {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    protected int id;

    @ColumnInfo(name="imageName")
    protected String imageName;

    @ColumnInfo(name="width")
    protected int width;

    @ColumnInfo(name="height")
    protected int height;

    @ColumnInfo(name="wasCropped")
    protected boolean wasCropped;

    public BearImage(String imageName, int width, int height, boolean wasCropped){
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.wasCropped = wasCropped;
    }

    public String getImageName(){
        return imageName;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
}