package origami.viewer;


import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import origami.Filter;
import origami.FindFilters;
import origami.Origami;
import origami.OrigamiFX;
import origami.utils.FileWatcher;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewerController {

    private final ImagesCollector imagesCollector = new ImagesCollector();
    private ImagesDirectory model = new ImagesDirectory();
    private Timeline timeline;
    private AnimationTimer timer;
    private boolean slideShowOn = false;
    private DoubleProperty zoomProperty;

    @FXML
    private ResourceBundle resources;

    @FXML
    private ImageView imageView;

    @FXML
    private Button previousButton;

    @FXML
    private Button playButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button browseButton;

    @FXML
    private Label directoryLabel;

    @FXML
    private ListView<File> imagesList;

    @FXML
    private GridPane gridPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane imageHolder;

    @FXML
    private javafx.scene.control.TextField filterPath = new javafx.scene.control.TextField();
    private MyFileWatcher mywatcher;

    @FXML
    private void initialize() {
        filterPath.setText(System.getProperty("user.home") + "/Desktop/filters.edn");
        mywatcher = new MyFileWatcher(new File(filterPath.getText()));
        mywatcher.start();
        installKeyHandlers();
        initializeImagesList();
        imagesList.itemsProperty().bind(model.resultProperty());
    }

    @FXML
    public void browseButtonAction() throws IOException {
        imageView.setImage(null);
        zoomProperty = null;
        selectDirectory();
        if (model.getDirectory() != "") {
            updateDirectoryLabel();
            updateImagesList();
            enableButtons();
        }
    }

    @FXML
    public void nextButtonAction() {
        imagesList.getSelectionModel().selectNext();
    }

    @FXML
    public void playButtonAction() {
        if (!slideShowOn) {
            slideShowOn = true;
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                }
            };
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);

            Duration duration = Duration.millis(2000);

            EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    if (imagesList.getSelectionModel().getSelectedItem() != null && !imagesList.getSelectionModel().isSelected(imagesList.getItems().size() - 1)) {
                        imagesList.getSelectionModel().selectNext();
                    } else {
                        imagesList.getSelectionModel().selectFirst();
                    }
                }
            };
            KeyFrame keyFrame = new KeyFrame(duration, onFinished);
            timeline.getKeyFrames().add(keyFrame);

            timeline.play();
            timer.start();
            playButton.setText(resources.getString("button.stop"));
        } else {
            slideShowOn = false;
            timeline.stop();
            timer.stop();
            playButton.setText(resources.getString("button.play"));
        }
    }

    @FXML
    public void previousButtonAction() {
        imagesList.getSelectionModel().selectPrevious();
    }

    private void initializeZoom() {
        if (zoomProperty == null) {
            zoomProperty = new SimpleDoubleProperty(200);
            zoomProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable arg0) {
                    imageView.setFitWidth(zoomProperty.get() * 4);
                    imageView.setFitHeight(zoomProperty.get() * 3);
                }
            });

            scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    try {
                        if (event.getDeltaY() > 0) {
                            zoomProperty.set(zoomProperty.get() * 1.1);
                        } else if (event.getDeltaY() < 0) {
                            zoomProperty.set(zoomProperty.get() / 1.1);
                        }
                    } catch (NullPointerException e) {
                    }
                }
            });
        }
    }

    private void installKeyHandlers() {
        gridPane.setFocusTraversable(true);
        final EventHandler<KeyEvent> previousKeyHandler = new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    previousButtonAction();
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    nextButtonAction();
                    keyEvent.consume();
                }
            }
        };
        gridPane.setOnKeyPressed(previousKeyHandler);
    }

    private void enableButtons() {
        previousButton.setDisable(false);
        nextButton.setDisable(false);
        playButton.setDisable(false);
    }

    private void initializeImagesList() {
        imagesList.setCellFactory(listView -> new ListCell<File>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(80);
                imageView.setFitWidth(160);
                imageView.setPreserveRatio(true);
            }

            @Override
            public void updateItem(File image, boolean empty) {
                super.updateItem(image, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String path = image.getAbsolutePath();
                    imageView.setImage(OrigamiFX.magic(path));
                    setGraphic(imageView);
                }
            }
        });
        imagesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                if (newValue == null) {
                    return;
                }
                updateImage();

            }
        });
    }

    public void exportJPGButtonAction(ActionEvent actionEvent) {
        OrigamiFX.export(imagesList.getSelectionModel().getSelectedItem().getAbsolutePath(), filterPath.getText(), "jpg", null);
    }

    public void exportPNGButtonAction(ActionEvent actionEvent) {
        OrigamiFX.export(imagesList.getSelectionModel().getSelectedItem().getAbsolutePath(), filterPath.getText(), "png", null);
    }

    public void exportAll(ActionEvent actionEvent) {
        OrigamiFX.exportAll(imagesList.getSelectionModel().getSelectedItem().getAbsolutePath(), filterPath.getText(), "png");
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void openFilters(ActionEvent actionEvent) {
        try {
            File filterFile = new File(filterPath.getText());
            if (!filterFile.exists()) {
                executorService.execute(() -> {
                    FindFilters.generateEDNWithAllFilters(filterPath.getText());
                });
            }

            OrigamiFX.desktopOpenFile(filterPath.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFilterPath(KeyEvent actionEvent) {

        if (mywatcher != null) {
            mywatcher.stopThread();
        }
        mywatcher = new MyFileWatcher(new File(filterPath.getText()));
        mywatcher.start();
        updateImage();
    }

    public class MyFileWatcher extends FileWatcher {
        public MyFileWatcher(File watchFile) {
            super(watchFile);
        }

        @Override
        public void doOnChange() {
            updateImage();
        }

    }

    private void updateImage() {
        File vo = imagesList.getSelectionModel().getSelectedItem();
        if(vo == null) {
            // no image loaded
            return;
        }
        Filter f = Origami.StringToFilter(filterPath.getText());
        Image image = OrigamiFX.magic(vo.getAbsolutePath(), f);
        imageView.setImage(image);
        imageView.setPreserveRatio(true);
        initializeZoom();
    }

    private void updateImagesList() {
        Task<Collection<File>> getImagesTask = new Task<Collection<File>>() {

            @Override
            protected Collection<File> call() throws Exception {
                return imagesCollector.getImages(model.getDirectory());
            }

            @Override
            protected void succeeded() {
                model.setResult(getValue());
                imagesList.setVisible(true);
            }
        };
        new Thread(getImagesTask).start();
    }

    private void updateDirectoryLabel() {
        directoryLabel.setText(model.getDirectory());
        directoryLabel.setTooltip(new Tooltip(model.getDirectory()));
        directoryLabel.setVisible(true);
    }

    private void selectDirectory() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(".").getCanonicalFile());
        try {
            model.setDirectory(directoryChooser.showDialog(gridPane.getScene().getWindow()).getAbsolutePath());
        } catch (NullPointerException e) {
        }
    }
}
