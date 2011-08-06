/*
 * Copyright 2011 Katasoft
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
package com.katasoft.beanpole.system;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.katasoft.beanpole.util.Factory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@code BeanstalkEnvironmentPropertiesFactory} will convert all of the Beanstalk-specific custom environment
 * properties into a Map of key-value pairs so the application does not need to be aware of the
 * environment property names.  For example, given the following AWS Beanstalk Custom Environment Properties
 * (passed as System Properties to the JVM):
 * <table>
 *     <thead>
 *         <tr>
 *             <th>System Property</th>
 *             <th>System Property Value</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>PARAM1</td>
 *             <td>key1 = value1, key2 = value2</td>
 *         </tr>
 *         <tr>
 *             <td>PARAM2</td>
 *             <td>key3 = value3, key4 = value4</td>
 *         </tr>
 *         <tr>
 *             <td>PARAM3</td>
 *             <td>key5 = value5, key6 = value6</td>
 *         </tr>
 *     </tbody>
 * </table>
 * This will result in a map returned with the following entries and corresponding values:
 * <table>
 *     <thead>
 *         <tr>
 *             <th>Invocation</th>
 *             <th>Return Value</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>map.get("PARAM1")</td>
 *             <td>key1 = value1, key2 = value2</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("PARAM2")</td>
 *             <td>key3 = value3, key4 = value4</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("PARAM3")</td>
 *             <td>key5 = value5, key6 = value6</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key1")</td>
 *             <td>value1</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key2")</td>
 *             <td>value2</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key3")</td>
 *             <td>value3</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key4")</td>
 *             <td>value4</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key5")</td>
 *             <td>value5</td>
 *         </tr>
 *         <tr>
 *             <td>map.get("key6")</td>
 *             <td>value6</td>
 *         </tr>
 *     </tbody>
 * </table>
 * As you can see, the original System property values are maintained (PARAM1, PARAM2, PARAM3), but each PARAM property
 * that was a comma-delimited list of key=value pairs was parsed and each key=value pair was added to the map
 * individually.
 * <p/>
 * This is beneficial because:
 * <ol>
 *     <li>The application can receive as many key=value properties as it needs and never worry about the fact that
 *     Beanstalk only exposes 5 custom system properties (PARAM1 - PARAM5)</li>
 *     <li>The application never needs to be aware of the AWS-specific system property names and will not be
 *     affected by any related name changes (it only needs to reference 'key1', 'key2', etc as necessary).</li>
 * </ol>
 *
 * @since 0.1
 */
public class BeanstalkEnvironmentPropertiesFactory implements Factory<Map<String,String>> {

    private static final Splitter COMMA_DELIMITED = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Splitter EQUALS_DELIMITED = Splitter.on("=").trimResults().omitEmptyStrings();

    @Override
    public Map<String, String> getInstance() {
        return getBeanstalkProperties();
    }

    public Map<String, String> getBeanstalkProperties() {

        Map<String, String> props = new LinkedHashMap<String, String>();

        for (BeanstalkEnvironmentProperty prop : BeanstalkEnvironmentProperty.values()) {
            String name = prop.name();
            String val = getSystemPropVal(name);
            if (val != null) {
                props.put(name, val);
            }

            if (val != null && name.startsWith(BeanstalkEnvironmentProperty.CUSTOM_PARAM_NAME_PREFIX)) {
                //see if we can split it up into properties based on comma-delimited values
                //prop1=val1, prop2=val2, ..., propN=valN

                Map<String,String> embeddedProps = splitToProperties(val);
                if (embeddedProps != null && !embeddedProps.isEmpty()) {
                    props.putAll(embeddedProps);
                }
            }
        }

        return props;
    }

    private Map<String,String> splitToProperties(String commaDelimited) {

        Map<String,String> props = new LinkedHashMap<String, String>();

        Iterable<String> keypairs = COMMA_DELIMITED.split(commaDelimited);
        for(String keypair : keypairs) {

            Iterable<String> split = EQUALS_DELIMITED.split(keypair);

            int i = 0;
            String name = null;
            String value = null;

            for(String s : split) {
                if (i == 0) {
                    name = s;
                } else if (i == 1) {
                    value = s;
                }
                i++;
            }

            //only treat the comma-delimited value as a name/value pair if we could split evenly on the equals-sign.
            if (i == 2 && name != null && value != null) {
                props.put(name, value);
            }
        }

        return props;
    }

    private String getSystemPropVal(String name) {
        String val = getSystemProperty(name);
        if (val != null) {
            val = Strings.emptyToNull(CharMatcher.WHITESPACE.trimFrom(val));
        }
        return val;
    }

    /**
     * Not intended to be used outside of the framework - used merely for mock/testing overrides.
     */
    @SuppressWarnings({"JavaDoc"})
    protected String getSystemProperty(String name) {
        return System.getProperty(name);
    }

}
