package git;

import org.apache.log4j.Logger;
import remotefetcher.ConfigDeployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SoutConfigDeployer implements ConfigDeployer {
    static final Logger logger = Logger.getLogger(SoutConfigDeployer.class);

    @Override
    public void deploy(InputStream reader) throws Exception{
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(reader,"UTF-8"));
            logger.info("Deploying to STDIO");

            String line;
            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
            }
        }catch (IOException e){
            throw new IOException("Unable to read configuration file");
        }
    }
}
