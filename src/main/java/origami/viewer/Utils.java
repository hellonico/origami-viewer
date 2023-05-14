package origami.viewer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import origami.Filter;
import origami.Filters;
import origami.Origami;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_REDUCED_COLOR_2;

public class Utils {

    public static Image magic(String path, Filter f) {
        Mat m = Imgcodecs.imread(path, IMREAD_REDUCED_COLOR_2);
        BufferedImage bi = Origami.matToBufferedImage(f.apply(m));
        return SwingFXUtils.toFXImage(bi, null);
    }

    public static Image magic(String path) {
        return magic(path, new Filters.NoOP());
    }

    public static void export(String path, String filter, String format, String outputDir) {
        Filter f = Origami.StringToFilter(filter);
        File input = new File(path);
        Mat m = Imgcodecs.imread(input.getAbsolutePath());
        Mat out = f.apply(m);
        String output = outputDir == null ? input.getParent() : outputDir;
        Imgcodecs.imwrite( output+ "/"+input.getName()+"." + format, out);
    }

    public static void exportAll(String path, String filter, String format) {
        File input = new File(path).getParentFile();
        File outputDir = new File(input+"/origami");
        outputDir.mkdirs();
        FilenameFilter f = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("png") || name.contains("jpg");
            }
        };
        for (File file : input.listFiles(f)) {
            export(file.getAbsolutePath(), filter, format, outputDir.getAbsolutePath());
        }
    }
}