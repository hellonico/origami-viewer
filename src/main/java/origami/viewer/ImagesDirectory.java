package origami.viewer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ImagesDirectory {
    private final StringProperty directory = new SimpleStringProperty("");
    private final ListProperty<File> result = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    public final String getDirectory() {
        return directory.get();
    }

    public final void setDirectory(String value) {
        directory.set(value);
    }

    public void setResult(Collection<File> value) {
        result.setAll(value);
    }

    public ListProperty<File> resultProperty() {
        return result;
    }
}
