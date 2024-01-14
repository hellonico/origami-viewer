package origami.viewer.dataProvider;

import java.util.Collection;

import origami.viewer.dataProvider.data.ImageVO;
import origami.viewer.dataProvider.impl.DataProviderImpl;

public interface DataProvider {
    DataProvider INSTANCE = new DataProviderImpl();
    
    Collection<ImageVO> getImages(String directoryPath);
}
