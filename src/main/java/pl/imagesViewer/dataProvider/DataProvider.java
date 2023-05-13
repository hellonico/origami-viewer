package pl.imagesViewer.dataProvider;

import java.util.Collection;

import pl.imagesViewer.dataProvider.data.ImageVO;
import pl.imagesViewer.dataProvider.impl.DataProviderImpl;

public interface DataProvider {
    DataProvider INSTANCE = new DataProviderImpl();
    
    Collection<ImageVO> getImages(String directoryPath);
}
