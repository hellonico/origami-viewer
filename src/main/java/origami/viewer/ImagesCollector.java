package origami.viewer;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ImagesCollector {

    static final Collection<String> imagesExtensions = Arrays.asList("webp", "jpeg", "jpg", "png", "gif");

    public List<File> getImages(String directoryPath) {
        File[] files = new File(directoryPath).listFiles((dir, name) -> {
            for (String extension : imagesExtensions) {
                if (name.toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
            return false;
        });
        return Arrays.asList(files);
    }

}
