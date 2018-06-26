package remotefetchDemo;

import git.GitRepositoryConnector;
import git.PollingActionHandler;
import git.SoutConfigDeployer;
import remotefetcher.ActionHandler;
import remotefetcher.ConfigDeployer;
import remotefetcher.RepositoryConnector;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class GitDemo {
    final static Logger logger = Logger.getLogger(GitDemo.class);

    public static void main(String[] params){
        RepositoryConnector repo = new GitRepositoryConnector("demo","https://EnigmaMaker@bitbucket.org/EnigmaMaker/is-git-import-demo.git","master");

        HashMap<File, ConfigDeployer> deployer_config = new HashMap<>();

        deployer_config.put(new File("service_providers/"), new SoutConfigDeployer());
        deployer_config.put(new File("identity_providers/"), new SoutConfigDeployer());

        ActionHandler pollingHandler = new PollingActionHandler(repo, deployer_config);
        pollingHandler.main();
    }
}
