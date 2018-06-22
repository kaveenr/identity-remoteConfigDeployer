import remotefetcher.RepositoryConnector;

import java.io.File;
import java.io.InputStream;

public class GitRepositoryConnector implements RepositoryConnector {

    private String uri = "";
    private String branch = "";

    public GitRepositoryConnector(String uri,String branch){
        this.branch = branch;
        this.uri = uri;
    }

    public void pullRepository() {

    }

    public InputStream getFile(File location) {
        return null;
    }
}
