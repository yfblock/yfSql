package io.github.yfblock.yfSql.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static Properties properties = new Properties();
    public static InputStream bufferedReader = Config.class.getResourceAsStream("/application.properties");
    public static String getConfig(String key) {
        try {
//            if (bufferedReader == null) return null;
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties.getProperty(key);
    }

    public static String getConfigOrDefault(String key, String defaultValue) {
        String value = Config.getConfig(key);
        if(value==null) return defaultValue;
        return value;
    }
}
