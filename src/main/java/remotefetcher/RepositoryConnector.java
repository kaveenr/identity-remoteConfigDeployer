package remotefetcher;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public interface RepositoryConnector {
    public void fetchRepository() throws Exception;
    public InputStream getFile(File location) throws Exception;
    public Date getLastModified(File location) throws Exception;
}
