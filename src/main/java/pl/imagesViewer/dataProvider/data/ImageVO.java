package pl.imagesViewer.dataProvider.data;

import java.io.File;

public class ImageVO {
    
    private String directory;
    private String name;

    public ImageVO(String directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    public final String getDirectory() {
        return directory;
    }

    public final void setDirectory(String directory) {
        this.directory = directory;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return getDirectory() + File.separator + getName();
    }
}
