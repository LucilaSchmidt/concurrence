package com.despegar.university.exercises.concurrence.domain.configurations;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ConfigurationsTest {

    private static final String IC_PATH = "/support/ic/conf/env";

    private static final String ENV_PATH = "/environment.properties";

    private String basePath;

    private Set<Object> keys = Sets.newHashSet();

    @DataProvider
    public Object[][] getData() throws IOException {

        File file = new File(".");
        this.basePath = file.getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath();

        Properties devProperties = new Properties();
        Properties icProperties = new Properties();

        devProperties.load(this.getClass().getClassLoader().getResourceAsStream("conf/env" + ENV_PATH));

        icProperties.load(new FileInputStream(this.basePath + IC_PATH + ENV_PATH));

        this.keys.addAll(devProperties.keySet());
        this.keys.addAll(icProperties.keySet());

        return new Object[][] { {devProperties, "dev"}, {icProperties, "ic"}};
    }

    @Test(dataProvider = "getData")
    public void configurationKeys_test(Properties props, String env) {

        System.out.println("---------- Starting with new file ------------");
        System.out.println(String.format("Environment: %s", env.toUpperCase()));

        List<String> missingConfigurableValues = Lists.newArrayList();

        for (Object key : this.keys) {
            String keyString = (String) key;
            System.out.println(key);
            if (props.getProperty(keyString) == null) {
                missingConfigurableValues.add(keyString);
            }
        }

        if (!isEmpty(missingConfigurableValues)) {
            System.out.println("---------- missing values ------------");

            for (String configurableValue : missingConfigurableValues) {
                System.out.println(String.format("missing configurable value: %s", configurableValue));
            }
        }

        System.out.println(String.format(" END Environment: %s", env.toUpperCase()));

        assertTrue(CollectionUtils.isEmpty(missingConfigurableValues), "there are missing configurable values");
    }

}
