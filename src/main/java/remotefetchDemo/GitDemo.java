package remotefetchDemo;

import github.GitRepositoryConnector;
import remotefetcher.RepositoryConnector;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GitDemo {
    final static Logger logger = Logger.getLogger(GitDemo.class);

    public static void main(String[] params){
        RepositoryConnector repo = new GitRepositoryConnector("areas","https://github.com/kaveenr/sri-lanka-district-area.json.git","master");
        File target = new File("locs.json");
        try {
            repo.pullRepository();
        } catch (Exception e){
            logger.error("Cloning Error");
        }

        InputStream reader;

        try {
            reader = repo.getFile(target);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(reader,"UTF-8"));

            String line;
            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
            }
        }catch (Exception e){
            logger.error("Repo Checkout Error");
        }
    }
}
