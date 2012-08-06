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
package com.stormpath.beanpole.spring.context

import static org.easymock.EasyMock.*
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.ConfigurableEnvironment

/**
 * Unit tests for the {@link ActiveProfilesInitializer} implementation.
 */
class ActiveProfilesInitializerTest extends GroovyTestCase {

    void testDefault() {
        def initializer = new ActiveProfilesInitializer()
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithSystemPropertyPresent() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            String getSystemProperty(String key) {
                if (key == ActiveProfilesInitializer.ACTIVE_PROFILES_PROPERTY_NAME) {
                    return "bar"
                }
                return null
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithSystemPropertyAndNullBeanstalkProperties() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                return null;
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithSystemPropertyAndEmptyBeanstalkProperties() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                return [:]
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithSystemPropertyAndEmptyBeanstalkPropertyValue() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                def map = [:]
                map.put("spring.profiles.active"," ")
                return map
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithSystemPropertyAndNullBeanstalkPropertyValue() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                def map = [:]
                map.put("spring.profiles.active",null)
                return map
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)

        replay appCtx

        initializer.initialize(appCtx)

        verify appCtx
    }

    void testInitializeWithoutSystemPropertyAndBeanstalkPropertiesPresent() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                def map = [:]
                map.put("spring.profiles.active","bar")
                return map
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)
        def env = createMock(ConfigurableEnvironment)
        expect(appCtx.getEnvironment()).andReturn env

        //noinspection GroovyAssignabilityCheck
        env.setActiveProfiles(eq("bar"))

        replay appCtx, env

        initializer.initialize(appCtx)

        verify appCtx, env
    }

    void testInitializeWithoutSystemPropertyAndMultipleBeanstalkValues() {

        def initializer = new ActiveProfilesInitializer() {
            @Override
            protected Map<String, String> getBeanstalkEnvironmentProperties(ConfigurableApplicationContext applicationContext) {
                def map = [:]
                map.put("spring.profiles.active","foo|bar|baz")
                return map
            }
        }
        def appCtx = createMock(ConfigurableApplicationContext)
        def env = createMock(ConfigurableEnvironment)
        expect(appCtx.getEnvironment()).andReturn env

        env.setActiveProfiles("foo", "bar", "baz")

        replay appCtx, env

        initializer.initialize(appCtx)

        verify appCtx, env
    }
}
