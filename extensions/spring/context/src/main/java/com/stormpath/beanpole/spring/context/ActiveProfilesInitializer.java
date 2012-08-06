/*
 * Copyright 2012 Stormpath
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.beanpole.spring.context;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.stormpath.beanpole.system.BeanstalkEnvironmentPropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import java.util.Map;

/**
 * {@code ApplicationContextInitializer} that will set the {@code ApplicationContext}'s
 * {@link org.springframework.core.env.ConfigurableEnvironment#setActiveProfiles(String...) active profiles} (as
 * <a href="http://blog.springsource.com/2011/02/11/spring-framework-3-1-m1-released/">described here</a>) based on
 * the environment properties discovered via a
 * {@link BeanstalkEnvironmentPropertiesFactory}.  This component is what allows triggering Spring's active profiles
 * based on Beanstalk's custom environment properties instead of the default Spring
 * {@code spring.profiles.active} system property (since the system properties set are limited to a select few in
 * Beanstalk by default).
 * <p/>
 * If the {@code BeanstalkEnvironmentPropertiesFactory} returns a value for the
 * {@code spring.profiles.active} property, that comma-delimited value will be split and the values will be used to
 * set the {@code ApplicationContextInitializer} that will set the {@code ApplicationContext}'s
 * {@link org.springframework.core.env.ConfigurableEnvironment#setActiveProfiles(String...) active profiles}.
 * <p/>
 * <h2>Webapp Usage</h2>
 * <ol>
 * <li>Define the following {@code context-param} in {@code web.xml}:
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;contextInitializerClasses&lt;/param-name&gt;
 *     &lt;param-value&gt;com.stormpath.beanpole.spring.context.ActiveProfilesInitializer&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * </li>
 * <li>Ensure that you've set the {@code spring.profiles.active=}<em>VALUES_LIST</em> key=value
 * pair in one of the Beanstalk environment properties
 * <a href="https://github.com/stormpath/beanpole/wiki/Environment-Properties">as described here</a>, where
 * <em>VALUES_LIST</em> is a <b>pipe-delimited</b> (i.e. single vertical bar) list of active profile names.
 * For example:
 * <pre>
 *     spring.profiles.active=profile1|profile2|...|profileN
 * </pre>
 * The value is pipe-delimited and not comma-delimited to avoid conflict: commas are already used to delimit entire
 * key=value pairs (key1=value1,key2=value2, etc).
 * </li>
 * </ol>
 *
 * @since 0.1
 */
public class ActiveProfilesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(ActiveProfilesInitializer.class);

    private static final Splitter PIPE_DELIMITED = Splitter.on("|").trimResults().omitEmptyStrings();

    private static final String ACTIVE_PROFILES_PROPERTY_NAME = AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        String key = ACTIVE_PROFILES_PROPERTY_NAME;

        String value = getSystemProperty(key);
        if (value != null) {
            log.info("The '{}' system property is already set and will be read by Spring as expected.  No active " +
                    "profiles will be set by this {} instance", key, ActiveProfilesInitializer.class.getName());
            return;
        }
        //value is null - no system property was set explicitly.  Now try the Beanstalk app environment properties:

        Map<String, String> props = getBeanstalkEnvironmentProperties(applicationContext);
        if (props == null || props.isEmpty()) {
            return;
        }

        value = props.get(key);
        if (value == null) {
            log.debug("No '{}' Beanstalk environment property set.  No Spring active profiles will be configured " +
                    "by this instance.", key);
            return;
        }

        Iterable<String> i = PIPE_DELIMITED.split(value);
        String[] values = Iterables.toArray(i, String.class);

        if (values == null || values.length <= 0) {
            return;
        }

        if (log.isDebugEnabled()) {
            String delimited = Joiner.on(',').skipNulls().join(values);
            log.debug("Applying discovered configured Spring active profiles: {}", delimited);
        }

        applicationContext.getEnvironment().setActiveProfiles(values);
    }

    protected String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * Returns the Beanstalk environment properties to inspect for Spring active profile names.  Implementation defaults
     * to returning the values returned by the {@link BeanstalkEnvironmentPropertiesFactory}, but can be overridden by
     * subclasses for custom behavior.
     *
     * @param applicationContext the {@code ApplicationContext} provided to the {@link #initialize} method - ignored
     *                           by this implementation but available to subclasses if necessary.
     * @return the Beanstalk environment properties to inspect for Spring active profile names.
     */
    protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
        return new BeanstalkEnvironmentPropertiesFactory().getInstance();
    }
}
