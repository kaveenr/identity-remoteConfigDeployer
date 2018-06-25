package git;

import org.apache.log4j.Logger;
import remotefetcher.ConfigDeployer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SoutConfigDeployer implements ConfigDeployer {
    final static Logger logger = Logger.getLogger(SoutConfigDeployer.class);

    @Override
    public void deploy(InputStream reader) {
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(reader,"UTF-8"));

            String line;
            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
            }
        }catch (Exception e){
            logger.error("I/O Exception reading configuration");
        }
    }
}
