package com.huchonglin.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Main;

import javax.servlet.ServletConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author: hcl
 * @date: 2020/7/4 09:54
 */
public class WebUtil {
    private static final Logger log = LoggerFactory.getLogger(WebUtil.class);
    private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private static final String CLASSPATH = "classpath";

    private static String MVC_PROPERTIES_NAME;

    private static String projectPath;

    public static String getMvcPropertiesName(ServletConfig config) {
        if (MVC_PROPERTIES_NAME != null) {
            return MVC_PROPERTIES_NAME;
        }

        MVC_PROPERTIES_NAME = config.getInitParameter(CONTEXT_CONFIG_LOCATION);
        if (MVC_PROPERTIES_NAME.startsWith(CLASSPATH)) {
            MVC_PROPERTIES_NAME =
                MVC_PROPERTIES_NAME.substring(MVC_PROPERTIES_NAME.indexOf(":") + 1);
        }
        return MVC_PROPERTIES_NAME;
    }

    public static Properties loadProperties(String configLocation) {
        Properties properties = new Properties();
        InputStream is = ClassUtil.class.getClassLoader().getResourceAsStream(configLocation);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Test
    public void test() {
        URL resource = Main.class.getClassLoader().getResource("");
        System.out.println(resource.getPath());
    }

    public static String getProjectPath() {
        if (projectPath != null) {
            return projectPath;
        }
        URL resource = ClassUtil.class.getClassLoader().getResource("spring-mvc.properties");
        String path = resource.getPath();
        if(path.startsWith("file:" + File.separator)){
            path = path.replace("file:"+File.separator, "");
            System.out.println(path);
        }
        projectPath = path.substring(0, path.indexOf("target") - 1);
        return projectPath;

    }

    public static String resolveRequestUrl(String requestURI) {
        String targetURL = "";
        String[] splits = requestURI.split("/");
        for (int i = 0; i < splits.length; i++) {
            if (i > 1) {
                targetURL += "/" + splits[i];
            }
        }
        log.info("requestURL :" + targetURL);
        return targetURL;
    }
}
