package pl.imagesViewer.dataProvider.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import pl.imagesViewer.dataProvider.DataProvider;
import pl.imagesViewer.dataProvider.data.ImageVO;

public class DataProviderImpl implements DataProvider {

    private final Collection<String> imagesExtensions = Arrays.asList("jpg", "png", "gif");
    
    @Override
    public Collection<ImageVO> getImages(String directoryPath) {
        File[] files = new File(directoryPath).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                for (String extension : imagesExtensions) {
                    if (name.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                }
                return false;
            }
        });
        Function<File, ImageVO> fileToImageVO = file -> new ImageVO(directoryPath, file.getName());
        return Arrays.asList(files).stream().filter(File::isFile).map(fileToImageVO).collect(Collectors.toList());
    }

}
