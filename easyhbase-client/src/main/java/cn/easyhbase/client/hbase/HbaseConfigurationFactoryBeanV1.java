/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.easyhbase.client.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Factory for creating HBase specific configuration. By default cleans up any connection
 * associated with the current configuration.
 *
 * @author Costin Leau
 */
public class HbaseConfigurationFactoryBeanV1 implements InitializingBean, FactoryBean<Configuration> {

    private Configuration configuration;
    private Configuration hadoopConfig;
    private Properties properties;
    private final String PRINCIPAL = "kerberos.principal";
    private final String KEYTAB_PATH = "keytab.file";
    private final String HBASE_SECURITY_AUTHENTICATION = "hbase.security.authentication";
    private final String HBASE_SECURITY_AUTHORIZATION = "hbase.security.authorization";

    /**
     * Sets the Hadoop configuration to use.
     *
     * @param configuration The configuration to set.
     */
    public void setConfiguration(Configuration configuration) {
        this.hadoopConfig = configuration;
    }

    /**
     * Sets the configuration properties.
     *
     * @param properties The properties to set.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void afterPropertiesSet() {
        configuration = (hadoopConfig != null ? HBaseConfiguration.create(hadoopConfig) :
                HBaseConfiguration.create());
        addProperties(configuration, properties);
//        configuration.addResource("hbase-site.xml");
        if (configuration.getBoolean(HBASE_SECURITY_AUTHORIZATION, false)
                && "kerberos".equals(configuration.get(HBASE_SECURITY_AUTHENTICATION))) {
            UserGroupInformation.setConfiguration(configuration);
            System.out.println("hbase.coprocessor.region.classes: " + configuration.get("hbase" +
                    ".coprocessor" +
                    ".region.classes"));
            System.out.println("hbase.coprocessor.master.classes: " + configuration.get("hbase" +
                    ".coprocessor.master.classes"));
            System.out.println("hbase.rpc.engine: " + configuration.get("hbase.rpc.engine"));
            System.out.println("hbase.rpc.protection: " + configuration.get("hbase.rpc" +
                    ".protection"));

            System.out.println("hbase.zookeeper.property.clientPort: " + configuration.get("hbase" +
                    ".zookeeper.property.clientPort"));
            System.out.println("hbase.zookeeper.quorum: " + configuration.get("hbase.zookeeper" +
                    ".quorum"));
            System.out.println("zookeeper.znode.parent: " + configuration.get("zookeeper.znode" +
                    ".parent"));

            try {
                UserGroupInformation.loginUserFromKeytab(configuration.get(PRINCIPAL),
                        configuration.get(KEYTAB_PATH));
                System.out.println("user: " + configuration.get(PRINCIPAL) + " login " +
                        "successfully," + " principal: " + configuration.get(KEYTAB_PATH));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds the specified properties to the given {@link Configuration} object.
     *
     * @param configuration configuration to manipulate. Should not be null.
     * @param properties    properties to add to the configuration. May be null.
     */
    private void addProperties(Configuration configuration, Properties properties) {
        Assert.notNull(configuration, "A non-null configuration is required");
        if (properties != null) {
            Enumeration<?> props = properties.propertyNames();
            while (props.hasMoreElements()) {
                String key = props.nextElement().toString();
                configuration.set(key, properties.getProperty(key));
            }
        }
    }

    public Configuration getObject() {
        return configuration;
    }

    public Class<? extends Configuration> getObjectType() {
        return (configuration != null ? configuration.getClass() : Configuration.class);
    }

    public boolean isSingleton() {
        return true;
    }
}