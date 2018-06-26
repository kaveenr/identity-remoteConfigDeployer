package git;

import org.apache.log4j.Logger;
import remotefetcher.ActionHandler;
import remotefetcher.ConfigDeployer;
import remotefetcher.RepositoryConnector;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollingActionHandler implements ActionHandler, Runnable {

    final static Logger logger = Logger.getLogger(PollingActionHandler.class);

    private RepositoryConnector repo;
    private HashMap<File, ConfigDeployer> directoryMap;
    private Map<File,Date> revisionDate = new HashMap<>();

    // Keeps last deployed revision date of each file
    private HashMap<File,Date> revisionDates = new HashMap<>();

    public PollingActionHandler(RepositoryConnector repo, HashMap<File, ConfigDeployer> directoryMap) {
        this.repo = repo;
        this.directoryMap = directoryMap;
    }

    public void main(){
        Thread t = new Thread(new PollingActionHandler(this.repo,this.directoryMap));
        t.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.repo.fetchRepository();
            } catch (Exception e){
                logger.info("Error pulling repository");
            }
            this.directoryMap.forEach((directory,deployer) -> {
                this.pollDirectory(directory,deployer);
            });
            try{
                Thread.sleep(1000 * (long) 60);
            }catch (InterruptedException e){
                logger.info("Polling loop interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void pollDirectory(File path, ConfigDeployer deployer){
        logger.info("Polling Directory " + path.getPath() + " for changes");
        List<File> configFiles = null;

        try {
            configFiles = this.repo.listFiles(path);
        }catch (Exception e){
            logger.info("Error listing files in root");
        }

        if (configFiles != null){
            for (File file: configFiles) {
                Date currentRevision = null;
                try {
                    currentRevision = this.repo.getLastModified(file);
                }catch (Exception e){
                    logger.info("Unable to read modify date of " + path.getPath());
                }
                if (this.revisionDate.containsKey(file) && currentRevision != null){
                    if (this.revisionDate.get(file).before(currentRevision) || true){
                        logger.info("Deploying " + file.getPath());
                        try {
                            deployer.deploy(repo.getFile(file));
                        } catch (Exception e){
                            logger.info("Error Deploying "+ file.getName());
                        }
                    }
                }
                this.revisionDate.put(file,currentRevision);
            }
        }
    }
}
