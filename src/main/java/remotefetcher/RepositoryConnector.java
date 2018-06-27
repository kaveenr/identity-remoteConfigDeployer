package remotefetcher;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface RepositoryConnector {
    void fetchRepository() throws Exception;
    InputStream getFile(File location) throws Exception;
    Date getLastModified(File location) throws Exception;
    String getFileHash(File location) throws Exception;
    List<File> listFiles(File location) throws Exception;
}
