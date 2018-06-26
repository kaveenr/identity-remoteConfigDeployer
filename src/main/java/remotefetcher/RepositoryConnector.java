package remotefetcher;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface RepositoryConnector {
    public void fetchRepository() throws Exception;
    public InputStream getFile(File location) throws Exception;
    public Date getLastModified(File location) throws Exception;
    public String getFileHash(File location) throws Exception;
    public List<File> listFiles(File location) throws Exception;
}
