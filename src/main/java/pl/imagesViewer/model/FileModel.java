package pl.imagesViewer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import pl.imagesViewer.dataProvider.data.ImageVO;

public class FileModel {
    private final StringProperty directory = new SimpleStringProperty("");
    private final ListProperty<ImageVO> result = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    public final String getDirectory() {
        return directory.get();
    }

    public final void setDirectory(String value) {
        directory.set(value);
    }

    public StringProperty directoryProperty() {
        return directory;
    }

    public List<ImageVO> getResult() {
        return result.get();
    }

    public void setResult(Collection<ImageVO> value) {
        result.setAll(value);
    }

    public ListProperty<ImageVO> resultProperty() {
        return result;
    }
}
