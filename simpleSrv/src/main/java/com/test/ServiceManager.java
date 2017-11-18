package com.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServiceManager {
    private Logger logger = LogManager.getLogger(ServiceManager.class);

    private static ServiceManager ourInstance = new ServiceManager();

    public static ServiceManager getInstance() {
        return ourInstance;
    }

    private ServiceManager() {
        init();
    }

    private Map<String, Object> services = new HashMap();

    private void init() {
        String pathResource = Thread.currentThread().getContextClassLoader().getResource("server.properties").getPath();
        File configFile = new File(pathResource);
        try (FileReader reader = new FileReader(configFile)) {
            Properties props = new Properties();
            props.load(reader);

            Enumeration<?> e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = props.getProperty(key);
                try {
                    String className = ServiceManager.class.getPackage().getName() + "." + value;
                    Object newObject = Class.forName(className).newInstance();
                    services.put(key, newObject);
                } catch (InstantiationException ex) {
                    logger.error(ex.getMessage());
                } catch (IllegalAccessException ex) {
                    logger.error(ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    logger.error(ex.getMessage());
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public Object getService(String name) {
        return services.get(name);
    }

}
