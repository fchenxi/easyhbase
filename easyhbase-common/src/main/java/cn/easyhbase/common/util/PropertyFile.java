package cn.easyhbase.common.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyFile {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFile.class);
    private static Properties dbProps;
    private static String confPath;

    private static synchronized void init_prop(String clientId4Path) {
        dbProps = new Properties();
        InputStream fileInputStream = null;
        try {
            if (isExist("/home/" + clientId4Path)) {
                confPath = "/home/" + (StringUtils.isNotBlank(clientId4Path) ? clientId4Path + File.separator : "");

                fileInputStream = new FileInputStream(confPath + "sms-email-config.properties");
            } else if (isExist("D:/" + clientId4Path)) {
                confPath = "D:/" + (StringUtils.isNotBlank(clientId4Path) ? clientId4Path + File.separator : "");

                fileInputStream = new FileInputStream(confPath + "sms-email-config.properties");
            } else {
                String path = PropertyFile.class.getClassLoader().getResource("conf").getPath();

                if (path == null) {
                    throw new NullPointerException("配置路径不存在,至少在以下路径需存在配置文件:\r\n" + "/home/" + "\r\n" + "D:/" + "\r\n" + PropertyFile.class.getClassLoader().getResource("") + "/conf/");
                }

                path = path.replaceAll("%20", " ") + "/";
                fileInputStream = new FileInputStream(path + "sms-email-config.properties");

                confPath = path;
            }
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
        }
        try {
            dbProps.load(fileInputStream);
            dbProps.put("log4j.properties.path", confPath + "log4j.properties");

            addProperties2SystemEnvironment(dbProps);
        } catch (IOException e) {
            logger.error("不能读取数据库配置文件, 请确保dts-client.properties在CLASSPATH指定的路径中!", e);
        }
    }

    public static Properties getProprties(String profile) {
        Properties props = new Properties();
        InputStream fileInputStream = null;
        try {
            if (isExist("/home/" + profile)) {
                confPath = "/home/" + (StringUtils.isNotBlank(profile) ? profile : "");

                fileInputStream = new FileInputStream(confPath);
            } else if (isExist("D:/" + profile)) {
                confPath = "D:/" + (StringUtils.isNotBlank(profile) ? profile : "");

                fileInputStream = new FileInputStream(confPath);
            } else {
                String path = PropertyFile.class.getClassLoader().getResource("conf").getPath();

                if (path == null) {
                    throw new NullPointerException("配置路径不存在,至少在以下路径需存在配置文件:\r\n" + "/home/" + "\r\n" + "D:/" + "\r\n" + PropertyFile.class.getClassLoader().getResource("") + "/conf/");
                }

                path = path.replaceAll("%20", " ") + "/";
                fileInputStream = new FileInputStream(path + profile);

                confPath = path;
            }
        } catch (FileNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
        }
        try {
            props.load(fileInputStream);
        } catch (IOException e) {
            logger.error("不能读取数据库配置文件, 请确保dts-client.properties在CLASSPATH指定的路径中!", e);
        }

        return props;
    }

    public static Properties getProps(String clientId4Path) {
        if (dbProps == null)
            init_prop(clientId4Path);
        return dbProps;
    }

    public static String getConfPath() {
        return confPath;
    }

    public static String getCurDateTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }

    public static boolean isExist(String filePath) {
        return new File(filePath).exists();
    }

    public static String getValue(String name, String defvalue) {
        String value = getProps("").getProperty(name);
        if ((value != null) && (value.length() > 0)) {
            return value;
        }
        return defvalue;
    }

    public static boolean getBoolValue(String name, boolean defvalue) {
        String value = getProps("").getProperty(name);
        if ("true".equals(value))
            return true;
        if ("false".equals(value)) {
            return false;
        }
        return defvalue;
    }

    public static long getLongValue(String name, long defvalue) {
        String value = getProps("").getProperty(name);
        if (value != null) {
            return Long.valueOf(value).longValue();
        }
        return defvalue;
    }

    private static void addProperties2SystemEnvironment(Properties properties) {
        Enumeration props = properties.keys();
        while (props.hasMoreElements()) {
            String key = (String) props.nextElement();
            System.setProperty(key, (String) properties.get(key));
        }
    }
}