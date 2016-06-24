package com.polycom.sqa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private Properties p;

    public ConfigUtil() {
        try {
            File file = new File("sysconfig.properties");
            InputStream inputStream = new FileInputStream(file.getAbsolutePath());

            p = new Properties();

            p.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("host:" + p.getProperty("host") + ",port:" + p.getProperty("port"));

    }

    public String getString(String key) {
        String result = p.getProperty(key);
        return result;
    }
}
