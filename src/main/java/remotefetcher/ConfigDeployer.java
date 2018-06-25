package remotefetcher;

import java.io.File;
import java.io.InputStream;

public interface ConfigDeployer {
    public void deploy(InputStream reader);
}
