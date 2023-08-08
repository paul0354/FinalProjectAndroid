package algonquin.cst2355.finalprojectandroid.data;

import java.io.File;

/**
 * An interface which mandates the implementation of functionality to save an image
 * from a provided file.
 * @author Julia Paulson
 * @version 1.0
 */
public interface ImgSaver {
    /**
     * A method which saves an image using a provided file.
     * @param f A provided file
     * @return A boolean result regarding whether the image was saved.
     */
    boolean saveImage(File f);
}
