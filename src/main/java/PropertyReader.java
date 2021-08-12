import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Property reader that read .properties extension file to properties value
 */
public class PropertyReader {
    private static Properties properties = new Properties();
    private final static String PROPS_FILE = "src/main/resources/config.properties";



    public static Properties getProperties() {
        if (properties.isEmpty()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(PROPS_FILE), StandardCharsets.UTF_8)) {
                properties.load(reader);properties.isEmpty();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
