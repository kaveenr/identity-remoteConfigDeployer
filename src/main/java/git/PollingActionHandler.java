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
        while (true) {
            this.directoryMap.forEach((file,deployer) -> {
                logger.info("Polling repository");
                Date currentRevision = null;
                Date newRevision = null;
                try {
                    currentRevision =  repo.getLastModified(file);
                } catch (Exception e){
                    logger.info("I/O Error reading attributes");
                    e.printStackTrace();
                }
                if (currentRevision != null){
                    try {
                        repo.fetchRepository();
                    } catch (Exception e){
                        logger.info("Error pulling repository");
                    }
                    try {
                        newRevision = repo.getLastModified(file);
                    } catch (Exception e){
                        logger.info("I/O Error reading attributes");
                    }

                    if(newRevision.after(currentRevision)) {
                        logger.info("Repository change detected");
                        try {
                            deployer.deploy(repo.getFile(file));
                        } catch (Exception e) {
                            logger.error("Error Deploying " + deployer.getClass());
                        }
                    }
                }
                try{
                    Thread.sleep(10000*60);
                }catch (InterruptedException e){
                    logger.info("Polling loop interuppted");
                }
            });
        }
    }
}
