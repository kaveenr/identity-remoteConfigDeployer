package demo;

import git.GitRepositoryConnector;
import git.PollingActionHandler;
import git.SoutConfigDeployer;
import remotefetcher.ActionHandler;
import remotefetcher.ConfigDeployer;
import remotefetcher.RepositoryConnector;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class GitDemo {
    static final Logger logger = Logger.getLogger(GitDemo.class);

    public static void main(String[] params){
        RepositoryConnector repo = new GitRepositoryConnector("demo","https://EnigmaMaker@bitbucket.org/EnigmaMaker/is-git-import-demo.git","master");

        HashMap<File, ConfigDeployer> deployerConfig = new HashMap<>();

        deployerConfig.put(new File("service_providers/"), new SoutConfigDeployer());
        deployerConfig.put(new File("identity_providers/"), new SoutConfigDeployer());

        ActionHandler pollingHandler = new PollingActionHandler(repo, deployerConfig);
        pollingHandler.main();
    }
}
