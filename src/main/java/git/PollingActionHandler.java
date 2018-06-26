package git;

import org.apache.log4j.Logger;
import remotefetcher.ActionHandler;
import remotefetcher.ConfigDeployer;
import remotefetcher.RepositoryConnector;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class PollingActionHandler implements ActionHandler, Runnable {

    final static Logger logger = Logger.getLogger(PollingActionHandler.class);

    private RepositoryConnector repo;
    private HashMap<File, ConfigDeployer> directoryMap;

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
            this.directoryMap.forEach((file,deployer) -> {
                this.pollDirectory(file,deployer);
            });
            try{
                Thread.sleep(1000 * (long) 60);
            }catch (InterruptedException e){
                logger.info("Polling loop interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void pollDirectory(File file, ConfigDeployer deployer){
        logger.info("Polling repository");
        Date currentRevision = null;
        try {
            repo.fetchRepository();
        } catch (Exception e){
            logger.info("Error pulling repository");
        }
        try {
            currentRevision = repo.getLastModified(file);
        } catch (Exception e){
            logger.info("I/O Error reading attributes");
        }
        if (revisionDates.containsKey(file) && currentRevision != null){
            if(revisionDates.get(file).before(currentRevision)) {
                logger.info("Repository change detected");
                try {
                    deployer.deploy(repo.getFile(file));
                } catch (Exception e) {
                    logger.info("Error Deploying " + deployer.getClass());
                }
                revisionDates.put(file,currentRevision);
            }else {
                logger.info("No changes detected");
            }
        }
    }
}
