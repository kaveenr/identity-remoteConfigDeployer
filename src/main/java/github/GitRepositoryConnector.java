package github;

import org.apache.log4j.Logger;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import org.eclipse.jgit.treewalk.TreeWalk;
import remotefetcher.RepositoryConnector;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

public class GitRepositoryConnector implements RepositoryConnector {

    final static Logger logger = Logger.getLogger(GitRepositoryConnector.class);

    private String uri = "";
    private String branch = "";
    private String name = "";
    private File repoPath;
    private Git git;

    public GitRepositoryConnector(String name, String uri,String branch){
        this.name = name;
        this.branch = branch;
        this.uri = uri;
        this.repoPath = new File(System.getProperty("java.io.tmpdir") + this.name);
        logger.info("REPO DIR " + this.repoPath.getAbsolutePath());
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

    public InputStream getFile(File location) throws Exception{
        ObjectReader reader = this.git.getRepository().newObjectReader();
        RevWalk walk = new RevWalk(reader);

        try {
            ObjectId lastCommitId = this.git.getRepository().resolve(Constants.HEAD);
            RevCommit commit = walk.parseCommit(lastCommitId);
            TreeWalk treewalk = TreeWalk.forPath(this.git.getRepository(),location.getPath(),commit.getTree());

            if (treewalk != null) {
                return reader.open(treewalk.getObjectId(0)).openStream();
            }
        } catch (Exception e ){
            throw new Exception("File Checkout Exception");
        }
        return null;
    }

    public Date getLastModified() {
        return null;
    }
}
