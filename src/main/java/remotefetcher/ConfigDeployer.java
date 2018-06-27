package remotefetcher;

import java.io.InputStream;

public interface ConfigDeployer {
    void deploy(InputStream reader) throws Exception;
}
