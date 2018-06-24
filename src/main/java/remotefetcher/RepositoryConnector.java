package remotefetcher;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public interface RepositoryConnector {
    public void pullRepository() throws Exception;
    public InputStream getFile(File location) throws Exception;
    public Date getLastModified();
}
