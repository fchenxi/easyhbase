package cn.easyhbase.client.hbase;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class HbaseConfigurationFactoryBean
        implements InitializingBean, FactoryBean<Configuration> {
    private Configuration configuration;
    private Configuration hadoopConfig;
    private Properties properties;
    private final String PRINCIPAL = "kerberos.principal";
    private final String KEYTAB_PATH = "keytab.file";
    private final String HBASE_SECURITY_AUTHENTICATION = "hbase.security.authentication";

    public void setConfiguration(Configuration configuration) {
        this.hadoopConfig = configuration;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void afterPropertiesSet() {
        this.configuration = (this.hadoopConfig != null ? HBaseConfiguration.create(this
                .hadoopConfig) : HBaseConfiguration.create());
        addProperties(this.configuration, this.properties);
        if (("kerberos".equalsIgnoreCase(this.configuration.get(HBASE_SECURITY_AUTHENTICATION)))) {
            UserGroupInformation.setConfiguration(this.configuration);
            try {
                UserGroupInformation.loginUserFromKeytab(this.configuration.get("kerberos" +
                        ".principal"), this.configuration.get("keytab.file"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addProperties(Configuration configuration, Properties properties) {
        Assert.notNull(configuration, "A non-null configuration is required");
        if (properties != null) {
            Enumeration props = properties.propertyNames();
            while (props.hasMoreElements()) {
                String key = props.nextElement().toString();
                configuration.set(key, properties.getProperty(key));
            }
        }
    }

    public Configuration getObject() {
        return this.configuration;
    }

    public Class<? extends Configuration> getObjectType() {
        return this.configuration != null ? this.configuration.getClass() : Configuration.class;
    }

    public boolean isSingleton() {
        return true;
    }
}