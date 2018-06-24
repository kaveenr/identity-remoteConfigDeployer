package github;

import org.apache.log4j.Logger;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
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
    public File repoPath;
    private Repository repo;

    public GitRepositoryConnector(String name, String uri,String branch){
        this.name = name;
        this.branch = branch;
        this.uri = uri;
        this.repoPath = new File(System.getProperty("java.io.tmpdir") + this.name);
        logger.info("Repository directory set to " + this.repoPath.getAbsolutePath());
    }

    private Repository cloneRepository() throws Exception {
        CloneCommand clone_request = Git.cloneRepository()
                .setURI(this.uri)
                .setDirectory(this.repoPath)
                .setBranchesToClone(Arrays.asList(branch))
                .setBranch(this.branch);
        try {
            return clone_request.call().getRepository();
        } catch (Exception e){
            throw new Exception("Clone Exception");
        }

    }

    private Repository getLocalRepository() throws Exception {
        FileRepositoryBuilder localBuilder = new FileRepositoryBuilder();
        return localBuilder.findGitDir(this.repoPath)
                .build();
    }

    private RevCommit getLastCommit(ObjectReader reader) throws Exception{
        RevWalk walk = new RevWalk(reader);
        ObjectId lastCommitId = this.repo.resolve(Constants.HEAD);
        return walk.parseCommit(lastCommitId);
    }

    public void pullRepository() throws Exception {
        if (this.repoPath.exists() && this.repoPath.isDirectory()){
            logger.info("Found local repository");
            this.repo = this.getLocalRepository();
        }else {
            logger.info("Cloning repository from remote");
            this.repo = this.cloneRepository();
        }
    }

    public InputStream getFile(File location) throws Exception{
        ObjectReader reader = this.repo.newObjectReader();
        RevCommit commit = this.getLastCommit(reader);

        try {
            TreeWalk treewalk = TreeWalk.forPath(this.repo,location.getPath(),commit.getTree());

            if (treewalk != null) {
                return reader.open(treewalk.getObjectId(0)).openStream();
            }
        } catch (Exception e ){
            throw new Exception("File checkout exception");
        }
        return null;
    }

    public Date getLastModified() throws Exception{
        try {
            ObjectReader reader = this.repo.newObjectReader();
            RevCommit commit = getLastCommit(reader);
            return new Date((long) commit.getCommitTime() * 1000); //UNIX timestamp to seconds
        }catch (Exception e){
            throw new Exception("Repository I/O exception");
        }
    }
}
