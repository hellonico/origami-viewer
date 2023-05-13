package pl.imagesViewer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import origami.Filter;
import origami.Filters;
import origami.Origami;

import java.awt.image.BufferedImage;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_REDUCED_COLOR_2;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_REDUCED_COLOR_4;

public class Utils {

    public static Image magic(String path, Filter f) {
        Mat m = Imgcodecs.imread(path,IMREAD_REDUCED_COLOR_2);
        BufferedImage bi = Origami.matToBufferedImage(f.apply(m));
        return SwingFXUtils.toFXImage(bi,null);
    }

    public static Image magic(String path) {
        return magic(path, new Filters.NoOP());
    }

}
