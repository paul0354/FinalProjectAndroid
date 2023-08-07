package algonquin.cst2355.finalprojectandroid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a bear image. Contains functionality to retrieve the bear image's
 * file name, width and height (in pixels), and size in bytes.
 */
@Entity
public class BearImage {
    /**
     * The id of the bear image.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    protected int id;

    /**
     * The file name of the bear image.
     */
    @ColumnInfo(name="imageName")
    protected String imageName;

    /**
     * The width of the bear image in pixels.
     */
    @ColumnInfo(name="width")
    protected int width;

    /**
     * The height of the bear image in pixels.
     */
    @ColumnInfo(name="height")
    protected int height;

    /**
     * The size of the bear image in bytes.
     */
    @ColumnInfo(name="byteSize")
    protected int byteSize;

    /**
     * Creates a new bear image using a provided file name, width, and
     * height. Establishes byteSize using a calculation involving
     * width and height.
     * @param imageName The file name for the bear image.
     * @param width The width of the bear image in pixels.
     * @param height The height of the bear image in pixels.
     */
    public BearImage(String imageName, int width, int height){
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.byteSize = ((width*height*32)/8);
    }

    /**
     * Retrieves the bear image's file name.
     * @return imageName The bear image's file name.
     */
    public String getImageName(){
        return imageName;
    }

    /**
     * Retrieves the bear image's width.
     * @return width The bear image's width in pixels.
     */
    public int getWidth(){
        return width;
    }

    /**
     * Retrieves the bear image's height.
     * @return height The bear image's height in pixels.
     */
    public int getHeight(){
        return height;
    }

    /**
     * Retrieves the bear image's size.
     * @return byteSize The bear image's size in bytes.
     */
    public int getByteSize() { return byteSize; }
}