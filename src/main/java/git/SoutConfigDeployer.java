package git;

import org.apache.log4j.Logger;
import remotefetcher.ConfigDeployer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SoutConfigDeployer implements ConfigDeployer {
    static final Logger logger = Logger.getLogger(SoutConfigDeployer.class);

    @Override
    public void deploy(InputStream reader) throws Exception{
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(reader,"UTF-8"));

            logger.info("Deploying to STDIO");

            String line;
            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
            }
        }catch (Exception e){
            throw new Exception("I/O Exception reading configuration");
        }
    }
}
