package remotefetcher;

import java.io.File;
import java.io.InputStream;

public interface RepositoryConnector {
    public void pullRepository();
    public InputStream getFile(File location);
}
