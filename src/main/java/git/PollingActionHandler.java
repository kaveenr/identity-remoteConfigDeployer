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

    static final Logger logger = Logger.getLogger(PollingActionHandler.class);

    private RepositoryConnector repo;
    private Map<File, ConfigDeployer> directoryMap;
    private Integer frequency =  60;

    // Keeps last deployed revision date of each file
    private Map<File,Date> revisionDates = new HashMap<>();

    public PollingActionHandler(RepositoryConnector repo, Map<File, ConfigDeployer> directoryMap) {
        this.repo = repo;
        this.directoryMap = directoryMap;
    }

    public void main(){
        Thread t = new Thread(new PollingActionHandler(this.repo,this.directoryMap));
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.repo.fetchRepository();
            } catch (Exception e){
                logger.info("Error pulling repository");
            }

            this.directoryMap.forEach(this::pollDirectory);

            try{
                Integer seconds = this.frequency;
                logger.info("Sleeping for " + seconds +" seconds");
                Thread.sleep(1000 * (long) seconds);
            }catch (InterruptedException e){
                logger.info("Polling loop interrupted");
                Thread.currentThread().interrupt();
            }

            if (Thread.currentThread().isInterrupted()) break;
        }
    }

    private void pollDirectory(File path, ConfigDeployer deployer){
        logger.info("Polling Directory " + path.getPath() + " for changes");
        List<File> configFiles = null;

        try {
            configFiles = this.repo.listFiles(path);
        }catch (Exception e){
            e.printStackTrace();
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
                if (this.revisionDates.containsKey(file) && currentRevision != null
                        && this.revisionDates.get(file).before(currentRevision)){
                    logger.info("Deploying " + file.getPath());
                    try {
                        deployer.deploy(repo.getFile(file));
                    } catch (Exception e){
                        logger.info("Error Deploying "+ file.getName());
                    }
                }
                this.revisionDates.put(file,currentRevision);
            }
        }
    }
}
