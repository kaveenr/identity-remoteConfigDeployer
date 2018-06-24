import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import remotefetcher.RepositoryConnector;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

public class GitRepositoryConnector implements RepositoryConnector {

    private String uri = "";
    private String branch = "";
    private String name = "";
    private File repoPath;
    private Git git;

    public GitRepositoryConnector(String name, String uri,String branch){
        this.branch = branch;
        this.uri = uri;
        this.repoPath = new File("$TMPDIR/"+this.name);
    }

    public void pullRepository() throws Exception {
        this.cloneRepository(this.uri,this.branch);
    }

    private void cloneRepository(String uri, String master) throws Exception {
        CloneCommand clone_request = Git.cloneRepository()
                .setURI(this.uri)
                .setDirectory(this.repoPath)
                .setBranchesToClone(Arrays.asList(branch))
                .setBranch(this.branch);
        try {
            this.git = clone_request.call();
        } catch (Exception e){
            throw new Exception("Clone Exception");
        }

    }

    public InputStream getFile(File location) {
        return null;
    }

    public Date getLastModified() {
        return null;
    }
}
